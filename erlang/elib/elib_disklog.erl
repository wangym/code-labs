%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%     简化disk_log操作,提供一个异步同步日志系统
%%% Created : 2009-12-4
%%%     流程:
%%%         1.用户通过elib_disklog:putlog不断的写入log文件，文件序号从1...64999.例如:/log/delete.log.1
%%%         2.用户通过elib_disklog_reader不断的读并处理log, 处理的位置信息存入***.cursor文件中,处理后再把处理过的log清除
%%% Example:
%%%     elib_disklog:start(dellog, "./log/delete.log").
%%%     elib_disklog:putlog(dellog, <<"hello1">>).
%%%     elib_disklog:putlog(dellog, <<"hello2">>).
%%%     elib_disklog:stop(dellog).
%%% ------------------------------------------------------------------
-module(elib_disklog).

%% 64M
-define(DEFAULT_MAX_BYTES, 67108864).
%%-define(DEFAULT_MAX_BYTES, 6144).
%% 64M * 64999 = 近39TB, 39TB意思不是当前日志文件的大小限制，而是从有日志以来包括已删除的日志大小，这是系统的一个极限
-define(DEFAULT_MAX_FILES, 64999).

-record(conf, {
        a_name = undefined, 
        s_logfile = undefined,      %% log文件
        i_flush_mode = 2,           %% 刷新日志的方式 0 立即刷新 1 每一秒刷新一次  2让系统自己决定什么时候刷新
        p_flush = undefined
        }).

%% --------------------------------------------------------------------
%% External exports
%% -------------------------------------------------------------------
-export([start/2, start/3, stop/1, putlog/2, status/1]).


%% --------------------------------------------------------------------
%% Function: start/3, start/2
%% Description: 启动
%% Params:  
%%          A_name  	    名称
%%          S_logfile       日志文件名 例如: ./log/delete.log
%%          I_flush_mode    刷新模式 0 立即刷新 1每一秒钟刷新一次 2 让系统自己决定什么时候刷新
%% Returns: {ok, Pid}       |
%%          {repaired,test,{recovered,_},{badbytes,0}}
%%			{error , Reason}
%% -------------------------------------------------------------------
start(A_name, S_logfile)->
    start(A_name, S_logfile, 2).

start(A_name, S_logfile, I_flush_mode)->
    R = #conf{a_name = A_name, s_logfile = S_logfile, i_flush_mode = I_flush_mode},
    case whereis(A_name) of
        undefined->
            Pid = spawn_link(fun()-> loop(#conf{}) end),
            register(A_name, Pid),
            case rpc(A_name, {start, R}) of
                ok -> {ok, Pid};
                {repaired,test,{recovered,_},{badbytes,0}} ->
                    start(A_name, S_logfile);

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
%% Function: stop/1
%% Description: 
%% Params:  
%%          A_name  	名称
%%          B          LOG内容 例如: <<"del 101901000">>
%% Returns: ok
%%          {error, not_start}
%%			{error , Reason}
%% -------------------------------------------------------------------
putlog(A_name, B) when is_binary(B)->
    rpc(A_name, {putlog, B}).

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
loop(#conf{p_flush = P_flush} = R)->
    receive
        {P_caller, {start, R_new} } ->
            case disk_log:open([{name, R_new#conf.a_name}, 
                                {file, R_new#conf.s_logfile},
                                {type, wrap},
                                {size, {?DEFAULT_MAX_BYTES, ?DEFAULT_MAX_FILES}}]) of
                {ok, _} ->
                    P_me = self(),                   
                    %% 启动一个线程每1秒发送一个flush消息给自己
                    P_new_flush = case R_new#conf.i_flush_mode of
                                1 ->spawn_link(fun()->loop_flush([1000, P_me]) end );
                                _ -> undefined
                              end,
                    reply(P_caller, ok),
                    loop(R_new#conf{p_flush = P_new_flush});
                Error ->
                    reply(P_caller, Error)
            end;
        {'EXIT', _Why, P_flush} ->
            P_me = self(),
            P_new = spawn_link(fun()-> loop_flush([1000, P_me]) end),
            loop(R#conf{p_flush = P_new});
        {P_caller, flush} ->
            disk_log:sync(R#conf.a_name),
            reply(P_caller, ok),
            loop(R);
        {P_caller, {putlog, B_log} } ->
            case disk_log:log(R#conf.a_name, B_log) of
                ok ->
                    %% 如果配置是立即刷新, 否则什么都不做,同步到磁盘上再返回ok
                    case R#conf.i_flush_mode of
                        0 -> disk_log:sync(R#conf.a_name);
                        _ -> nothing
                    end,
                    reply(P_caller, ok);
                Error ->
                    reply(P_caller, Error)
            end,
            loop(R);
        {P_caller, status} ->
            reply(P_caller, {ok, R}),
            loop(R);
        {P_caller, stop}->
            disk_log:close(R#conf.a_name),
            reply(P_caller, ok),
            stoped
    end.


loop_flush([I_sleep, Parent])->
    receive
        after I_sleep ->
            rpc(Parent, flush),
            loop_flush([I_sleep, Parent])
    end.

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
