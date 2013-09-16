-module(elib_logger_test).
%%
%% 因逻辑复杂只对默认配置进行测试
%% log_level = info
%%
%% make:all(),l(elib_logger_test), l(elib_logger), elib_logger_test:test().
%%
-define(DEBUG(S_format, L_params),  elib_logger:debug(?MODULE, ?LINE, S_format, L_params)).
-define(INFO(S_format, L_params),   elib_logger:info(?MODULE, ?LINE, S_format, L_params)).
-define(ERROR(S_format, L_params),  elib_logger:error(?MODULE, ?LINE, S_format, L_params)).


-export([test/0]).

test()->
    elib_logger:start(),
    io:format("~w~n",[elib_logger:status()]),
    ?ERROR("test error",[]),
    ?ERROR("test error ~w",[erlang:time()]),
    ?ERROR("test error exception",[erlang:date()]),

    ?INFO("test info",[]),
    ?INFO("test info ~w", [erlang:time()]),

    ?INFO("console not info message begin",[]),
    elib_logger:disable_console(),
    ?INFO("test info ~w..", [erlang:time()]),
    elib_logger:enable_console(),
    ?INFO("console not info message end", []),

    ?INFO("not debug message output begin",[]),
    ?DEBUG("test debug.",[]),
    ?DEBUG("test debug ~w.",[erlang:time()]),
    ?INFO("not debug message output end",[]),
    ?INFO("debug message output begin",[]),
    elib_logger:enable_debug(),
    ?DEBUG("test debug ~w..",[erlang:time()]),
    ?INFO("debug message output end",[]),

    ?INFO("not debug message output ( disable_debug ) begin",[]),
    elib_logger:disable_debug(),
    ?INFO("not debug message output ( disable_debug ) end",[]),
    
    I_line = ?LINE,
    ?INFO("not debug message output ( enable debug ~w ) begin",[I_line - 3]),
    elib_logger:enable_debug(?MODULE, 0, I_line - 3),
    ?DEBUG("test..................",[]),
    ?INFO("not debug message output ( enable debug ~w ) end",[I_line - 3]),

    ?INFO("debug message output ( enable debug ~w ) begin",[I_line + 10]),
    elib_logger:enable_debug(?MODULE, 0,  I_line + 10),
    ?DEBUG("test..................",[]),
    ?INFO("debug message output ( enable debug ~w ) end",[I_line + 10]),

    ?INFO("not debug message output ( disable_debug ~w ) begin",[?MODULE]),
    elib_logger:disable_debug(?MODULE),
    ?DEBUG("test..................",[]),
    ?INFO("not debug message output ( disable_debug ~w ) end",[?MODULE]),
 
    Stat = elib_logger:status(),

    elib_logger:stop(),
    Stat.
