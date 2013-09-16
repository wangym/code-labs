%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%		SOCKET POOL TCP连接池 测试模块
%%% 
%%%
%%%     例如:
%%%     elib_socket_pool_test:base(). %%完成有base test finish
%%%     并发测试
%%%     elib_socket_pool_test:parallelstart([{"10.5.57.131",2345},{"10.5.57.132",2345},{"10.5.57.133",2345},{"10.5.57.134",2345},{"10.5.57.133",2345}], 20).
%%%     elib_socket_pool_test:parallelstop(). 
%%%
%%%
%%% Created:2010-02-27
%%%   
%%% -------------------------------------------------------------------
-module(elib_socket_pool_test).

-define(MANAGER_NAME, test_manager).
-define(TEST_SERVER_PORT, 20102).
-define(TEST_SERVER_NAME, pool_test).
-define(TEST_POOL_NAME, pool).
-define(TEST_PER_IP_MAX_CONNECTS, 13). 
-record(conf, {
            i_work_time = 0,
            i_sleep_time = 0,
            parent = undefined,
            ips = [],
            finish_count = 0,
            busy_count = 0,
            timeout_count = 0
        }).

-export([env_start/0, env_stop/0]).
-export([basetest/0, base/0]).
-export([parallelstart/2, parallelstart/3, parallelstart/4, parallelstop/0]).


%% --------------------------------------------------------------------
%% Function: basetest/0
%% Description: 
%% Params:	
%%          S_ip  测试IP地址
%%          I_port
%% Returns:	{ok, Pid} 	|
%%			{error, Reason}
%% --------------------------------------------------------------------    
base()->
    env_start(),
    basetest(),
    env_stop().

basetest()->

    io:format("test connect.~n"),
    {ok, S} = elib_socket_pool:connect(?TEST_POOL_NAME, "127.0.0.1", ?TEST_SERVER_PORT),

    %% "还"掉连接后,再次取，应该还是同一个socket
    io:format("test disconnect.~n"),
    ok = elib_socket_pool:disconnect(?TEST_POOL_NAME, S),
    io:format("test reconnect.~n"),
    {ok, S} = elib_socket_pool:connect(?TEST_POOL_NAME, "127.0.0.1", ?TEST_SERVER_PORT),

    %% 关闭连接后再取，应该取到一个新的socket
    io:format("test close connect.~n"),
    ok = elib_socket_pool:close_connect(?TEST_POOL_NAME, S),
    %% pool应该关闭该socket
    undefined = erlang:port_info(S),

    {ok, S1} = elib_socket_pool:connect(?TEST_POOL_NAME, "127.0.0.1", ?TEST_SERVER_PORT),
    true = (S1 =/= S),
    ok = elib_socket_pool:close_connect(?TEST_POOL_NAME, S1),

    %% 是否超出max connects 限制
    io:format("test per ip max connects.~n"),
    L = lists:seq(1, ?TEST_PER_IP_MAX_CONNECTS),
    [L_H | L_T] = lists:map(fun(_X)-> {ok, SS} = elib_socket_pool:connect(?TEST_POOL_NAME, "127.0.0.1", ?TEST_SERVER_PORT),SS end, L),
    {error, pool_busy} = elib_socket_pool:connect(?TEST_POOL_NAME, "127.0.0.1", ?TEST_SERVER_PORT),
    %% 释放掉一个
    ok = elib_socket_pool:close_connect(?TEST_POOL_NAME, L_H),
    {ok, S2} = elib_socket_pool:connect(?TEST_POOL_NAME, "127.0.0.1", ?TEST_SERVER_PORT),
    ok = elib_socket_pool:close_connect(?TEST_POOL_NAME, S2),
    %% 关闭所有的
    
    lists:map(fun(X) -> ok = elib_socket_pool:close_connect(?TEST_POOL_NAME, X) end, L_T),
    io:format("~w~n",[elib_socket_pool:status(?TEST_POOL_NAME)]),


    



    io:format("\n===========base test finish.===========~n"),
    ok.

%%
%% I_process_count 测试进程数
%% I_work_count 循环测试次数
%% I_work_time 模拟工作时间 单位:ms
%% I_sleep_time 2次工作间休眠时间 单位:ms
%%
parallelstart(L_ips, I_process_count)->
    parallelstart(L_ips, I_process_count, 3, 0).

parallelstart(L_ips, I_process_count, I_work_time)->
    parallelstart(L_ips, I_process_count, I_work_time, 0).

parallelstart(L_ips, I_process_count, I_work_time, I_sleep_time)->
    env_start(),
    R = #conf{
            ips = L_ips,
            i_work_time = I_work_time,
            i_sleep_time = I_sleep_time
            },
    Pid = spawn_link(fun()->parallel_manager([]) end),
    Pid ! {self(), start, I_process_count, R},
    receive
        ok ->
            put({?MANAGER_NAME, start_time}, erlang:now()),
            register(?MANAGER_NAME, Pid),
            {ok, Pid}
    end.

parallelstop()->
    case whereis(?MANAGER_NAME) of
        undefined->
            {error, not_start};
        Pid ->
            Pid ! {self(), stop},
            receive
                {ok, L_result} ->
                    %秒
                    I_usetime = timer:now_diff(erlang:now(),  get({?MANAGER_NAME, start_time})) / 1000000,

                    {I_finish, I_busy, I_timeout} = report(L_result, {0,0,0}),
                    io:format("finish: ~w qps(s):~w~n",[I_finish, I_finish/ I_usetime]),
                    io:format("busy:~w qps(s):~w~n",[I_busy, I_busy/I_usetime]),
                    io:format("timeout:~w qps(s):~w~n",[I_timeout, I_timeout/I_usetime]),
                    io:format("total:~w qps(s):~w~n",[(I_finish + I_busy + I_timeout), (I_finish + I_busy + I_timeout)/I_usetime]),
                    unregister(?MANAGER_NAME),
                    ok
                after 10000 ->
                    {error, stop_timeout}
            end
    end.            

report([], Ret)->
    Ret;
report([{_Pid, R} | T], {I_finish, I_busy, I_timeout})->
    report(T, {I_finish + R#conf.finish_count, I_busy + R#conf.busy_count, I_timeout + R#conf.timeout_count}).



parallel_manager(L_pids)->
    receive
        {P_caller, start, I_count, R_conf}->
            {ok, L} = manager_start(I_count, R_conf#conf{parent = self()}, []),
            P_caller ! ok,
            parallel_manager(L);
        {P_caller, stop}->
            lists:map(fun(Pid)-> Pid ! stop end, L_pids),
            manager_stop(L_pids),
            P_caller ! {ok, get()},
            receive
                ok ->
                    stoped
            end

    end.

manager_stop([])->
    ok;
manager_stop(L_pids)->
    receive
        {test_finish, Pid, R}->
            put(Pid, R),
            manager_stop(L_pids -- [Pid])
    end.
    

manager_start(0, _R, List)->
    {ok, List};
manager_start(I_count, R, List)->
    Pid = spawn_link(fun()->parallel_loop(R) end),
    Pid ! work,
    put(Pid, undefined),
    manager_start(I_count - 1, R, List ++ [Pid]).



parallel_loop(R)->
    receive
        work ->
            [{IP, PORT} | T] = R#conf.ips,
            R_new = case parallel_work(IP, PORT, R#conf.i_work_time) of
                        ok ->
                            R#conf{finish_count = R#conf.finish_count + 1};
                        {error, pool_busy} ->
                            R#conf{busy_count = R#conf.busy_count + 1};
                        {error, timeout} ->
                            R#conf{timeout_count = R#conf.timeout_count + 1};
                        _Other ->
                            exit
                    end,
            self() ! work,
            timer:sleep(R#conf.i_sleep_time),
            parallel_loop(R_new#conf{ips = T ++ [{IP, PORT}]});
        stop ->
            R#conf.parent ! {test_finish, self(), R},
            stoped
    end.

parallel_work(IP, Port, I_work_time)->
    case elib_socket_pool:connect(?TEST_POOL_NAME, IP, Port) of
        {ok, Socket} ->
            timer:sleep(I_work_time),
            ok = elib_socket_pool:disconnect(?TEST_POOL_NAME, Socket),
            ok;
        {error, pool_busy} ->
            {error, pool_busy};
        {error, timeout} ->
            {error, timeout};
        Error ->
            io:format("parallel work error, ~s:~w~n",[IP, Port]),
            exit(Error)
    end.

env_start()->
    io:format("test env start.~n"),
    {ok, _ServerPid} = elib_telnet:start(?TEST_SERVER_PORT, ?TEST_SERVER_NAME),
    io:format("test server start in ~w.~n",[?TEST_SERVER_PORT]),
    {ok, _PoolPid} = elib_socket_pool:start(?TEST_POOL_NAME,[{per_ip_max_connects, ?TEST_PER_IP_MAX_CONNECTS}]),
    io:format("test pool start per_ip_max_connects ~w.~n",[?TEST_PER_IP_MAX_CONNECTS]),
    ok.

env_stop()->
    ok = elib_socket_pool:stop(?TEST_POOL_NAME),
    io:format("test pool stop.~n"),
    elib_telnet:stop(?TEST_SERVER_PORT),
    io:format("test server stop.~n"),
    ok.
 
