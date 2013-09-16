%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%		SOCKET POOL TCP连接池
%%% 
%%%
%%% 
%%%
%%%
%%% Created:2010-01-19
%%% Example:
%%%     elib_socket_pool:start(),
%%%     case elib_socket_pool:connect("10.5.57.131", 2009, [{keepalive, Boolean}, {packet, 4}]) of
%%%         {ok, Socket} ->
%%%             case gen_tcp:send(Socket, <<"hello">>) of
%%%                 ok ->
%%%                     case gen_tcp:recv(Socket, 0) of
%%%                         {ok, B} ->
%%%                             elib_socket_pool:disconnect(Socket),
%%%                             {ok, B};
%%%                         {error, RecvReason} ->
%%%                             elib_socket_pool:close_connect(Socket),
%%%                             {error, RecvReason}
%%%                     end;
%%%                 {error, SendReason} ->
%%%                     elib_socket_pool:close_connect(Socket),
%%%                     {error, SendReason}
%%%             end;
%%%         {error, ConnectReason} ->
%%%             {error, ConnectReason}
%%%     end.
%%%   
%%% -------------------------------------------------------------------
-module(elib_socket_pool).

%% 缺省连接超时时间 单位:ms
-define(DEFAULT_CONNECT_TIMEOUT, 1000).
%% 缺省租用超时时间 单位:ms 
-define(DEFAULT_USE_TIMEOUT, 1000).
%% 缺省最大连接数
-define(DEFAULT_MAX_CONNECTS, 1000). 
%% 缺省单个IP最大连接数
-define(DEFAULT_PER_IP_MAX_CONNECTS, 10).
%% 缺省单个IP最小连接数
-define(DEFAULT_PER_IP_MIN_CONNECTS, 10).

-define(GET_LIST(KEY), case get(KEY) of undefined -> []; List -> List end).



%% 配置记录
-record(conf, {
            max_connects = ?DEFAULT_MAX_CONNECTS,      %%连接池最大连接数
            per_ip_max_connects = ?DEFAULT_PER_IP_MAX_CONNECTS,   %%单个IP最大连接数
            per_ip_min_connects = ?DEFAULT_PER_IP_MIN_CONNECTS,   %%单个IP最小连接数
            cur_connects = 0,   %%当前连接总数
            busy_count = 0,     %%报告busy次数
            max_connect_time = 0,  %%最大连接用时.单位:ms
            max_use_time = 0    %%最大使用用时.单位:ms
        }).

%% 单个SOCKET 记录
-record(item, {
            ip = undefined,         %% IP地址
            port = 0,               %% 端口号
            pid = undefined,        %% 当前使用该SOCKET的进程PID, 如没有使用为undefined
            state = wait_connect,   %% [wait_connect | idile | busy] [等待连接 | 空闲 | 忙]
            socket = undefined,     %% SOCKET句柄
            create_time = undefined,        %% 建立时间
            last_use_time = undefined,      %% 最后一次使用时间
            connect_timeout = undefined    %% 连接超时时间 单位:ms
        }).


-export([start/0, start/1, start/2, stop/0, stop/1, status/0, status/1]).
-export([connect/2, connect/3, connect/4, disconnect/1, disconnect/2, close_connect/1, close_connect/2]).
-export([timestamp/0]).


%% --------------------------------------------------------------------
%% Function: start/0, start/1, start/2
%% Description: 
%% Params:	
%%          A_name  连接池名字 例如:sppool
%%          L_options 参数列表 [{max_connects, Int()}, {per_ip_max_connects, Int()},  {per_ip_min_connects, Int()}]
%% Returns:	{ok, Pid} 	|
%%			{error, Reason}
%% --------------------------------------------------------------------    
start()->
    start([]).

start(L_options)->
    start(?MODULE, L_options).

start(A_name, L_options)->
    R = start_init(L_options, #conf{}),
    case whereis(A_name) of
        undefined->
            Pid = spawn_link(fun()-> loop(#conf{}) end),
            register(A_name, Pid),
            case rpc(A_name, {start, R}) of
                ok -> 
                    {ok, Pid};
                Error -> 
                    Error
            end;
        P_old ->
            {ok, P_old}        
    end.

start_init([], R)->
    R;
start_init([{max_connects, I} | T], R)->
    start_init(T, R#conf{max_connects = I});
start_init([{per_ip_max_connects, I} | T], R)->
    start_init(T, R#conf{per_ip_max_connects = I});
start_init([{per_ip_min_connects, I} | T], R)->
    start_init(T, R#conf{per_ip_min_connects = I});
start_init([ _OtherUnknow | T], R)->    
    start_init(T, R).


%% --------------------------------------------------------------------
%% Function: stop/0, stop/1
%% Description: 
%% Params:	
%% Returns:	ok 					|
%%			{error, not_start}
%% --------------------------------------------------------------------
stop()->
    stop(?MODULE).

stop(A_name)->
    case rpc(A_name, stop) of
        ok ->
            catch(erlang:unregister(A_name)),
            ok;
        Error ->
            Error
    end.


%% --------------------------------------------------------------------
%% Function: status/0, status/1
%% Description: 
%% Params:	
%% Returns:	{ok, List_status}			
%% --------------------------------------------------------------------       
status()->
    status(?MODULE).

status(A_name)->
    rpc(A_name, status).



%% --------------------------------------------------------------------
%% Function: connect/2, connect/3, connect/4
%% Description: 
%%      根据IP和端口号从连接池中获得一个可用的TCP SOCKET.如果没有则尝试建立个新的
%% Params:	A_name 连接池名称
%%          S_ip        目标主机IP地址
%%          I_port      目标主机的端口号
%%          L_options   [{timeout, Int()}, {tcp_options, List}] 
%%                      timeout 连接超时时间 
%%                      List TCP连接参数 注意不能有{action, true}
%%          I_time_out  连接超时时间 单位:ms 默认1秒
%% Returns:	{ok, Socket}		|
%%			{error, pool_busy}  | 忙,没有空闲的SOCKET,且现有SOCKET数已到达上线
%%          {error, connect_time_out}   | 连接超时
%%			{error, Reason}
%% --------------------------------------------------------------------   
connect(S_ip, I_port) ->
    connect(?MODULE, S_ip, I_port, []).

connect(S_ip, I_port, L_options) when is_list(L_options)->
    connect(?MODULE, S_ip, I_port, L_options);
connect(A_name, S_ip, I_port)->
    connect(A_name,  S_ip, I_port, []).

connect(A_name, S_ip, I_port, L_options)->
    I_timeout = proplists:get_value(timeout, L_options, ?DEFAULT_CONNECT_TIMEOUT),
    case do_connect(A_name, S_ip, I_port, L_options) of
        {error, pool_busy} ->
            %%如果是pool忙,则等待一段时间,再试一次
            timer:sleep(I_timeout),
            do_connect(A_name, S_ip, I_port, L_options);
        Other->
            Other
    end.
    

do_connect(A_name, S_ip, I_port, L_options)->
    L_tcp_options = proplists:get_value(tcp_options, L_options, []),
    I_timeout = proplists:get_value(timeout, L_options, ?DEFAULT_CONNECT_TIMEOUT),
    %% 当前时间单位:ms, 取时间操作比较耗时, 让应用来解决.和实际时间有6微秒延迟, 没有影响
    I_now_time = timestamp(),
    case rpc(A_name, {connect, S_ip, I_port, I_timeout, I_now_time}) of
        {ok, Socket} ->
            {ok, Socket};
        {error, pool_busy} ->
            {error, pool_busy};
        {error, no_idle_connect} ->
            case gen_tcp:connect(S_ip, I_port, L_tcp_options, I_timeout) of
                {ok, Socket} ->
                 %% 把控制权转移到控制进程
                 gen_tcp:controlling_process(Socket, whereis(A_name)),
                 case rpc(A_name, {connect_ok, S_ip, I_port, Socket}) of
                        ok ->
                            {ok, Socket}
                    end;
                {error, Reason} ->
                    {error, Reason}
            end;
        {error, not_start} ->
            {error, not_start}
    end.

%% --------------------------------------------------------------------
%% Function: disconnect/1, disconnect/2
%% Description: 
%%      把使用过的SOCKET返回到连接池
%%      !!如果应用使用中出现任何意外和错误, 应该使用close_connect
%% Params:	Socket  	
%% Returns:	ok              |
%%			{error, Reason}
%% --------------------------------------------------------------------       
disconnect(Socket)->
    disconnect(?MODULE, Socket).

disconnect(A_name, Socket)->
    rpc(A_name, {disconnect, Socket}).


%% --------------------------------------------------------------------
%% Function: close_connect/1, close_connect/2
%% Description: 
%% Params:	Socket
%% Returns:	ok 					|
%%			{error, not_start}
%%			{error, Reason}
%% --------------------------------------------------------------------       
close_connect(Socket)->
    close_connect(?MODULE, Socket).

close_connect(A_name, Socket) ->
    rpc(A_name, {close_connect, Socket}).

%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------   
loop(R)->
    receive
        {P_caller, {start, R_new} } ->
            reply(P_caller, ok),                
            loop(R_new);

        {P_caller, {connect, S_ip, I_port, I_timeout, I_now_time} } ->
          
            L_old = ?GET_LIST({S_ip, I_port}),
            case get_idle_connect(L_old, I_now_time, 0) of
                {ok, Socket} -> 
                    %% 有空闲连接
                    update_connect(Socket, [{state, busy}, {pid, P_caller}, {last_use_time, timestamp()}]),
                    reply(P_caller, {ok, Socket});    
                {not_found, I_current_connect_count} ->
                    case (I_current_connect_count >= R#conf.per_ip_max_connects) of
                        true -> 
                            %% 无空闲连接, 且连接数已到上限
                            reply(P_caller, {error, pool_busy});
                        false -> 
                            %% 无空闲连接, 且允许建立新连接
                            %% 先建立个连接记录, 并把socket属性置为调用者PID, 状态置为wait_connect
                            reply(P_caller,{error, no_idle_connect}),
                            %% 先发消息后建记录目的:是使create_time晚于应用开始连接时间, 防止pool提前删除该连接的记录
                            put_connect(S_ip, I_port, P_caller, wait_connect, I_timeout)
                    end
            end,                
            loop(R);            

        {P_caller, {connect_ok, S_ip, I_port, Socket} } ->

            %% 应用建立新连接成功后,先删除上步的临时记录置
            del_connect(P_caller),
            %% 新增一条socket记录并置busy                
            put_connect(S_ip, I_port, Socket, busy),
            update_connect(Socket, [{state, busy}, {pid, P_caller}, {last_use_time, timestamp()}]),
            reply(P_caller, ok),
            loop(R);

        {P_caller, {disconnect, Socket} }->

            update_connect(Socket, [{state, idle}, {pid, undefined}, {last_use_time, undefined}]),
            reply(P_caller, ok),
            loop(R);

        {P_caller, {close_connect, Socket} }->

            del_connect(Socket),
            reply(P_caller, ok),                
            loop(R);

        {P_caller, status} ->
            L = get(),
            L_sockets = lists:filter(fun({K,_V})-> erlang:is_port(K) end, L),
            L_items = lists:filter(fun({K,_V})-> not erlang:is_port(K) end, L),
            reply(P_caller, {{conf, R}, {sockets, L_sockets}, {items, L_items}}),
            loop(R);

        {P_caller, stop} ->
            reply(P_caller, ok),
            stoped
    end.


%% --------------------------------------------------------------------
%% Function: get_idle_connect/3
%% Description: 
%%      1.查找是否有空闲的链接SOCKET
%%      2.查找过程中清理处于wait_connect状态,并超时的连接记录
%%      3.查找已租用但是租用进程挂掉的记录
%% Params:	
%%          L_connects  连接记录LIST
%%          I_now       当前时间 单位:ms
%%          I_connect_count 需要返回的有效连接数
%% Returns:	{ok, IdleSocket}    	        | 找到空闲的SOCKET
%%          {not_found, I_connect_count}    | 没有找到空闲连接,返回当前有效连接数
%% --------------------------------------------------------------------      
get_idle_connect([], _I_now, I_connect_count)->
    {not_found, I_connect_count};   
get_idle_connect([ R | T], I_now, I_connect_count)->
    case R#item.state of
        idle ->
            {ok, R#item.socket};
        wait_connect ->
            case (I_now - R#item.create_time) > R#item.connect_timeout of
                true ->
                    %% 如果连接已经超时,则重用该连接
                    del_connect(R#item.socket),
                    get_idle_connect(T, I_now, I_connect_count);
                false ->
                    get_idle_connect(T, I_now, I_connect_count + 1)
            end;
        busy ->
            case (I_now - R#item.last_use_time) > ?DEFAULT_USE_TIMEOUT of
                true ->
                    %% 如果使用已经超时,检查线程是否还存活,如果不存活则删除
                    case erlang:is_process_alive(R#item.pid) of
                        false ->
                            del_connect(R#item.socket),
                            get_idle_connect(T, I_now, I_connect_count);
                        true ->
                            get_idle_connect(T, I_now, I_connect_count + 1)
                    end;
                false ->
                    get_idle_connect(T, I_now, I_connect_count + 1)
            end
    end.
    

%% --------------------------------------------------------------------
%% Function: update_connect/2
%% Description: 
%%      修改某个端口记录的属性
%% Params:	
%%          Socket      端口
%%          L_property  要修改的属性列表[{state, atom()} |, {pid, pid()} |, {last_use_time, int()}]
%% Returns:	ok 					|
%%			{error, Reason}
%% --------------------------------------------------------------------      
update_connect(Socket, L_property) ->
    case erlang:get(Socket) of
        {S_ip, I_port} ->
            L_old = ?GET_LIST({S_ip, I_port}),
            L_new = lists:map(fun(R)->
                            case (R#item.socket == Socket) of
                                true -> update_item(R, L_property);
                                false -> R
                            end
                        end, L_old),
            erlang:put({S_ip, I_port}, L_new),
            ok;
        undefined ->
            {error, not_found}          
    end.

update_item(R_old, []) ->
    R_old;
update_item(R_old, [{pid, P_new} | T])->
    update_item(R_old#item{pid = P_new}, T);
update_item(R_old, [{last_use_time, I_use_time} | T])->
    update_item(R_old#item{last_use_time = I_use_time}, T);
update_item(R_old, [{state, A_newstate} | T]) ->
    update_item(R_old#item{state = A_newstate}, T).

%% --------------------------------------------------------------------
%% Function: put_connect/4
%% Description: 
%%          增加一个新端口记录
%% Params:	
%%          S_ip        IP地址
%%          I_port      端口号
%%          Socket      端口
%%          A_state     状态 wait_connect | idle | busy
%%          I_connect_timeout   连接超时时间 单位:ms
%% Returns:	ok 					|
%%			{error, Reason}
%% --------------------------------------------------------------------      
put_connect(S_ip, I_port, Socket, A_state) ->
    put_connect(S_ip, I_port, Socket, A_state, 0).

put_connect(S_ip, I_port, Socket, A_state, I_connect_timeout) ->
    L_old = ?GET_LIST({S_ip, I_port}),
    L_new = L_old ++ [#item{
                        ip = S_ip, 
                        port = I_port, 
                        state = A_state, 
                        socket = Socket, 
                        connect_timeout = I_connect_timeout,
                        create_time = timestamp()}],
    erlang:put( {S_ip, I_port}, L_new ),
    erlang:put( Socket, {S_ip, I_port} ),
    ok.

%% --------------------------------------------------------------------
%% Function: del_connect/1
%% Description: 
%%          删除一个新端口记录
%% Params:	
%%          Socket      端口
%% Returns:	ok 					|
%%			{error, Reason}
%% --------------------------------------------------------------------      
del_connect(Socket) ->
    case erlang:get(Socket) of
        {S_ip, I_port} ->
            L_old = ?GET_LIST({S_ip, I_port}),
            L_new = del_connect(L_old, Socket, []),
            erlang:put({S_ip, I_port}, L_new),
            erlang:erase(Socket),
            ok;
        undefined ->
            {error, not_found}
    end.

del_connect([], _FindSocket, L_ret)->
    L_ret;
del_connect([ R | T], FindSocket, L_ret) ->
    case R#item.socket == FindSocket of
        true -> 
            close_socket(R#item.socket),
            del_connect(T, FindSocket, L_ret);
        false -> 
            del_connect(T, FindSocket, L_ret ++ [R])
    end.    

%% 尽可能的关闭SOCKET    
close_socket(Socket) ->
    case is_port(Socket) of
        false ->
            ok;
        true ->
            case erlang:port_info(Socket) of
                undefined ->
                    ok;
                _ ->
                    catch( gen_tcp:close(Socket)),
                    ok
            end
    end.
    


%% 返回当前时间戳 精确到ms    
timestamp()->
    {M, S, MicroS} = erlang:now(),
    (M * 1000000 + S) * 1000 + round( MicroS /1000).

%% SERVER端回复消息
reply(P_caller, U_reply)->
    P_caller ! {reply, P_caller, U_reply}.

%% CLIENT发消息    
rpc(A_name, U_message) when is_atom(A_name) ->
    case whereis(A_name) of
        undefined ->
            {error, not_start};
        Pid ->
            rpc(Pid, U_message)
    end;
rpc(Pid, U_message) when is_pid(Pid)->
    P_me = self(),
    Pid ! {P_me, U_message},
    receive
        {reply, P_me, U_reply} -> U_reply
    end.    
