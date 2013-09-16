%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description : 
%%%     系统日志主要职责如下:
%%%     1.读取当前应用下的logger.conf
%%%         1.1日志文件分区方式(暂时只支持每天一个,例如: kvdb_20100302.log
%%%         1.2日志文件前缀 例如:kvdb
%%%         1.3日志延迟写的buffer大小及延迟时间
%%%         1.4日志格式例如:%LEVEL\t%DATETIME\t%PID~t%MODULE:%LINE~t%MESSAGE\r\n
%%%         1.6日志默认检查配置文件logger.conf的间隔时间,默认:3秒 (以后实现)
%%%         1.7定期检查logger.conf 如果已经修改重新reload配置文件(只reload输出级别定义部分)
%%%             目的方便调试在线服务:如有问题修改配置文件输出更多的debug信息,找到问题后关闭debug输出
%%%             (以后实现,现阶段通过控制台控制)
%%%         1.8默认日志输出级别
%%%     2.日志级别:ERROR > INFO > DEBUG, 默认INFO
%%%     3.可以智能选择是否同时输出到控制台,通过判断命令行或者
%%%     4.LOG输出级别可以控制到MOUDLE和对应的行,
%%%         例如: kvdb_sp_mysql:10-30:debug kvdb_sp_mysql中10-30行间代码以debug级别输出
%%%
%%%
%%% Created : 2010-3-9
%%% --------------------------------------------------------------------
-module(elib_logger).

%% 配置文件, 当前目录下的logger.conf
-define(CONF_FILE, "logger.conf").

%% debug 进程名, 如果存在则输出debug信息 目的加快无debug时处理速度
-define(DEBUG_NAME, elib_logger_debug).

-define(DEFAULT_LOG_DIR , "log").
-define(DEFAULT_LOG_PREFIX, "logger").
-define(DEFAULT_DELAY_SIZE, 262144).
-define(DEFAULT_DELAY,  2000).
%% [ERROR]  2009-12-09 17:41:12 <0.291.0>   kvdb_sp_server:145  insert failed...
-define(DEFAULT_LAYOUT,  "[~w]  ~s  ~w  ~w:~w   ").
-define(LAYOUT_DATA,    [R_log#log.a_log_level,datetime2string(R_log#log.t_datetime), R_log#log.pid, R_log#log.a_module_name,R_log#log.i_line_no]).
-define(DEFAULT_LOGLEVEL, info).    

%% 最大行号,只要不可能出现就可以
-define(MAX_LINE_NO,    99999999).
%% 间隔多长时间检查下时间以便于更改记录文件
-define(TIMER_INTERVAL, 5000).

-record(conf,{
        fd = undefined,                         %% log文件句柄
        s_log_dir = ?DEFAULT_LOG_DIR,           %% log所在文件夹 
        s_log_prefix = ?DEFAULT_LOG_PREFIX,     %% log文件前缀
        t_log_create_date = undefined,          %% log建立时时间,便于和当前时间比对.如果过了零点更换log文件
        i_delay_size = ?DEFAULT_DELAY_SIZE,     %% log文件延迟写字节设置 256K 单位:字节
        i_delay = ?DEFAULT_DELAY,                         %% log文件延迟写延迟时间设置 2秒 单位:毫秒
        s_layout = ?DEFAULT_LAYOUT,             %% 输出格式
        a_log_level = ?DEFAULT_LOGLEVEL,                     %% 默认日志输出级别
        %% 模块配置 例: [{kvdb_sp_mysql,{debug, 0, 0},{kvdb_sp_server,{info, 10, 30}}]
        %% 1.kvdb_sp_mysql模块输出级别是debug
        %% 2.kvdb_sp_server模块的第10,30行间输出为info级别, 其他行同缺省级别配置
        %% 3.其他模块同缺省配置
        l_module_conf = [],
        b_output_console = false,               %%是否输出到控制台
        r_timer = undefined,                    %%定时检查时间的消息监控进程记录
        p_debug = undefined                     %%debug空进程PID
    }).

-record(log, {
            a_log_level = debug,
            a_module_name = undefined,
            t_datetime = {{2009,01,01},{0,0,0}},    %%时间 默认:2009-01-01 00:00:00
            pid = undefined,
            i_line_no = 0,
            s_format = "",
            l_data = []
        }).


-export([start/0, stop/0, status/0]).
-export([disable_console/0, enable_console/0]).
-export([enable_debug/0, enable_debug/1, enable_debug/3, disable_debug/0,
        disable_debug/1]).
%%-export([load_conf/0]).
-export([log/5, sync_log/5]).
-export([debug/3, debug/4, info/3, info/4, error/3, error/4]).

%% --------------------------------------------------------------------
%% Function: start/0
%% Description: 
%% Params:	
%% Returns:	{ok, Pid} 	|
%%			{error, Reason}
%% --------------------------------------------------------------------    
start()->
    case whereis(?MODULE) of
        undefined ->
            {ok, R_new} = load_conf(),
            Pid = spawn_link(fun()-> loop(R_new) end),
            ok = rpc(Pid, start),
            register(?MODULE, Pid),
            {ok, Pid};
        Pid ->
            {ok, Pid}
    end.

%% --------------------------------------------------------------------
%% Function: stop/0
%% Description: 
%% Params:	
%% Returns:	ok 					|
%%			{error, not_start}
%% --------------------------------------------------------------------
stop()->
    rpc(?MODULE, stop).

%% --------------------------------------------------------------------
%% Function: log/5, sync_log/5
%%           debug/3, debug/4, info/3, info/4
%error/3,error/r
%% Description: 
%%      记录LOG, 
%%      log/5 是异步方式 
%%      sync_log/5是同步方式
%% Params:	
%%      A_level         [debug | info | error]
%%      A_module_name   输出LOG的模块名
%%      I_line_no       行号
%%      S_format        格式 例如: "~w ~w"
%%      L_data          数据 例如: [erlang:date(), erlang:time()]
%% Returns:	ok 					|
%%			{error, not_start}
%% --------------------------------------------------------------------
debug(A_module_name, I_line_no, S_format)->
    debug(A_module_name, I_line_no, S_format, []).

debug(A_module_name, I_line_no, S_format, L_data) ->
    case whereis(?DEBUG_NAME) of
        undefined -> 
            ok;
        _ ->
            log(debug, A_module_name, I_line_no, S_format, L_data)
    end.

info(A_module_name, I_line_no, S_format)->
    info(A_module_name, I_line_no, S_format, []).

info(A_module_name, I_line_no, S_format, L_data) ->
    log(info, A_module_name, I_line_no, S_format, L_data).

error(A_module_name, I_line_no, S_format)->
    error(A_module_name, I_line_no, S_format, []).

error(A_module_name, I_line_no, S_format, L_data) ->
    log(error, A_module_name, I_line_no, S_format, L_data).


%% 同步输出日志
sync_log(A_level, A_module_name, I_line_no, S_format, L_data)->
    P_caller = self(),
    R_log = #log{
        a_log_level = A_level,
        a_module_name = A_module_name,
        t_datetime = {erlang:date(), erlang:time()},
        pid = P_caller,
        i_line_no = I_line_no,
        s_format = S_format,
        l_data = L_data
    },
    rpc(?MODULE, {log, R_log}).

%% 异步输出日志发送方不用等待
log(A_level, A_module_name, I_line_no, S_format, L_data)->
    P_caller = self(),
    R_log = #log{
        a_log_level = A_level,
        a_module_name = A_module_name,
        t_datetime = {erlang:date(), erlang:time()},
        pid = P_caller,
        i_line_no = I_line_no,
        s_format = S_format,
        l_data = L_data
    },
    case whereis(?MODULE) of
        undefined ->
            {error, not_start};
        _ ->
            ?MODULE ! {P_caller, {asyn_log, R_log}},
            ok
    end.            

%% --------------------------------------------------------------------
%% Function: status/1
%% Description: 
%% Params:	
%% Returns:	
%%      {ok, Record}    |
%%      {error, not_start}
%% --------------------------------------------------------------------
status()->
    rpc(?MODULE, status).

%% --------------------------------------------------------------------
%% Function: disable_console/0, enable_console/0
%% Description: 
%%      打开或关闭控制台输出
%% Params:	
%% Returns:	
%%      ok                  |
%%      {error, not_start}
%% --------------------------------------------------------------------
disable_console()->
    rpc(?MODULE, disable_console).

enable_console()->
    rpc(?MODULE, enable_console).

%% --------------------------------------------------------------------
%% Function: enable_debug/0, enable_debug/1, enable_debug/3
%%           disable_debug/0, disable_debug/1
%% Description: 
%%      打开或关闭debug信息输出, 但如果配置文件中设置了a_log_level = debug
%%      则一定会输出debug信息,即配置文件优先级高
%%      只有指定模块,指定行号范围的debug才会输出
%% Params:	
%%      A_module_name   模块名
%%      I_begin         起始行号
%%      I_end           截止行号
%% Returns:	
%%      ok                  |
%%      {error, not_start}
%% --------------------------------------------------------------------
enable_debug()->
    enable_debug(all).

enable_debug(A_module_name)->
    enable_debug(A_module_name, 0, ?MAX_LINE_NO).

enable_debug(A_module_name, I_begin, I_end)->
    rpc(?MODULE, {enable_debug, A_module_name, I_begin, I_end}).

disable_debug()->
    disable_debug(all).

disable_debug(A_module_name)->
    rpc(?MODULE, {disable_debug, A_module_name}).


%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------
loop(R)->
    receive
        {P_caller, start} ->
            {ok, Fd} = open_log(R),
            %%检查是否有shell, 如果erl没有使用-noshell参数,则使用io:format输出
            B_output_console = (init:get_argument(noshell) == error),
            %%5秒检查下时间是否过零点               
            {ok, Timer} = timer:send_interval(?TIMER_INTERVAL, self(), checktime),
            %%
            P_debug = spawn_link(fun()->debug_loop() end),
            case R#conf.a_log_level of
                debug ->
                    register(?DEBUG_NAME, p_debug);
                _ ->
                    nothing
            end,
            reply(P_caller, ok),
            loop(R#conf{fd = Fd, 
                        t_log_create_date = erlang:date(), 
                        r_timer = Timer, 
                        b_output_console = B_output_console,
                        p_debug = P_debug
                    });
        checktime ->
            D_old = R#conf.t_log_create_date,
            case erlang:date() of
                D_old ->
                    loop(R);
                D_new ->
                    %% 时间不对换文件
                    file:close(R#conf.fd),
                    {ok, Fd} = open_log(R),
                    loop(R#conf{fd = Fd, t_log_create_date = D_new})           
            end;
        {P_caller, {log, R_log}} ->
            output_log(R, R_log),
            reply(P_caller, ok),
            loop(R);
        {_P_caller, {asyn_log, R_log}} ->
            output_log(R, R_log),
            loop(R);    
        {P_caller, disable_console} ->
            reply(P_caller, ok),
            loop(R#conf{b_output_console = false});
        {P_caller, enable_console} ->
            reply(P_caller, ok),
            loop(R#conf{b_output_console = true}); 

        {P_caller, {enable_debug, A_module_name, I_begin, I_end}} ->
            R_new = case A_module_name of
                        all -> 
                            R#conf{l_module_conf = [{all, {error, 0, ?MAX_LINE_NO}}]};
                        _ ->
                            L_old = proplists:delete(all,
                                R#conf.l_module_conf),
                            L_new = proplists:delete(A_module_name, L_old) ++
                            %% 最高级别强制输出
                            [{A_module_name, {error,I_begin, I_end}}],
                            R#conf{l_module_conf = L_new}
                    end,
            register_debug(R_new),
            reply(P_caller, ok),
            loop(R_new);
        {P_caller, {disable_debug, A_module_name}} ->
            L_new = case A_module_name of
                        all -> 
                            [];
                        _ -> 
                            proplists:delete(A_module_name, R#conf.l_module_conf)
                    end,
            R_new = R#conf{l_module_conf = L_new}, 
            unregister_debug(R_new),
            reply(P_caller, ok),
            loop( R_new );
        {P_caller, status} ->
            reply(P_caller, {ok, R}),
            loop(R);
        {P_caller, stop} ->
            file:close(R#conf.fd),
            timer:cancel(R#conf.r_timer),
            R#conf.p_debug ! stop,
            reply(P_caller, ok),
            stoped                
    end.

%% 空进程只是为了register个名字
debug_loop()->
    receive
        stop -> stoped
    end.

register_debug(R)->
    case R#conf.a_log_level of
        debug ->
            ok;
        _ ->
            case whereis(?DEBUG_NAME) of
                undefined ->
                    register(?DEBUG_NAME, R#conf.p_debug);
                _ ->
                    ok
            end
    end.

unregister_debug(R)->
    case R#conf.a_log_level of
        debug ->
            ok;
        _ ->
            case (whereis(?DEBUG_NAME) =/= undefined) and (R#conf.l_module_conf ==
                    []) of
                false ->
                    ok;
                true ->
                    unregister(?DEBUG_NAME),
                    ok
            end
    end.

%% --------------------------------------------------------------------
%% Function: output_log/2
%% Description: 
%%      输出日志
%%      1.判断在当前配置下是否应该输出
%%      2.转换成字符串
%%      3.是否输出到控制台
%%      4.输出到文件
%%      5.ok
%% Params:	
%%      R_conf      conf record
%%      R_log       log record
%% Returns:	
%%      ok
%% --------------------------------------------------------------------
output_log(R_conf, R_log)->
    A_log_level = get_level(R_conf, R_log),
    case (level2int(A_log_level) >= level2int(R_conf#conf.a_log_level)) of
        true ->
            %% 格式化错误为字符串
            S_log = case catch(io_lib:format(R_conf#conf.s_layout ++
                        R_log#log.s_format ++ "\r\n",?LAYOUT_DATA ++
                        R_log#log.l_data)) of
                    {'EXIT', FormatError} ->
                        io_lib:format("~w\r\n",[FormatError]);
                    Str ->
                        Str
                    end,
            %% 输出到控制台
            case R_conf#conf.b_output_console of
                true ->
                    io:format("~s",[S_log]);
                false ->
                    nothing
            end,
            %% 输出到文件
            ok = file:write(R_conf#conf.fd, S_log),
            ok;
        false ->
            ok
    end.

%% --------------------------------------------------------------------
%% Function: open_log/1
%% Description: 
%%      打开日志文件,返回句柄
%%      文件名类似kvdb_20090102.log   前缀 + "_" + 时间
%% Params:	
%%      R_conf      conf record
%% Returns:	
%%      {ok, FileHandle}
%% --------------------------------------------------------------------
open_log(R_conf)->
    {Y,M,D} = erlang:date(),
    S_filename = R_conf#conf.s_log_dir ++ "/" ++ R_conf#conf.s_log_prefix ++ "_" ++
            date2string_int(Y) ++ date2string_int(M) ++ date2string_int(D) ++ ".log",
    file:open(S_filename ,[delayed_write, append,{delayed_write, R_conf#conf.i_delay_size, R_conf#conf.i_delay}]).



%% --------------------------------------------------------------------
%% Function: datetime2string/1
%% Description: 
%%      把时间格式化输出为字符串
%%
%%      68> elib_util:datetime2string().
%%      "2010-03-09 09:58:33"
%%      
%% Params:	
%%      Date        时间 {erlang:date(), erlang:time()}
%% Returns:	
%%      string      例: "2009-01-03 23:49:32"
%% --------------------------------------------------------------------
datetime2string({{Y,M,D},{H,I,S}})->
    date2string_int(Y) ++ "-" ++ date2string_int(M) ++ "-" ++ date2string_int(D) ++ " " ++
    date2string_int(H) ++ ":" ++ date2string_int(I) ++ ":" ++date2string_int(S).

date2string_int(I) when I >= 10 ->
    integer_to_list(I);
date2string_int(I) ->
    "0" ++ integer_to_list(I).

%% --------------------------------------------------------------------
%% Function: level2int
%% Description: 
%%      把错误级别转化成整型
%% Params:	
%%      A_log_level     [error | debug | info]
%% Returns:	
%%      int()           [1..3]
%% --------------------------------------------------------------------	
level2int(error)->
    3;
level2int(info)->
    2;
level2int(debug)->
    1.
    
%% --------------------------------------------------------------------
%% Function: get_level
%% Description: 
%%      根据配置记录及模块名,行号计算该日志输出级别
%% Params:	
%%      R_conf          配置记录
%%      R_log           LOG记录
%% Returns:	
%%      A_log_level     [error | debug | info]
%% --------------------------------------------------------------------	
get_level(R_conf, R_log) ->
    Default = proplists:get_value(all, R_conf#conf.l_module_conf), 
    case proplists:get_value(R_log#log.a_module_name,
            R_conf#conf.l_module_conf, Default) of
        undefined ->
            % 如果没有专门配置 LOG级别
            R_log#log.a_log_level;
        {A_level, I_begin, I_end} ->
            % 如果有配置且在指定行号之内则使用定制配置级别
            case (R_log#log.i_line_no >= I_begin) and (R_log#log.i_line_no =< I_end) of
                true ->
                    A_level;
                false ->
                    R_log#log.a_log_level                       
            end              
    end.
    
    
%% --------------------------------------------------------------------
%% Function: load_conf/0
%% Description: 
%%      从配置文件中读取一个记录
%% Params:	
%% Returns:	
%%      {ok, R}             conf record
%%      {error, Reason}     
%% --------------------------------------------------------------------	
load_conf()->
    case file:read_file(?CONF_FILE) of
        {ok, B} ->
            L_prop = load_conf(B, <<>>, []),
            R = #conf{
                    s_log_dir = proplists:get_value("log_dir", L_prop, ?DEFAULT_LOG_DIR),
                    s_log_prefix = proplists:get_value("log_prefix", L_prop, ?DEFAULT_LOG_PREFIX),
                    i_delay_size =
                    list_to_integer(proplists:get_value("delay_size", L_prop,
                            integer_to_list(?DEFAULT_DELAY_SIZE))),
                    i_delay = list_to_integer(proplists:get_value("delay",
                            L_prop, integer_to_list(?DEFAULT_DELAY))),
                    s_layout = proplists:get_value("layout", L_prop, ?DEFAULT_LAYOUT),
                    a_log_level = list_to_atom(proplists:get_value("loglevel",
                            L_prop, atom_to_list(?DEFAULT_LOGLEVEL)))
                },
            {ok, R};
        {error, enoent} ->
            {ok, #conf{}};
        Error ->
            Error
    end.

load_conf(<<>>, _Line, List)->
    List;
load_conf(<<13,T/binary>>, Line, List)->
    load_conf(T, Line, List);
load_conf(<<10,T/binary>>, Line, List)->
    case Line of
        <<>> ->
            load_conf(T, <<>>, List);
        <<35:8, _T/binary>> ->
            %% # 忽略
            load_conf(T, <<>>, List);
        _ ->
            S = binary_to_list(Line),
            L = string:tokens(S, "="),
            case length(L) of
                2 -> 
                    load_conf(T, <<>>, List ++ [list_to_tuple(L)]);
                _ ->
                    load_conf(T, <<>>, List)
            end                
    end;            
load_conf(<<H:8, T/binary>>, Line, List)->
    load_conf(T, <<Line/binary, H>>, List).


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
     

