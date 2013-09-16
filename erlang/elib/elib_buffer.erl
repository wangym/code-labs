%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%		缓冲池管理
%%% Created:2009-11-10
%%% Example:
%%% -------------------------------------------------------------------
-module(elib_buffer).

%% 单位字节 , 10M
-define(DEFAULT_MAX_SIZE, 10485760). 
%% 每次清理多少个
-define(CLEANUP_COUNT, 20).
%% 清理模式1 考虑整个erl虚拟使用内存,一旦虚拟机使用内存超过最大设置即开始清理, 默认模式
-define(CLEANUP_SYSTEM, cleanup_system).
%% 清理模式2 只考虑buffer是用内存, 项目中常常会发生buffer是用未超出限制,但加上erl虚拟机使用内存则大大超出预设值
-define(CLEANUP_BUFFER, cleanup_buffer).

%% 配置状态记录
-record(conf, {
        a_name = undefined,         %% erlang注册名
        a_name_fifo = undefined,    %% 先入先出算法记录，记录顺序的ets表
        i_max_size = 0,             %% 最大内存数 单位:字节, 如果小于默认字节:DEFAULT_MAX_SIZE 置于0 禁止cache
        i_last_id = 0,              %% 最后一个id
        a_cleanup_mode = ?CLEANUP_SYSTEM,    %%清理模式 [cleanup_system | cleanup_buffer]
        i_system_memory = 0         %% 系统内存数 字节
        }).

-export([start/0, start/1, start/2, stop/1, stop/0]).
-export([putdata/3, putdata/2, deldata/2, deldata/1, getdata/2, getdata/1, status/1, status/0]).

%% --------------------------------------------------------------------
%% Function: start/0, start/1, start/2
%% Description: 
%% Params:	A_name  erlang注册名
%%          I_max_size 最大字节 如果< DEFAULT_MAX_SIZE则设置为0 ,禁止CACHE
%% Returns:	{ok, Pid} 	|
%%			{error, Reason}
%% --------------------------------------------------------------------    
start()->
    start(?MODULE).

start(A_name) ->
    start(A_name, ?DEFAULT_MAX_SIZE).

start(A_name, I_max_size)->
    I_max = case I_max_size < ?DEFAULT_MAX_SIZE of true -> 0; false -> I_max_size end,
    P_me = self(),
    case whereis(A_name) of
        undefined ->
            P_new = spawn_link(fun()-> loop(#conf{}) end),
            A_name_fifo = list_to_atom(atom_to_list(A_name) ++ "_fifo"),
            R_new =#conf{a_name = A_name, a_name_fifo = A_name_fifo, i_max_size = I_max, i_last_id = 0}, 
            P_new ! {start,  R_new, P_me},
            receive
                {ok, P_me} -> 
                    register(A_name, P_new),
                    {ok, P_new}
            end;
        Pid -> {ok, Pid}
    end.

%% --------------------------------------------------------------------
%% Function: stop/0, stop/1
%% Description: 
%% Params:	A_name  erlang注册名
%% Returns:	ok 					|
%%			{error, not_start}
%% --------------------------------------------------------------------
stop()->
    stop(?MODULE).

stop(A_name)->
    P_me = self(),
    case whereis(A_name) of
        undefined -> {error, not_start};
        _Pid ->
            A_name ! {stop, P_me},
            receive
                {ok, P_me} -> ok
            end
    end.

%% --------------------------------------------------------------------
%% Function: putdata/2, putdata/3
%% Description: 
%% Params:	A_name  	erlang注册名
%%			U_name		表达式包括: binary | list | int | Tuple...
%%			U_value		表达式包括: binary | list | int | Tuple...
%% Returns:	ok 					|
%%			{error, not_start}
%%			{error, Reason}
%% --------------------------------------------------------------------       
putdata(U_name, U_value)->
    putdata(?MODULE, U_name, U_value).

putdata(A_name, U_name, U_value)->
    P_me = self(),
    case whereis(A_name) of
        undefined ->
            {error, not_start};
        _Pid ->
            A_name ! {putdata, {U_name, U_value}, P_me},
            receive
                {ok, P_me} -> ok;
                {fail, P_me} -> {error, put_error}
            end
    end.

%% --------------------------------------------------------------------
%% Function: getdata/1, getdata/2
%% Description: 
%% Params:	A_name  	erlang注册名
%%			U_name		表达式包括: binary | list | int | Tuple...
%% Returns:	{ok, U_value}
%%          undefined
%%			{error, Reason}
%% --------------------------------------------------------------------       
getdata(U_name)->
    getdata(?MODULE, U_name).

getdata(A_name, U_name)->
    case ets:info(A_name) of
        undefined ->
            {error, not_start};
        _ ->
           case catch(ets:lookup(A_name, U_name)) of
                [{U_name, U_value, _Id}] -> {ok, U_value};
                _ -> undefined
           end
    end.

%% --------------------------------------------------------------------
%% Function: deldata/1, deldata/2
%% Description: 
%% Params:	A_name  	erlang注册名
%%			U_name		表达式包括: binary | list | int | Tuple...
%% Returns:	ok 					|
%%			{error, not_start}
%%			{error, Reason}
%% --------------------------------------------------------------------       
deldata(U_name)->
    deldata(?MODULE, U_name).

deldata(A_name, U_name)->
    P_me = self(),
    case whereis(A_name) of
        undefined ->
            {error, not_start};
        _Pid ->
            A_name ! {deldata, U_name, P_me},
            receive
                {ok, P_me} -> ok
            end
    end.

%% --------------------------------------------------------------------
%% Function: status/0, status/1
%% Description: 
%% Params:	A_name  	erlang注册名
%% Returns:	{ok, T_master_status, T_namefifo_status}			
%% --------------------------------------------------------------------       
status()->
    status(?MODULE).

status(A_name) ->
    P_me = self(),
    A_name ! {status, P_me},
    receive
        Data -> Data
    end.

%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------   
loop(#conf{a_name = A_name, a_name_fifo = A_name_fifo} = R_conf)->
    receive
        {start, R_new, P_from} ->
            %% 主表
            ets:new(R_new#conf.a_name, [set, protected, named_table]),
            %% 实现FIFO的辅助表
            ets:new(R_new#conf.a_name_fifo, [ordered_set, protected, named_table]),
            P_from ! {ok, P_from},
            %% 启动定时进程，定时更新system memory
            P_me = self(),
            spawn_link(fun()-> loop_system_memory(P_me) end),
            loop(R_new#conf{i_system_memory = erlang:memory(total)});
        {update_system_memory, I_new_size} ->
            loop(R_conf#conf{i_system_memory = I_new_size});
        {deldata, U_name, P_from} ->
            case ets:lookup(A_name, U_name) of
                [{U_name, _U_value, Id}] ->
                    ets:delete(A_name, U_name),
                    ets:delete(A_name_fifo, Id),
                    P_from ! {ok, P_from},
                    loop(R_conf);
                [] ->
                    P_from ! {ok, P_from},
                    loop(R_conf)
            end;
        {putdata, {U_name, U_value}, P_from} ->
            case R_conf#conf.i_max_size of
                0 ->
                    %% 用户禁止cache
                    P_from ! {ok, P_from},
                    loop(R_conf);
                _ ->
                    %%有一个问题当单个值的大小超过i_max_size设置会死循环
                    %%前期清理
                    cleanup(R_conf),
                    case ets:lookup(A_name, U_name) of
                        [] ->
                            %% 新增
                            I_last_id = R_conf#conf.i_last_id,
                            %% 主表插入
                            case ets:insert(A_name, {U_name, U_value, I_last_id + 1}) of
                                true ->
                                    %% FIFO辅助表
                                    ets:insert(A_name_fifo, {I_last_id + 1, U_name}),
                                    P_from ! {ok, P_from},
                                    loop(R_conf#conf{i_last_id = I_last_id + 1});
                                false ->
                                    P_from ! {fail, P_from},            
                                    loop(R_conf)
                            end;
                        [{U_name, _U_oldvalue, I_oldid}] ->
                             %%修改
                             ets:insert(A_name, {U_name, U_value, I_oldid}),
                             P_from ! {ok, P_from},
                             loop(R_conf)
                    end
            end;                    
        {status, P_from} ->
            P_from ! {ok, R_conf, ets:info(A_name), ets:info(A_name_fifo)},
            loop(R_conf);
        {stop, P_from} ->
            ets:delete(R_conf#conf.a_name),
            ets:delete(R_conf#conf.a_name_fifo),
            P_from ! {ok, P_from}
    end.

loop_system_memory(Parent)->
    receive
        stop ->
            stoped
        after 2000 ->
            %% 2秒检查一次
            Parent ! {update_system_memory, erlang:memory(total)},
            loop_system_memory(Parent)
    end.

%% 清理
cleanup(#conf{a_name = A_name, a_name_fifo = A_name_fifo, i_max_size = I_max_size} = R_conf)->
    I_cur = case R_conf#conf.a_cleanup_mode of
                ?CLEANUP_SYSTEM ->
                    R_conf#conf.i_system_memory;
                ?CLEANUP_BUFFER ->
                    [{memory, I1} | _] = ets:info(A_name),
                    [{memory, I2} | _] = ets:info(A_name_fifo),
                    (I1 + I2)
            end,
   case I_cur > I_max_size of
        false -> ok;
        true ->
            %% 尝试删除指定个数的数据
            cleanup(R_conf, ?CLEANUP_COUNT)
%%            %% 再次检查是否小于最大字节数限制
%%            cleanup(R_conf)
    end.
   
cleanup(_, 0)->
    ok;
cleanup(#conf{a_name = A_name, a_name_fifo = A_name_fifo} = R_conf, I_count)->
    case ets:first(A_name_fifo) of
        '$end_of_table' -> ok;
        Id ->
            [{Id, U_name}] = ets:lookup(A_name_fifo, Id),
            ets:delete(A_name, U_name),
            ets:delete(A_name_fifo, Id),
            cleanup(R_conf, I_count - 1)
    end.
