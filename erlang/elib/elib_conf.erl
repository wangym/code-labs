%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :配置文件管理.
%%%		1.所有配置文件都在项目目录/conf下(或 ./, ./conf/, ../, ../conf/
%%%		2.配置文件扩展名必须是.conf
%%%		3.只需要 start 方法打开配置文件，不需要释放.
%%%
%%%     注意: 
%%%          1.如果没有定义配置文件路径会在默认路径下自动搜索
%%%          2.lread_string ...l系列方法，是先读取进程内dict再发消息，以提高速度
%%%
%%% Created : 2009-2-5
%%% Example:
%%%       elib_conf:start(ns).  或elib_conf:start(ns, "../conf/ns.conf").
%%%       elib_conf:read_string(ns, "ns_db.host").
%%%       elib_conf:read_atom(ns, "ns_db.user").
%%%       elib_conf:read_atom(ns, "no", true).
%%%       elib_conf:lread_integer(ns, "ns_db.max_connects").
%%%       elib_conf:stop(ns).
%%% History:
%%%     2009-12-3 更新为elib_conf changqingteng@alibaba-inc.com
%%% --------------------------------------------------------------------
-module(elib_conf).
-define(FINDDIR, ["./", "./conf/", "../", "../conf/"]).

%% --------------------------------------------------------------------
%% External exports
%% --------------------------------------------------------------------

-export([start/1, start/2, stop/1, status/1]).
-export([read_string/2, read_string/3, read_integer/2, read_integer/3, read_atom/2, read_atom/3]).
-export([lread_string/2, lread_string/3, lread_integer/2, lread_integer/3, lread_atom/2, lread_atom/3]).
-export([write_string/3]).

%% --------------------------------------------------------------------
%% Function: start/1, start/2
%% Description: 打开一个配置文件,默认在项目的conf下
%% Params:	A_conf_name	对应配置文件名 例如kvdb.conf A_conf_name = kvdb
%%  		S_file_name 具体文件名 例如: /home/conf/kvdb.conf
%% Returns:	{ok, Pid}		|	
%%			{error, Reason}
%% --------------------------------------------------------------------
start(A_conf_name)->
    %% 防止重复查询文件
    case erlang:whereis(A_conf_name) of
        undefined ->
            %%  默认在./, ../conf, ../conf下找
            S_filename = atom_to_list(A_conf_name) ++ ".conf",
            case find_file(?FINDDIR, S_filename) of
                not_found ->
                    {error, "not found " ++ S_filename ++ ", please copy to " ++ string:join(?FINDDIR, ",")};
                S_path ->
                    start(A_conf_name, S_path)
            end;
        P_old ->
            {ok, P_old}
    end.            


start(A_conf_name, S_file_name)->
	case erlang:whereis(A_conf_name) of
		undefined->
			Pid = spawn_link(fun()->loop([S_file_name, []]) end),
			register(A_conf_name, Pid),
			A_conf_name ! start,
            {ok, Pid};
		P_old ->
            {ok, P_old}
	end.		

%% --------------------------------------------------------------------
%% Function: stop/1
%% Description:
%% Params:	A_conf_name	对应配置文件名 例如kvdb.conf A_conf_name = kvdb
%% Returns:	ok
%%          {error, not_start}
%%			{error, Reason}
%% --------------------------------------------------------------------
stop(A_conf_name)->
    case whereis(A_conf_name) of
        undefined -> {error, not_start};
        Pid -> 
            Pid ! stop,
            stoped
    end.
%% --------------------------------------------------------------------
%% Function: read_atom/2, read_atom/3, lread_atom/2, lread_atom/3
%% Description: 从配置文件中读取一个原子值
%% Params:	A_conf_name	对应配置名 例如: kvdb
%%  		S_key 配置值名
%%			A_default	缺省值
%% Returns:	A_result
%% --------------------------------------------------------------------	
read_atom(A_conf_name, S_key)->
	read_atom(A_conf_name, S_key, unknow).
		
read_atom(A_conf_name, S_key, A_default)->
	S_value = read_string(A_conf_name, S_key, atom_to_list(A_default)),
	erlang:list_to_atom(S_value).

lread_atom(A_conf_name, S_key) ->
    lread_atom(A_conf_name, S_key).
    
lread_atom(A_conf_name, S_key, A_default)->
	S_value = lread_string(A_conf_name, S_key, atom_to_list(A_default)),
	erlang:list_to_atom(S_value).

    

%% --------------------------------------------------------------------
%% Function: read_integer/2, read_integer/3, lread_integer/2, lread_integer/3
%% Description: 从配置文件中读取一个整型值
%% Params:	A_conf_name	对应配置名 例如: kvdb
%%  		S_key 配置值名
%%			I_default	缺省值
%% Returns:	I_result
%% --------------------------------------------------------------------
read_integer(A_conf_name, S_key)->
	read_integer(A_conf_name, S_key, 0).

	
read_integer(A_conf_name, S_key, I_default)->
	S_value = read_string(A_conf_name, S_key, integer_to_list(I_default)),
	case string:to_integer(S_value) of
		{I_value,_} ->
			I_value;
		_Error ->
			I_default			
	end.    

lread_integer(A_conf_name, S_key) ->
    lread_integer(A_conf_name, S_key, 0).

lread_integer(A_conf_name, S_key, I_default) ->
   	S_value = lread_string(A_conf_name, S_key, integer_to_list(I_default)),
	case string:to_integer(S_value) of
		{I_value,_} ->
			I_value;
		_Error ->
			I_default			
	end.    

 
%% --------------------------------------------------------------------
%% Function: read_string/2, read_string/3, lread_string/2, lread_string/3
%% Description: 
%%      从配置文件中读取一个字符串值
%%      lread_string 先从本地dict读取可以减少发送消息的开销，适合只读不修改的配置量
%% Params:	A_conf_name	对应配置名 例如: kvdb
%%  		S_key 配置值名
%%			S_default	缺省值
%% Returns:	S_result	
%% --------------------------------------------------------------------	
read_string(A_conf_name, S_key)->
	read_string(A_conf_name, S_key, "").	
		
read_string(A_conf_name, S_key, S_default)->
	A_conf_name ! {read, S_key, self()},
	receive
		{ok, undefiend} ->
			S_default;
		{ok, S_value} ->
			S_value		
	end.			

lread_string(A_conf_name, S_key) ->
    lread_string(A_conf_name, S_key, "").

lread_string(A_conf_name, S_key, S_default)->
    S_localkey = atom_to_list(A_conf_name) ++ "." ++ S_key,
    case erlang:get(S_localkey) of
        elib_conf_undefined ->
            undefined;
        undefined->
            case read_string(A_conf_name, S_key, S_default) of
                undefined -> 
                    erlang:put(S_localkey, elib_conf_undefined),
                    undefined;
                Other ->
                    erlang:put(S_localkey, Other),
                    Other    
            end;
        S_val -> 
            S_val   
    end.

%% --------------------------------------------------------------------
%% Function: write_string/3
%% Description: 
%%      更改一个配置，只修改内存部分，文件并不修改
%% Params:	A_conf_name	对应配置名 例如: kvdb
%%  		S_key 配置值名
%%			S_value	新的值
%% Returns:	ok  |
%%          {error, not_found}
%% --------------------------------------------------------------------	
write_string(A_conf_name, S_key, S_value)->
	A_conf_name ! {write, S_key, S_value, self()},
	receive
		ok ->
			ok;
        Error ->
            Error
	end.		
%% --------------------------------------------------------------------
%% Function: status/1
%% Description: 
%%      显示当前状态
%% Params:	A_conf_name	对应配置名 例如: kvdb
%% Returns:	{ok, List}  |
%%          {error, Reason}
%% --------------------------------------------------------------------	
status(A_conf_name)->
    A_conf_name ! {status, self()},
    receive
        Any -> Any
    end.


%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------

loop([SConfFile, LConf])->
	receive
		start ->
			LNew = load_conf(SConfFile),
			loop([SConfFile, LNew]);
		stop ->
			ok;
        {status, PFrom} ->
            PFrom ! {ok, LConf},
            loop([SConfFile, LConf]);
		{read, SKey, PFrom} ->
			PFrom ! {ok, find_name(SKey, LConf)},
			loop([SConfFile, LConf]);
		{write, SKey, SValue, PFrom} ->
            case find_name(SKey, LConf) of
                undefined ->
                    PFrom ! {error, not_found},
                    loop([SConfFile, LConf]);
                SOld ->
                    PFrom ! ok,
                    L_new =(LConf -- [{SKey, SOld}]) ++ [{SKey, SValue}], 
                    loop([SConfFile, L_new])
            end;
		{save, PFrom} ->
			PFrom ! { ok, unknow},
			loop([SConfFile, LConf])
	end.

	
	
	
	

%%
%% @return list 
%%
load_conf(SFileName) when is_list(SFileName) ->
	{ok, Bin} = file:read_file(SFileName),
	load_conf(Bin);
load_conf(Bin)->
	parse_from_list(binary_to_list(Bin)).
 
parse_from_list(SBody)->
	Lines = string:tokens(SBody, "\r\n"),
	parse_lines(Lines, []).
	
parse_lines([], LRet)->
	LRet;
parse_lines([ H | T], LRet)->
	[SFirst | _] = H,
	case SFirst == $# of
		true ->
			parse_lines(T, LRet);
		false ->			
            case string:tokens(H, "  ") of
                ["include", S_include_file] ->
                    parse_lines(T, LRet ++ load_conf(S_include_file));
                _Other ->                    
        			parse_lines(T, LRet ++ [list_to_tuple(string:tokens(H, "="))])
            end                    
	end.

%%
%% @return undefined | string
%%	
find_name(_SFind, [])->
	undefiend;
find_name(SFind, [ {SName, SValue} | _T]) when SFind == SName ->
	SValue;
find_name(SFind, [ _H | T])->
	find_name(SFind, T).

find_file([], _)->
    not_found;
find_file([ S_dir | T], S_filename)->
    case filelib:is_file(S_dir ++ S_filename) of
        true -> S_dir ++ "/" ++ S_filename;
        false -> find_file(T, S_filename)
    end.


	    
