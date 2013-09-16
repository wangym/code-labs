%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%		记录系统工作状态的模块    
%%% Created : 2009-12-2
%%% Example:
%%%     elib_status:start().
%%%     elib_status:reg_text(startup, "2009-09-21").
%%%     elib_status:reg_count({wp, insert}).
%%%     elib_status:reg_count({wp, update}, 30).
%%%     ...
%%%     elib_status:inc({wp, update}).
%%%     elib_status:inc({wp, insert}, 2).
%%%     elib_status:store(startup, "2009-09-22").
%%%     ...
%%%     {ok, L_wpstatus} = elib_status:get_list(wp).
%%%     {ok, L_allstatus} = elib_status:get_list().
%%%     %% 格式化为字符串
%%%     io:format("~n~s~n",[elib_status:items2txt(L_wpstatus)]).
%%%     -------------------
%%%     93>
%%%     wp update 31
%%%     wp insert 2
%%%     -------------------
%%%
%%%     io:format("~n~s~n",[elib_status:items2txt(L_allstatus)]).
%%%
%%%     ------------------
%%%     94>
%%%     wp update 31
%%%     wp insert 2
%%%     startup 2009-09-22
%%%     -------------------
%%%     ...
%%%     elib_status:stop().
%%%
%%%
%%% -------------------------------------------------------------------
-module(elib_status).

-record(item, {
            a_type = count, %% 类型 [text | count]
            a_group = default, %% 分组
            a_name = undefined, %% 名字
            u_value = undefined %% 值 [string() | int()]
        }).

%% --------------------------------------------------------------------
%% External exports
%% --------------------------------------------------------------------
-export([start/0, stop/0, inc/1, inc/2, store/2, get/1, get_list/0, get_list/1, get_list/2]).
-export([reg_text/1, reg_text/2, reg_count/1, reg_count/2, reg_item/3, items2txt/1]).


%% --------------------------------------------------------------------
%% Function: start/0, start/1
%% Description: 
%% Params:  L_init      初始化参数列表
%%                       [ {text, {default, startup}, "2009-09-12 13:56:48" }, {int, } | T ] 
%% Returns: {ok, Pid}
%%          {error, Reason}            
%% --------------------------------------------------------------------
start()->
    case whereis(?MODULE) of
        undefined ->
            Pid = spawn_link(fun()->process_flag(trap_exit, true), loop([]) end),
            register(?MODULE, Pid),
            {ok, Pid};
        P_old ->
            {ok, P_old}
    end.


%% --------------------------------------------------------------------
%% Function: reg_text/1, reg_text/2, reg_count/1, reg_count/2, reg_item/3
%% Description:  注册一个状态记录
%% Params:  A_name 名称 [atom | tuple] 例如: startup => {default, startup} 或 {wp, insert}
%%          I_value 整型初始值 默认 0
%%          S_value 字符串 默认 ""
%%          A_type  [text | count]
%% Returns: ok              |
%%          {error, Reason}
%% --------------------------------------------------------------------
reg_text(A_name) ->
    reg_text(A_name, "").

reg_text(A_name, S_value) when is_list(S_value) ->    
    reg_item(text, A_name, S_value).

reg_count(A_name)->
    reg_count(A_name, 0).

reg_count(A_name, I_value) when is_integer(I_value)->
    reg_item(count, A_name, I_value).

reg_item(A_type, A_name, U_value)->
    {A_group, A_newname} =  get_tuple_name(A_name),
    R = #item{a_type = A_type, a_group = A_group, a_name = A_newname, u_value = U_value},
    rpc({reg, R}).

    
%% --------------------------------------------------------------------
%% Function: stop/0
%% Description: 
%% Params:   
%% Returns: ok
%%          {error, not_start}            
%% --------------------------------------------------------------------
stop()->
    case whereis(?MODULE) of
        undefined -> {error, not_start};
        _Pid -> 
            rpc(stop)
    end.

%% --------------------------------------------------------------------
%% Function: inc/0, inc/1
%% Description: 
%% Params:  
%%          U_name [atom | tuple]  例如: startup 或 {wp, insert}
%%          I  integer()
%% Returns: ok
%%          {error, Reason}            
%% --------------------------------------------------------------------
inc(U_name) ->
    inc(U_name, 1).

inc(U_name, I) when ( is_integer(I) == true) ->
    arpc({inc, get_tuple_name(U_name), I}).

%% --------------------------------------------------------------------
%% Function: store/2
%% Description: 
%% Params:   
%%          U_name [atom | tuple]  例如: startup 或 {wp, insert}
%%          S_value string() | integer()
%% Returns: ok
%%          {error, Reason}            
%% --------------------------------------------------------------------
store(U_name, U_value) when ( is_list(U_value) or is_integer(U_value) ) == true ->
    arpc({store, get_tuple_name(U_name), U_value}).

%% --------------------------------------------------------------------
%% Function: get/1
%% Description: 
%% Params:   
%%          U_name [atom | tuple]  例如: startup 或 {wp, insert}
%% Returns: {ok, U_value}  U_value => [ string | integer]
%%          {error, not_found}    
%%          {error, Reason}            
%% --------------------------------------------------------------------
get(U_name) ->
    rpc({get, U_name}).

%% --------------------------------------------------------------------
%% Function: get_list/0 , get_list/1
%% Description: 
%% Params:   
%%          A_group atom [all | ...] 例如: all, wp, default
%%          A_type  atom() [ count | text ]
%% Returns: {ok, List} 例如: [[sp,insert,1000 ],[sp,update,10 ]]
%%          {error, Reason}            
%% --------------------------------------------------------------------
get_list()->
    get_list(all).

get_list(all)->
    rpc(get_all);
get_list(A_group)->
    rpc({get_group, A_group}).

get_list(A_group, A_type)->
    case get_list(A_group) of
        {ok, List} ->
            {ok, lists:filter(fun(X)-> X#item.a_type == A_type end, List)};
        Error ->
            Error
    end.

%% --------------------------------------------------------------------
%% Function: items2txt/1
%% Description: 把item record集合格式化为适应telnet服务输出的字符串
%%      例如:
%%          default name changgb
%%          wp insert 109
%%          wp update 2
%%          ..
%% Params:   
%%          L_items list() for item record 例如: [{item,text,default,name,"changgb"}...]
%% Returns: string
%% --------------------------------------------------------------------
items2txt(L_items) ->
    items2txt(L_items, "").

items2txt([], S) ->
    S;
items2txt([ R | T] , S)->
    S_value =   case R#item.a_type of
                    text -> R#item.u_value;
                    count -> integer_to_list(R#item.u_value)
                end,
    S_line =    case R#item.a_group of
                    default -> atom_to_list(R#item.a_name) ++ " " ++ S_value;
                    A_group -> atom_to_list(A_group) ++ " " ++ atom_to_list(R#item.a_name) ++ " " ++ S_value
                end,
                
    case S == "" of
        true -> items2txt(T, S_line);
        false -> items2txt(T, S ++ "\r\n" ++ S_line)
    end.

    

%% --------------------------------------------------------------------
%%          internel function
%% --------------------------------------------------------------------
loop(List)->
    receive
        {P_caller, {reg, R}} ->
            case item_lookup(List, {R#item.a_group, R#item.a_name}) of
                not_found -> 
                    reply(P_caller, ok), 
                    loop(List ++ [R]);
                _R_old -> 
                    reply(P_caller, {error, already_exists}), 
                    loop(List)
            end;
        {_P_caller, {inc, U_name, I} } ->
            L_new = case item_lookup(List, U_name) of
                        not_found -> List;
                        R when (R#item.a_type == count)  ->
                            R_new = R#item{u_value = R#item.u_value + I},
                            (( List -- [R] ) ++ [R_new]);
                        _ -> List    
                    end,
            loop(L_new);
        {_P_caller, {store, U_name, U_value} } ->
            L_new = case item_lookup(List, U_name) of
                        not_found -> List;
                        R ->
                            R_new = R#item{u_value = U_value},
                            %% 先删后增
                            (( List -- [R] ) ++ [R_new])
                    end,
            loop(L_new);
        {P_caller, {get,  U_name} } ->
            case item_lookup(List, U_name) of
                not_found -> reply(P_caller, {error, not_found});
                R -> reply(P_caller, {ok, R#item.u_value})
            end,
            loop(List);
        {P_caller, get_all} ->
            L_ret = item_sort( List ),
            reply(P_caller, {ok, L_ret}),
            loop(List);
        {P_caller, {get_group, A_group} } ->
            L_ret = item_sort( item_filter(List, A_group) ), 
            reply(P_caller, {ok, L_ret}),
            loop(List);
        {P_caller, stop} ->
            reply(P_caller, stoped),
            stoped
    end.


reply(P_caller, T_reply)->
    P_caller ! {reply, P_caller, T_reply},
    ok.

rpc(T_message) ->
    case arpc(T_message) of
        ok ->
            P_me = self(),
            receive
                {reply, P_me, T_reply_message} -> T_reply_message
            end;
        Error ->
            Error
    end.            

arpc(T_message)->
    case whereis(?MODULE) of
        undefined -> 
            {error, not_start};
        _ ->            
            ?MODULE ! {self(), T_message},
            ok
    end.        

get_tuple_name(A_name) when is_atom(A_name) == true ->
    {default, A_name};
get_tuple_name({A_group, A_name}) when ( is_atom(A_group) and is_atom(A_name) ) == true ->
    {A_group, A_name}.

item_lookup([], _)->
    not_found;
item_lookup([R | T], U_name)->
    {A_group, A_name} = get_tuple_name(U_name),
    case ( (A_group == R#item.a_group) and (A_name == R#item.a_name)) of
        true-> R;
        false -> item_lookup(T, U_name)
    end.

item_sort(List) ->
    lists:sort(
            fun(A, B) ->
                case (A#item.a_group == B#item.a_group) of
                    true ->
                        %%组相同按名字排
                        (A#item.a_name < B#item.a_name);
                    false ->
                        %%default组优先
                        case ((A#item.a_group == default) or (B#item.a_group == default)) of
                            true ->
                                (A#item.a_group == default);
                            false ->
                                (A#item.a_group < B#item.a_group)
                        end
                end
            end, 
            List).

item_filter(List,A_group)->
    lists:filter(
            fun(R)->
                (R#item.a_group == A_group)
            end,
            List).


