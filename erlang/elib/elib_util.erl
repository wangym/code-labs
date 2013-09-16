%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%     elib提供的公共工具类
%%%     !!! elib本身的模块为保证独立性(每个elib模块不依赖,复制就可以使用)尽量不要调用elib_util的方法
%%%
%%% Created : 2009-2-21
%%% Example:
%%% History:
%%% --------------------------------------------------------------------
-module(elib_util).

%% 1个小时 近乎永久
-define(DEFAULT_RPC_TIMEOUT, 3600000).

%% --------------------------------------------------------------------
%% External exports
%% --------------------------------------------------------------------

-export([rpc/2, rpc/3, reply/2]).
-export([md5_string/1]).
-export([to_int/1]).
-export([list_partition/2]).
-export([binary_tokens/2, binary_join/2, binary2hex/1]).
-export([time2string/0, time2string/1, date2string/0, date2string/1, datetime2string/0, datetime2string/1]).
-export([term2string/1, term2binary/1]).
-export([eval/1, catch_apply/2, catch_apply/3]).
-export([is_ip/1, get_server_ip/0]).


%% --------------------------------------------------------------------
%% Function: get_server_ip/0
%% Description: 
%%      获取本机IP地址
%% Params: 
%%      
%% Returns: 
%%      {ok, S_ip}  例如: {ok, "10.16.127.3"}
%%      {error, Reason} 
%% --------------------------------------------------------------------
get_server_ip()->
    {ok, S_hostname} = inet:gethostname(),
    case inet:getaddr(S_hostname, inet) of
        {ok, {IP1, IP2, IP3, IP4}} ->
            {ok, integer_to_list(IP1) ++ "." ++ integer_to_list(IP2) ++ "." ++ integer_to_list(IP3) ++ "." ++ integer_to_list(IP4)};
        Error ->
            Error
    end.

%% --------------------------------------------------------------------
%% Function: is_ip/1
%% Description: 
%%      判断是否是个IP地址
%% Params: 
%%      S_ip
%% Returns: 
%%      true | false
%% --------------------------------------------------------------------
is_ip(S_ip)->
    L = string:tokens(S_ip, "."),
    is_ip_number(L).

is_ip_number([])->
    true;
is_ip_number([ H | T]) ->    
    case string:to_integer(H) of
        {error, _Reason} ->
            false;
        {I, []} ->
            case (I >= 0) and (I =< 255) of
                true ->
                    is_ip_number(T);
                false ->
                    false
            end;
        _Other ->
            false
    end.

%% --------------------------------------------------------------------
%% Function: catch_apply/1
%% Description: 
%%      比较安全的erlang:apply/3方法,捕获了很多异常
%% Params: 
%%      Module      atom() | binary
%%      Method      atom() | binary
%%      Args        list() | binary
%% Returns: 
%%      {error, Reason}                 |
%%      {ok, Result}
%% --------------------------------------------------------------------
catch_apply(Module, Method)->
    catch_apply(Module, Method, []).

catch_apply(Module, Method, Args) when is_binary(Module) ->
    case catch( binary_to_term(Module) ) of
        {'EXIT', Reason} ->
            {error, Reason};
        V ->
            catch_apply(V, Method, Args)
    end;
catch_apply(Module, Method, Args) when is_binary(Method)->
    case catch( binary_to_term(Method) ) of
        {'EXIT', Reason} ->
            {error, Reason};
        V ->
            catch_apply(Module, V, Args)
    end;
catch_apply(Module, Method, Args) when is_binary(Args) ->
    case catch( binary_to_term(Args) ) of
        {'EXIT', Reason} ->
            {error, Reason};
        V ->
            catch_apply(Module, Method, V)
    end;
catch_apply(Module, _Method, _Args) when Module == undefined ->
    {error, module_is_undefined};
catch_apply(_Module, Method, _Args) when Method == undefined ->
    {error, method_is_undefined};
catch_apply(Module, Method, Args) ->
    case catch( apply(Module, Method, Args) ) of
        {'EXIT', Reason} ->
            {error, Reason};
        Ret ->
            {ok, Ret}
    end.


%% --------------------------------------------------------------------
%% Function: eval/1
%% Description: 
%%      动态执行字符串中erlang代码
%%      !注意:命令行必须以"."结束
%% Params: 
%%      Cmd     binary() | string(), erlang命令 例如:"erlnag:now()." <<"erlang:now().">>
%% Returns: 
%%      {error, command_format_error}   |
%%      {error, Reason}                 |
%%      {ok, Value}
%% --------------------------------------------------------------------
eval(B_cmd) when is_binary(B_cmd) ->
    eval(binary_to_list(B_cmd));
eval(S_cmd) when is_list(S_cmd) ->
    case string:right(S_cmd, 1) of
        "." ->
            case erl_scan:string(S_cmd) of
                {ok, Tokens, _} ->
                    case erl_parse:parse_exprs(Tokens) of
                        {ok, Exprs} ->
                            case catch(eval_exprs(Exprs, [])) of
                                {'EXIT', Reason}->
                                    {error, Reason};
                                {ok, Value} ->
                                    {ok, Value};
                                ExprError ->
                                    {error, ExprError}
                            end;
                        ParseError ->
                            {error, ParseError}
                    end;
                ScanError ->
                    {error, ScanError}
            end;
        _Other ->
            {error, command_format_error}
    end.        

eval_exprs([ H | [] ], Binding) ->
    {value, Ret, _Binding} = erl_eval:expr(H, Binding),
    {ok,Ret};
eval_exprs([H | T] , Binding)->
    {value, _Ret, NewBinding} = erl_eval:expr(H, Binding),
    eval_exprs(T, NewBinding).



%% --------------------------------------------------------------------
%% Function: term2string/1, term2binary/1
%% Description: 
%%      把任意term表达式(Tuple,Atom..)转化为字符串binary
%% Params:  Term 表达式
%%          S_format 格式 例如"~p" 或"~w"
%% Returns:
%% --------------------------------------------------------------------
term2string(Term)->
    binary_to_list(term2binary(Term)).

term2binary(Term)->
    term2binary(Term, "~p").

term2binary(Term, S_format)->
    Chars = io_lib:format(S_format,[Term]),
    chars_to_binary(Chars).

chars_to_binary(Chars)->
    List = chars_to_binary(Chars, []),
    erlang:list_to_binary(lists:reverse(List)).

chars_to_binary([], L_ret)->
    L_ret;
chars_to_binary([H | T], L_ret) when H == 10->
    chars_to_binary(T, [10,13] ++ L_ret);
chars_to_binary([H | T], L_ret) when is_integer(H)->
    chars_to_binary(T, [H] ++ L_ret);
chars_to_binary([H | T], L_ret) when is_list(H) ->
    chars_to_binary(T, chars_to_binary(H, []) ++ L_ret).



%% --------------------------------------------------------------------
%% Function: time2string/0, time2string/1, date2string/0, date2string/1, datetime2string/0, datetime2string/1
%% Description: 
%%      把时间格式化输出为字符串
%%
%%      66> elib_util:time2string().
%%      "09:58:14"
%%      67> elib_util:date2string().
%%      "2010-03-09"
%%      68> elib_util:datetime2string().
%%      "2010-03-09 09:58:33"
%%      
%% Params:	
%%      Date        时间 erlang:date() | {Y, M, D} | erlang:time() | {H, I, S} | {erlang:date(), erlang:time()}
%% Returns:	
%%      string      例: "2009-01-03", "23:49:32", "2009-01-03 23:49:32"
%% --------------------------------------------------------------------
time2string()->
    time2string(erlang:time()).

time2string({H, M, S})->
    date2string_int(H) ++ ":" ++ date2string_int(M) ++ ":" ++date2string_int(S).

date2string()->
    date2string(erlang:date()).

date2string({Y, M, D})->
    date2string_int(Y) ++ "-" ++ date2string_int(M) ++ "-" ++ date2string_int(D).

datetime2string()->
    datetime2string({erlang:date(), erlang:time()}).

datetime2string({{Y, M, D}, {H, I, S}}) ->
    date2string({Y, M, D}) ++ " " ++ time2string({H, I, S}).

date2string_int(I) when I >= 10 ->
    integer_to_list(I);
date2string_int(I) ->
    "0" ++ integer_to_list(I).



%% --------------------------------------------------------------------
%% Function: binary_to_hex/1
%% Description: 
%%      把二进制数据转化为16进制表示的字符串
%% Params:	
%%      B binary
%% Returns:	
%%      string
%% --------------------------------------------------------------------
binary2hex(B)->
    List = binary_to_list(B),
    binary2hex(List,"").

binary2hex([], SRet)->
    SRet;
binary2hex([H | T], SRet)->
    [S] = io_lib:format("~.16B",[H]),
    case string:len(S) == 1 of
        true ->
            binary2hex(T, SRet ++ "0" ++ S);
        false ->
            binary2hex(T, SRet ++ S)
    end.    
%% --------------------------------------------------------------------
%% Function: binary_tokens/2
%% Description: 
%%      把binary 根据 token 拆分成数组
%%
%% Params:	
%%      B           待拆分数据 例: <<"a,b,c">>
%%      B_token     拆分的标志 例: <<",">> !!! 只能是单字节
%% Returns:	
%%      list()      例: [<<"a">>,<<"b">>,<<"c">>]
%% --------------------------------------------------------------------
binary_tokens(B, B_token)->
    <<B_char,_/binary>> = B_token,
    binary_tokens(B, B_char, <<>>, []).

binary_tokens(<<>>, _B_token, B_word, Ret)->
    case B_word of 
        <<>> -> Ret; 
        _ -> Ret ++ [B_word]
    end;
binary_tokens(<<H,T/binary>>, B_token, B_word, Ret) when H == B_token->
    NewRet = case B_word of 
                <<>> -> Ret; 
                _ -> Ret ++ [B_word]
             end,
    binary_tokens(T, B_token, <<>>, NewRet);
binary_tokens(<<H,T/binary>>, B_token, B_word, Ret) when H =/= B_token->
    binary_tokens(T, B_token, <<B_word/binary, H>>, Ret).

%% --------------------------------------------------------------------
%% Function: binary_join/2
%% Description: 
%%      把多个binary 根据 token 连接成一个binary
%% Params:	
%%      List        数组 例: [<<"hello",<<"world!">>]
%%      B_token     标志 例: <<",">> 或 <<" and ">>...
%% Returns:	
%%      binary      例: <<"hello, world!">>
%% --------------------------------------------------------------------
binary_join(List, B_token)->
    binary_join(List, B_token, []).

binary_join([], _B_word, Ret)->
    concat_binary(Ret);    
binary_join([H | T], B_word, Ret)->
    case Ret of
        [] -> 
            binary_join(T, B_word, [H]);
        _ ->
            binary_join(T, B_word, Ret ++ [B_word] ++ [H])
    end.
    


%% --------------------------------------------------------------------
%% Function: rpc/2, rpc/3
%% Description: 
%%      进程间发送消息
%%
%%      !!! 
%%      发送的消息 例如:{self(), ...}
%%      接受消息   例如:{reply, P_caller_pid, ...}
%%      消息处理方需使用rply返回消息
%% Params:	
%%      A_name      进程名字或进程PID
%%      U_message   消息内容
%%      I_timeout   超时时间默认1个小时 单位:ms
%% Returns:	
%%      ok
%%      {error, not_start} |
%%      {error, timeout}   |
%%      {error, Reason} 
%% --------------------------------------------------------------------
rpc(A_name, U_message)->
    rpc(A_name, U_message, ?DEFAULT_RPC_TIMEOUT).

rpc(A_name, U_message, I_timeout) when is_atom(A_name)->
    case whereis(A_name) of
        undefined ->
            {error, not_start};
        Pid ->
            rpc(Pid, U_message, I_timeout)
    end;
rpc(Pid, U_message, I_timeout) when is_pid(Pid) ->
    P_me = self(),
    Pid ! {P_me, U_message},
    case I_timeout of
        0 ->
            ok;
        _ ->            
            receive
                {reply, P_me, U_response} ->
                    U_response
                after I_timeout ->
                    {error, timeout}
            end
    end.            

    

%% --------------------------------------------------------------------
%% Function: reply/2
%% Description: 
%%      服务进程发送返回消息
%% Params:	
%%      P_caller    进程PID
%%      U_message   消息内容
%% Returns:	
%%      ok
%% --------------------------------------------------------------------
reply(P_caller, U_message)->
    P_caller ! {reply, P_caller, U_message},
    ok.

%% --------------------------------------------------------------------
%% Function: md5_string/1
%% Description: 
%%      MD5 返回32位字符串
%%      例如: md5_string("hello") =>"5d41402abc4b2a76b9719d911017c592"
%% Params:	
%%      S       atom() | binary() | list
%% Returns:	
%%      string()
%% --------------------------------------------------------------------
md5_string(T) when is_tuple(T)->
    md5_string(term_to_binary(T));
md5_string(A) when is_atom(A)->
    md5_string(atom_to_list(A));
md5_string(S)->
    B_md5 = erlang:md5(S),
    L_md5 = binary_to_list(B_md5),
    L_hex = lists:map(fun(X) -> 
                [md5_hex( X div 16), md5_hex( X rem 16)]
                end, L_md5),
    lists:flatten(L_hex).

md5_hex(N) when N < 10 ->
    $0 + N;
md5_hex(N) when N >= 10, N <16 ->
    $a + (N - 10).


%% --------------------------------------------------------------------
%% Function: to_int/1
%% Description: 转整型
%% Params:	binary | string
%% Returns:	integer	|
%%			{error, Reason}
%% --------------------------------------------------------------------
to_int(B) when is_binary(B)->
	to_int(binary_to_list(B));
to_int(Str) when is_list(Str)->
	to_int(Str, 0).

to_int(Str, _I_default)->
	case string:to_integer(Str) of
		{error, Reason} ->
			{error, Reason};
		{I_result,_} ->
			I_result
	end.


%% --------------------------------------------------------------------
%% Function: list_partition/2
%% Description:
%%      对list进行分组
%%      lists中只支持分成2组
%%      例如:
%%      Test = [{a,1}, {b,1}, {c,2}].
%%      elib_util:list_partition(fun({K,V})->{V, K} end ,Test).
%%      返回:[{1,[a,b]},{2,[c]}]
%%      
%% Params:	
%%      Pred    fun(Elem) -> Tuple {Group, Value}   {分组键, 存储返回值}
%%      List    list()
%% Returns:	
%%	    list()
%% --------------------------------------------------------------------    
list_partition(Pred, List)->
    list_partition(Pred, List, []).

list_partition(_Pred, [], Ret)->
    Ret;
list_partition(Pred, [ X | T ], Ret)->
    {GroupV, SaveV} = Pred(X),
    case lists:keyfind(GroupV, 1, Ret) of
        false ->
            list_partition(Pred, T, Ret ++ [{GroupV, [SaveV]}]);
        {GroupV, Old} ->
            New = Old ++ [SaveV],
            list_partition(Pred, T, ( Ret -- [{GroupV, Old}] ) ++ [{GroupV, New}])
    end.

    
