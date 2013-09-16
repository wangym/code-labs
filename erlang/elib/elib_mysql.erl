%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%     MYSQL ERLANG DRIVER
%%%     是对网上的 erlang driver简单封装
%%%     以后有计划用C写port方式
%%%     !!!限制,每个应用只能有一个实例。即在一个应用中只能访问一台mysql host
%%%
%%% Created : 2010-2-3
%%% Example:
%%% History:
%%% --------------------------------------------------------------------
-module(elib_mysql).
-define(DEFAULT_MAX_CONNECTS, 10).
-define(DEFAULT_CHARSET, utf8).

%% --------------------------------------------------------------------
%% External exports
%% --------------------------------------------------------------------

-export([start/3, start/4, start/5, stop/0, execute/1]).
-export([encode_mysql_charset/1]).

%% --------------------------------------------------------------------
%% Function: start/1, start/2
%% Description: 打开一个配置文件,默认在项目的conf下
%% Params:	A_conf_name	对应配置文件名 例如kvdb.conf A_conf_name = kvdb
%%  		S_file_name 具体文件名 例如: /home/conf/kvdb.conf
%% Returns:	{ok, Pid}		|	
%%			{error, Reason}
%% --------------------------------------------------------------------
start(S_host, S_user, S_pass)->
    start(S_host, S_user, S_pass, "mysql").

start(S_host, S_user, S_pass, S_db)->
    start(S_host, S_user, S_pass, S_db, []).

start(S_host, S_user, S_pass, S_db, L_options)->
    case whereis(?MODULE) of
        undefined ->
            Pid = spawn_link(fun()->loop() end),
            Pid ! {self(), start,  {S_host, S_user, S_pass, S_db, L_options}},
            receive
                ok ->
                    register(?MODULE, Pid),
                    {ok, Pid};
                Error ->
                    Error
            end;
        P_old ->
            {ok, P_old}
    end.

%% --------------------------------------------------------------------
%% Function: stop/0
%% Description: 停止
%% Returns: ok
%% --------------------------------------------------------------------    
stop()->
    case whereis(?MODULE) of
        undefined->
            {error, not_start};
        Pid ->
            Pid ! {self(), stop},
            receive
                ok -> ok;
                Error -> Error
            end
    end.


%% --------------------------------------------------------------------
%% Function: execute/1
%% Description:
%%   执行sql语句
%% Params:
%%   B_sql  sql语句例如:<<"SELECT * FORM user;">>
%% Returns:
%%   {ok, L_rows}     |
%%   {ok, I_affected} |
%%   {error, Reason}
%% --------------------------------------------------------------------      
execute(B_sql)->
    case elib_mysql_impl:fetch(?MODULE, B_sql) of
        {data,{mysql_result,_,Rows,_,_}} ->
            {ok, Rows};
        {updated,{mysql_result,_,_,AffectedRows,_}} ->
            {ok, AffectedRows};
        Error ->
            Error

    end.

%% --------------------------------------------------------------------
%% Function: encode_mysql_charset/1
%% Description:
%%   对binary字符串进行mysql相关字符转义
%% Params:
%%   B      binary 字符串二进制
%% Returns:
%%   Binary 
%% --------------------------------------------------------------------      
encode_mysql_charset(B)->
    encode_mysql_charset(B, <<>>).

encode_mysql_charset(<<>>, Ret)->
    Ret;
%% 0 -> "\0"    
encode_mysql_charset(<<0, T/binary>>, Ret) ->
    encode_mysql_charset(T, <<Ret/binary, 92,"0">>);
%% 回车 -> "\n"    
encode_mysql_charset(<<10, T/binary>>, Ret)->
    encode_mysql_charset(T, <<Ret/binary, 92,"n">>);
%% 换行 -> "\r"    
encode_mysql_charset(<<13, T/binary>>, Ret)->
    encode_mysql_charset(T, <<Ret/binary, 92,"r">>);
%% ' => "\\'"    
encode_mysql_charset(<<39, T/binary>>, Ret)->
    encode_mysql_charset(T, <<Ret/binary, 92, 39>>);
%% \ => "\\"    
encode_mysql_charset(<<92, T/binary>>, Ret)->
    encode_mysql_charset(T, <<Ret/binary, 92, 92>>);
%%    %% 以下代码防止重复转义
%%    <<F, FT/binary>> = T,
%%    case F of
%%        %% "0"
%%        48 ->encode_mysql_charset(FT, <<Ret/binary, 92, "0">>);
        %% "n"
%%        110 ->encode_mysql_charset(FT, <<Ret/binary, 92, "n">>);
%%        %% "r"
%%        114 ->encode_mysql_charset(FT, <<Ret/binary, 92, "r">>);
%%        39 ->encode_mysql_charset(FT, <<Ret/binary, 92, 39>>);
%%        92 ->     encode_mysql_charset(FT, <<Ret/binary, 92, 92>>);
%%        _ -> 
%%            encode_mysql_charset(T, <<Ret/binary, 92, 92>>)
%%    end;
encode_mysql_charset(<<H, T/binary>>, Ret)->
    encode_mysql_charset(T, <<Ret/binary, H>>).
    
 

%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------

%%
%% 因为elib_mysql_impl的退出有些问题引起异常, 暂时起一个代理进程. 把mysql相关进程连到该代理进程
%% 代理进程退出则相关进程都响应退出
%% changgb
%% 2010-2-23
%%
loop()->
    receive
        {P_caller, start, {S_host, S_user, S_pass, S_db, L_options}} ->
            A_charset = proplists:get_value(charset, L_options, ?DEFAULT_CHARSET),
            I_max_connects = proplists:get_value(max_connects, L_options, ?DEFAULT_MAX_CONNECTS),
            case elib_mysql_impl:start_link(mysql_pool, S_host, undefined, S_user, S_pass, S_db, undefined, A_charset) of
                {ok, _Pid} ->
                    init_connects(I_max_connects, [S_host, S_user, S_pass, S_db]),
                    P_caller ! ok; 
                Error ->
                    P_caller ! {error, Error}
            end,
            loop();
        {P_caller, stop} ->
            P_caller ! ok,
            exit(whereis(mysql_dispatcher), kill),
            stoped
    end.            
            
    
init_connects(0, _)->
    ok;
init_connects(I_count, [S_host, S_user, S_pass, S_db])->
    elib_mysql_impl:connect(?MODULE, S_host, undefined, S_user, S_pass, S_db, true),
    init_connects(I_count - 1, [S_host, S_user, S_pass, S_db]).

    
