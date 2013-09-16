%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%     简化disk_log操作,提供一个异步同步日志系统
%%% Created : 2009-12-4
%%% Example:
%%%     elib_disklog:start(dellog, "./log/delete.log",fun(U_logitem) -> io:format("~w~n",[U_logitem]) end).
%%%     elib_disklog:log(dellog, <<"hello1">>).
%%%     elib_disklog:log(dellog, <<"hello2">>).
%%%     elib_disklog:stop(dellog).
%%% ------------------------------------------------------------------
-module(elib_disklog_reader).

%%单位:毫秒
-define(MAX_SLEEP, 100).
-define(CURSORKEY, "cursor").
-define(DEFAULT_MAX_FILES, 64999).


-record(conf, {
        a_name = undefined, 
        s_log_file = undefined,     %% log文件

        a_cursor_name = undefined,  %% 游标名称
        s_cursor_file = undefined,  %% 游标位置记录文件
        t_last_cursor = undefined,  %% 最后的游标

        fun_work = undefined,       %% 处理事务数据方法

        p_worker = undefined,       %% WORKER进程PID
        a_worker_status = undefined,    %% WORKER进程状态 [work | wait],

        p_clean = undefined,        %% 清理进程PID

        i_sleep = ?MAX_SLEEP,               %% 主进程无待处理消息时等待时间(毫秒),
        i_read_count = 10           %% 每次多多少条
        }).


%% --------------------------------------------------------------------
%% External exports
%% -------------------------------------------------------------------
-export([start/3, stop/1, status/1]).


%% --------------------------------------------------------------------
%% Function: start/3
%% Description: 启动
%% Params:  
%%          A_name  	    名称
%%          S_log_file      日志文件名 例如: ./log/delete.log
%%          FunWork         处理日志的函数
%% Returns: {ok, Pid}       |
%%			{error , Reason}
%% -------------------------------------------------------------------
start(A_name, S_log_file, FunWork)->
    R = #conf{
            a_name = A_name, 
            s_log_file = S_log_file, 
            fun_work = FunWork,
            s_cursor_file = filename:rootname(S_log_file) ++ ".cursor",
            a_cursor_name = list_to_atom(atom_to_list(A_name) ++ "_cursor")
            },
    case whereis(A_name) of
        undefined->
            Pid = spawn_link(fun()-> loop(#conf{}) end),
            register(A_name, Pid),
            case rpc(A_name, {start, R}) of
                ok -> {ok, Pid};
                Error -> Error
            end;
        P_old ->
            {ok, P_old}
    end.

%% --------------------------------------------------------------------
%% Function: stop/1
%% Description: 
%% Params:  
%%          A_name  	    名称
%% Returns: ok
%%          {error, not_start}
%%			{error , Reason}
%% -------------------------------------------------------------------
stop(A_name)->
    rpc(A_name, stop).

%% --------------------------------------------------------------------
%% Function: status/1
%% Description: 
%% Params:  
%%          A_name  	    名称
%% Returns: {ok, R}
%%          {error, not_start} 
%% ------------------------------------------------------------------
status(A_name)->
    rpc(A_name, status).

%% --------------------------------------------------------------------
%% Internal functions
%% --------------------------------------------------------------------
loop(#conf{i_sleep = I_sleep} = R)->
    receive
        {P_caller, {start, R_new} } ->
            case open_log(R_new) of
                {ok, T_firstcursor} ->
                    %% 获得头游标后，尝试打开游标状态文件
                    case open_cursor(R_new, T_firstcursor) of
                        {ok, R_init} ->
                            %%io:format("~w~n",[R_init]),
                            %% 初始化成功t_last_cursor后,初始化工作进程
                            P_worker = spawn(fun()->loop_worker(R_init#conf.fun_work) end),
                            %% 发送一个空的任务列表
                            rpc(P_worker, {run, []}),
                            reply(P_caller, ok),
                            %% 清理进程
                            P_clean = spawn(fun()->loop_clean(R_init) end),

                            loop(R_init#conf{p_worker = P_worker, p_clean = P_clean});
                        Error ->
                            reply(P_caller, Error)
                    end;
                Error ->
                    reply(P_caller, Error)
            end;
        {P_caller, status} ->
            reply(P_caller, {ok, R}),
            loop(R);

        {P_caller, work_finish} ->
            reply(P_caller, ok),
            %%保存t_last_cursor, 置wait状态 然后什么都不做,等待 after .
            save_cursor(R),

           loop(R#conf{a_worker_status = wait, i_sleep = 0});
        {P_caller, stop}->
            exit(R#conf.p_worker, kill),
            exit(R#conf.p_clean, kill),
            reply(P_caller, ok),
            stoped
        after I_sleep ->
            case R#conf.a_worker_status of
                work ->
                    loop(R);
                wait ->
                    case wrap_log_reader:chunk(R#conf.t_last_cursor, R#conf.i_read_count) of
                        {T_newcursor, eof} -> 
                            %% 没有任务, sleep时间长点
                            %% io:format("~w,eof.~n",[R#conf.t_last_cursor]),
                            loop(R#conf{t_last_cursor = T_newcursor, i_sleep = ?MAX_SLEEP});
                        {T_newcursor, L_jobs} ->
                            %% 有任务
                            rpc(R#conf.p_worker, {run, L_jobs}),
                            loop(R#conf{t_last_cursor = T_newcursor, a_worker_status = work, i_sleep = ?MAX_SLEEP});
                        {T_newcursor, L_jobs, _I_badbytes} ->
                            rpc(R#conf.p_worker, {run, L_jobs}),
                            loop(R#conf{t_last_cursor = T_newcursor, a_worker_status = work, i_sleep = ?MAX_SLEEP})
                    end
            end
    end.

    
%% 清理进程, 定期清除使用过的文件防止日志目录下文件数过多    
loop_clean(R)->
    receive
        after 3000 ->
            case catch(dets:lookup(R#conf.a_cursor_name, ?CURSORKEY)) of
                [] -> 
                    loop_clean(R);
                [{_, T_oldcursor}] ->

                    case T_oldcursor of
                        {wrap_reader, _FD, _CONT, _F, I_lastno,_T, _N}->
                            clean(R#conf.s_log_file, I_lastno),
                            loop_clean(R);
                        _Other ->
                            loop_clean(R)
                    end;
                _Other ->
                    %%主进程关掉后可能引起的dets:lookup异常
                    loop_clean(R)
            end
    end.

clean(_S_log_file, 1 ) ->    
    ok;
clean(S_log_file, I)->
    S_filename = S_log_file ++ "." ++ integer_to_list(I), 
    case filelib:is_file( S_filename ) of
        true -> 
            file:delete(S_filename);
        false ->
            nothing
    end,
    clean(S_log_file, I - 1).

%% 工作进程    
loop_worker(Fun)->
    receive
        {P_caller, {run, L_jobs} } ->
            
            reply(P_caller, ok),
            work(Fun, L_jobs),
            rpc(P_caller, work_finish),
            loop_worker(Fun);
        {P_caller, stop} ->
            reply(P_caller, ok),
            stoped
    end.

work(_F, [])->
    ok;
work(F, [H | T]) ->
    F(H),
    work(F, T).    


find_first_fn(R)->
    find_first_fn(R, 1).

find_first_fn(_R, ?DEFAULT_MAX_FILES)->    
    %%没找到只能返回0
    not_found;
find_first_fn(R, I) ->
    case filelib:is_file(R#conf.s_log_file ++ "." ++ integer_to_list(I)) of
        true ->I;
        false ->find_first_fn(R, I + 1)
    end.

    
open_log(R)->
    case wrap_log_reader:open(R#conf.s_log_file) of
        {ok, T_firstcursor} ->
            {ok, T_firstcursor};
        {error,{not_a_log_file, Info}} ->
            %% 先找出第一个文件，并尝试打开日志文件
            case find_first_fn(R) of
                not_found -> 
                    {error, {not_a_log_file, Info}};
                I_first ->
                    wrap_log_reader:open(R#conf.s_log_file, I_first)
            end;
        Error ->
            Error
    end.

open_cursor(R, T_firstcursor)->
    case dets:open_file(R#conf.a_cursor_name, [{file, R#conf.s_cursor_file}]) of
        {ok, _} ->
            case dets:lookup(R#conf.a_cursor_name, ?CURSORKEY) of
                [] -> 
                    {ok, R#conf{t_last_cursor = T_firstcursor}};
                [{_, T_oldcursor}] ->
                    %%根据 T_firstcursor恢复旧的游标状态
                    %% 取 Prim FD 类似:{file_descriptor,prim_file,{#Port<0.691>,372}}
                    {wrap_reader, PFD, _CONT, _FILE, _FN, _T, _N}  = T_firstcursor,
                    %% 用新的Prim FD 取代老的游标中的Prim FD
                    {wrap_reader, _FD, CONT, FILE, FN, T, N} = T_oldcursor,
                    {ok, R#conf{t_last_cursor = {wrap_reader, PFD, CONT, FILE, FN, T, N}}}
            end;
        Error ->
            Error
    end.              
    
save_cursor(R)->
    dets:insert(R#conf.a_cursor_name, {?CURSORKEY, R#conf.t_last_cursor}).


reply(P_caller, U_reply)->
    P_caller ! {reply, P_caller, U_reply}.

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
