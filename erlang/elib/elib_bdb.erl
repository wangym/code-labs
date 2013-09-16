%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description : BDB DRIVER( 支持 PORT / DRIVER 两种)方式驱动
%%% Created : 2009-11-24
%%% --------------------------------------------------------------------
-module(elib_bdb).

%% 数据表类型
-define(DB_BTREE, 1).
-define(DB_HASH,  2).
-define(DB_RECNO, 3).
-define(DB_QUEUE, 4).
-define(DB_UNKNOWN, 5).

%% 数据表打开标志
-define(DB_AUTO_COMMIT, 16#100).
-define(DB_CREATE, 16#1).
-define(DB_EXCL, 16#40).
-define(DB_MULTIVERSION, 16#4).
-define(DB_NOMMAP,16#8).
-define(DB_RDONLY, 16#400).
-define(DB_READ_UNCOMMITTED, 16#200).
-define(DB_THREAD, 16#10).
-define(DB_TRUNCATE, 16#4000).

%% PORT驱动的shell文件名称
-define(SHELL_PORT, "bdb_port").
%% DRIVER驱动的文件名称"bdb_drv.so"
-define(SHELL_DRIVER, "bdb_drv").
%% 默认在哪些目录下搜索驱动文件
-define(SHELL_FIND_SCOPE,	["./", "./ebin/", "../ebin/", "./c_src/", "../c_src/"]).

%% 返回元组OK的二进制表示
-define(B_OK, <<131,100,0,2,111,107>>).

%% 返回元组NOT_FOUND的二进制表示
%% erlang:term_to_binary({error,"DB_NOTFOUND: No matching key/data pair found"})
-define(B_NOTFOUND, <<131,104,2,100,0,5,101,114,114,111,114,107,0,44,68,66,95,78,79,84,70,79,85,78,68,58,32,78,111,32,109,97,116,99,104,105,110,103,32,107,101,121,47,100,97,116,97,32,112,97,105,114,32,102,111,117,110,100>>).

%% 测试数据例子
-define(B_CHECK, <<"____bdb_check_____">>).


%% 状态记录
-record(conf, {
            a_name = undefiend,     %% erlang 注册名
            s_dbfile = undefined,   %% 数据表文件例如: ./data/username.db
            a_dbtype = db_btree,    %% 数据类型 db_btree, db_hash
            l_dbflag = [db_create], %% 数据表打开标识 例如: [db_create , db_auto_commit]
            a_mode = driver,        %% 驱动方式 driver , port
            s_shell_path = undefined,   %% shell所在路径, 默认系统遍历搜索预订路径,也可指定
                                        %% 例如:/usr/bin/bdb_drv.so 或 /home/bin/bdb_port
            port = undefined        %% 打开的port端口ID
            }).

-export([start/2, start/3, stop/1, putdata/3, getdata/2, deldata/2]).
-export([check/1, status/1, test/3]).


%% --------------------------------------------------------------------
%% Function: start/2, start/3
%% Description: 
%% Params:	A_name  erlang注册名
%%			S_dbfile  数据库名(可指定路径) 例如: "test.db" | "../data/test.db"
%%          L_options 配置参数列表,默认如下:
%%                      [{mode,driver}, %% 默认 driver 驱动模式 [port | driver]
%%                       {type,db_btree}, %% 数据库类型 [db_btree | db_hash | db_recno | db_queue]
%%                       {flag,[db_create]}, %% 标记 多选 db_create , db_rdonly ...
%%                       {shell, undefiend}
%%                      }]
%% Returns:	{ok, Pid} 	|
%%			{error, Reason}
%% --------------------------------------------------------------------    
start(A_name, S_dbfile) ->
    start(A_name, S_dbfile, []).

start(A_name, S_dbfile, L_options)->
    case whereis(A_name) of
        undefined ->
            R = #conf{
                    a_name =    A_name,
                    a_mode =    case proplists:get_value(mode, L_options) of
                                    undefined -> driver;
                                    A_usrmode -> A_usrmode
                                end,
                    s_dbfile =  S_dbfile,
                    a_dbtype =  case proplists:get_value(type, L_options) of 
                                    undefined -> db_btree; 
                                    A_usrtype -> A_usrtype 
                                end,
                    l_dbflag =  case proplists:get_value(flag, L_options) of 
                                    undefined -> [db_create]; 
                                    A_usrflag -> A_usrflag
                                end,
                    s_shell_path=   case proplists:get_value(shell, L_options) of
                                        undefined -> undefined;
                                        A_usrshell-> A_usrshell
                                    end
                },
            start(R);
        P_old -> {ok, P_old}
    end.  

%% 检查模式            
start(#conf{a_mode = A_mode}) when A_mode == undefiend ->
    {error, "mode is empty. select [port | driver]."};
%% 检查shell_path   
start(#conf{a_mode = A_mode, s_shell_path = S_shell_path} = R) when S_shell_path == undefined ->
    S_shell_file = case A_mode of port -> ?SHELL_PORT ; _-> ?SHELL_DRIVER ++ ".so" end,
    case find_file(S_shell_file, ?SHELL_FIND_SCOPE) of
        not_found->
            {error, sformat("not found ~s. please copy ~s to ~s .", 
                        [S_shell_file, S_shell_file, string:join(?SHELL_FIND_SCOPE, ",")])};
        S_find ->
            start(R#conf{s_shell_path = S_find ++ S_shell_file})
    end;
%% 启动进程    
start(R)->
    case lists:member(R#conf.a_mode, [port, driver]) of
        false -> {error, "error mode " ++ atom_to_list(R#conf.a_mode)};
        true ->
                Pid = spawn_link(fun()-> loop(R) end),
                A_start_mode = case R#conf.a_mode of port -> port; _Other -> driver end,
                Pid ! {start, self(), A_start_mode},
                receive
                    ok -> 
                        register(R#conf.a_name, Pid), 
                        {ok, Pid};
                    {error, Reason} -> 
                        {error, Reason}
                end
    end.

%% --------------------------------------------------------------------
%% Function: stop/1
%% Description: 
%% Params:	A_name  erlang注册名
%% Returns:	ok 					|
%%			{error, not_start}
%% --------------------------------------------------------------------
stop(A_name) ->
    rpc(A_name, stop).

 
%% --------------------------------------------------------------------
%% Function: putdata/3
%% Description: 
%% Params:	A_name  	erlang注册名
%%			B_key		binary
%%			B_val		binary
%% Returns:	ok 					|
%%			{error, not_start}
%%			{error, Reason}
%% --------------------------------------------------------------------    
putdata(A_name, B_key, B_val)->
    rpc(A_name, {put, B_key, B_val}).


%% --------------------------------------------------------------------
%% Function: getdata/2
%% Description: 
%% Params:	A_name  	erlang注册名
%%			B_key		binary
%% Returns:	{ok, B_value}			|
%%			undefined       		|
%%			{error, format_error}	|
%%			{error, not_start}		|
%%			{error, Reason}	
%% --------------------------------------------------------------------      
getdata(A_name, B_key)->
    case rpc(A_name, {get, B_key}) of
        {ok, B_val} -> 
                {ok, B_val};
        not_found -> 
                undefined;
        {error, not_found} -> 
                undefined;
        Other -> 
                Other                           
    end.


%% --------------------------------------------------------------------
%% Function: deldata/2
%% Description: 
%% Params:	A_name  	erlang注册名
%%			B_key		binary
%% Returns:	ok						|
%%			{error, not_start}		|
%%			{error, Reason}	
%% -------------------------------------------------------------------- 
deldata(A_name, B_key)->
    %% 先查后删，避免删除空的数据影响性能
    case getdata(A_name, B_key) of
        {ok, _B_val} ->
            case rpc(A_name, {del, B_key}) of
                not_found -> ok;
                Other -> Other
            end;
        undefined -> ok;
        Other -> Other
    end.

%% --------------------------------------------------------------------
%% Function: check/1
%% Description: 
%% Params:	A_name  	erlang注册名
%% Returns:	normal					|
%%			{fail, {putstat, ..}, {getstat, ..}, {delstat, ..}}		|
%% --------------------------------------------------------------------
check(A_name)->
	deldata(A_name, ?B_CHECK),
	PutStat = putdata(A_name, ?B_CHECK, ?B_CHECK),
	GetStat = case getdata(A_name, ?B_CHECK) of
				{ok, ?B_CHECK} -> ok;
				{error, Reason} -> {error, Reason}
			  end,
	DelStat = deldata(A_name, ?B_CHECK),
	case ( (PutStat == ok ) and (GetStat == ok) and (DelStat == ok) ) of
		true -> normal;
		false -> {fail, {putstat , PutStat}, {getstat, GetStat}, {delstat, DelStat}}
	end.

%% --------------------------------------------------------------------
%% Function: status/1
%% Description: 
%% Params:	A_name  	erlang注册名
%% Returns:	{ok, Record}					
%% --------------------------------------------------------------------
status(A_name)->
    rpc(A_name, status).


%% --------------------------------------------------------------------
%% Function: test/3 方法
%% Description: 测试port方式功能和性能
%% Params:	A_dbname  erlang注册名
%%			I_count   循环做几次
%%			I_size	  模拟多大的数据
%% Sample:
%%		start(test,"test.db").
%%		test(test, 10000, 256).
%% Returns:	ok
%% --------------------------------------------------------------------
test(A_dbname, I_count, I_size)->
	LB = lists:map(fun(_X)-><<"A">> end, lists:seq(1, I_size)),
    B = concat_binary(LB),
    L = lists:seq(1, I_count),

    statistics(wall_clock),
    statistics(runtime),
	lists:map(fun(X)->?MODULE:putdata(A_dbname, <<X:64>>, B) end, L),
    {_,TimePut1}=statistics(wall_clock),
    {_,_TimePut2}=statistics(runtime),
	io:format("put ~w size ~w data, use time ~w ms, avg time ~w ms.~n",[I_count, size(B), TimePut1, TimePut1 / I_count]),

    statistics(wall_clock),
    statistics(runtime),
	lists:map(fun(X)->?MODULE:getdata(A_dbname, <<X:64>>) end, L),
    {_,TimeGet1}=statistics(wall_clock),
    {_,_TimeGet2}=statistics(runtime),
	io:format("get ~w size ~w data, use time ~w ms, avg time ~w ms.~n",[I_count, size(B), TimeGet1, TimeGet1 / I_count]),

    statistics(wall_clock),
    statistics(runtime),
	lists:map(fun(X)->?MODULE:deldata(A_dbname, <<X:64>>) end, L),
    {_,TimeDel1}=statistics(wall_clock),
    {_,_TimeDel2}=statistics(runtime),
	io:format("del ~w size ~w data, use time ~w ms, avg time ~w ms.~n",[I_count, size(B), TimeDel1, TimeDel1 / I_count]),
	ok.
    

%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------
loop(#conf{port = Port, a_mode = A_mode} = R)->
    receive
        {start, P_caller, port} ->
	        I_type = type_to_int(R#conf.a_dbtype),
			I_flags = flag_to_int(R#conf.l_dbflag),
            %% open port
            S_command = sformat("~s ~s ~w ~w", [R#conf.s_shell_path, R#conf.s_dbfile, I_type, I_flags]),
            P_new = open_port({spawn, S_command}, [{packet, 4}, binary]),

            P_caller ! ok,
            loop(R#conf{port = P_new});

        {start, P_caller, driver} ->
	        I_type = type_to_int(R#conf.a_dbtype),
			I_flags = flag_to_int(R#conf.l_dbflag),
            S_shell_dir = filename:rootname(R#conf.s_shell_path, ?SHELL_DRIVER ++ ".so"),
            %% load driver
            erl_ddll:load_driver(S_shell_dir, ?SHELL_DRIVER),
            
            %% open port
            S_command = sformat("~s ~s ~w ~w", [?SHELL_DRIVER, R#conf.s_dbfile, I_type, I_flags]),
            P_new = open_port({spawn, S_command}, [binary]),

            P_caller ! ok,
            loop(R#conf{port = P_new});

        {P_caller, {put, B_key, B_val}}->
            T_res = do_call_port(A_mode, Port, {put, B_key, B_val}),
            reply(P_caller, T_res),
            loop(R);
        {P_caller, {get, B_key}} ->
            T_res = do_call_port(A_mode, Port, {get, B_key}),
            reply(P_caller, T_res),
            loop(R);
        {P_caller, {del, B_key}} ->
            T_res = do_call_port(A_mode, Port, {del, B_key}),
            reply(P_caller, T_res),
            loop(R);
        {P_caller, status} ->
            reply(P_caller, {ok, R}),
            loop(R);
        {P_caller, stop} -> 
            case R#conf.a_mode of
                port -> 
                    port_command(Port, {close, nop}), 
        			receive 
    		    		{Port, {data, _Data}} -> 
                            reply(P_caller,stoped)
	        		end;
                _Other ->
                    port_close(Port),
                    reply(P_caller, stoped)
            end
    end.

%% 编码    
do_call_port(Mode, Port, U_message) when is_binary(U_message) == false ->
    do_call_port(Mode, Port, term_to_binary(U_message));
%% driver模式    
do_call_port(driver, Port, B)->
	port_control(Port, 1, B),
	receive
		{Port, {data, ?B_OK}} -> ok;
		{Port, {data, ?B_NOTFOUND}} -> not_found;
		{Port, {data, B_term}} ->binary_to_term(B_term)
	end;
%% port 模式    
do_call_port(port, Port, B)->
	port_command(Port, B),
	receive
		{Port, {data, ?B_OK}} -> ok;
		{Port, {data, ?B_NOTFOUND}} -> not_found;   
		{Port, {data, B_term}} -> binary_to_term(B_term);
		Reason -> {error, Reason}
	end.


reply(P_caller, T_message)->
    P_caller ! {reply, P_caller, T_message}.

rpc(A_name, T_message) when is_atom(A_name)->
    case whereis(A_name) of
        undefined ->
            {error, not_start};
        Pid ->
            rpc(Pid, T_message)
    end;
rpc(Pid, T_message) when is_pid(Pid) ->
    P_me = self(),
    Pid ! {P_me, T_message},
    receive
        {reply, P_me, T_response} ->
            T_response
    end.
    

sformat(S_format, L)->
    Chars = io_lib:format(S_format, L),
    [S_ret] = io_lib:format("~s",[list_to_binary(Chars)]),
    S_ret.

%% 在指定范围内查找某个文件的路径
find_file(_S_find, [])->
	not_found;
find_file(S_find, [ S_dir | T])->
	case filelib:is_file(S_dir ++ S_find) of
		true -> S_dir;
		false -> find_file(S_find, T)
	end.
	
%% db_btree... => int()
type_to_int(A_type) when is_atom(A_type) ->
	case A_type of
		db_btree	-> ?DB_BTREE;
		db_hash		-> ?DB_HASH;
		db_recno	-> ?DB_RECNO;
		db_queue	-> ?DB_QUEUE
	end.

%% [db_create, db_auto_commit] => int()
flag_to_int(A_flag) when is_atom(A_flag) ->
	case A_flag of
		db_auto_commit	-> ?DB_AUTO_COMMIT;
		db_create		-> ?DB_CREATE;
		db_excl			-> ?DB_EXCL;
		db_multiversion	-> ?DB_MULTIVERSION;
		db_nommap		-> ?DB_NOMMAP;
		db_rdonly		-> ?DB_RDONLY;
		db_read_uncommitted	-> ?DB_READ_UNCOMMITTED;
		db_thread		-> ?DB_THREAD;
		db_truncate		-> ?DB_TRUNCATE
	end;
flag_to_int(L_flags) when is_list(L_flags) ->
	flag_to_int(L_flags, 0).

flag_to_int([], I)->
	I;
flag_to_int([ A_flag | T], I) ->
	I_flag = flag_to_int(A_flag),
	flag_to_int(T, (I_flag bor I)).
