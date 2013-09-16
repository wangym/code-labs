%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%% 	为了保证module的独立性，可耻了复制了JOE老爷爷的tcp_server.erl.
%%%	    简化TELNET SERVER端开发的基础类
%%% Created : 2009-12-3
%%% Example:
%%% 
%%% -module(ns_shell).
%%% -export([start/0, stop/0, process_request/1]).
%%% -define(SHELL_PORT, 799).
%%%
%%% start()->
%%%     elib_telnet:start(?SHELL_PORT, ?MODULE).
%%%
%%% stop()->
%%%    elib_telnet:stop(?SHELL_PORT).
%%%
%%% --------------------------------------------------------------------
%%% Function: process_request/1
%%% Description: 
%%%      具体服务处理逻辑
%%%      注: 1.如果要关闭socket 可以用 exit(close)
%%%          2.返回结果支持 string | atom | binary | tuple
%%% Params:	
%%% Returns:	U_response     	
%%% --------------------------------------------------------------------
%%% process_request(["echo", S1 | _])->
%%%     "echo " ++ S1;
%%% process_request(["status"])->
%%%     "ok status.";
%%% process_request(["help"])->
%%%     "ok, help.";
%%% process_request(["quit"])->
%%%     exit(close)
%%% process_request(_)->
%%%     "unknow command, please 'help'.".
%%%
%%% c:> telnet 127.0.0.1 799
%%% unknow command, please 'help'.
%%% status
%%% ok status.
%%% help
%%% ok, help.
%%% echo world!
%%% echo world!
%%% quit
%%%
%%% ------------------------------------------------------------------
-module(elib_telnet).
-define(DEFAULT_MAX_CONNECTS, 30).


%% --------------------------------------------------------------------
%% External exports
%% --------------------------------------------------------------------

-export([stop/1, start/2, start/3]).

%% --------------------------------------------------------------------
%% Function: start/2, start/3
%% Description: 启动
%% Params:  
%%          I_port  	    端口号
%%          Fun             处理具体请求的方法
%%          I_max_clients   最大连接数
%% Returns: {ok, Pid}       |
%%			{error , Reason}
%% --------------------------------------------------------------------
start(I_port, A_module)->
	start(I_port, A_module, ?DEFAULT_MAX_CONNECTS).

start(I_port, A_module, I_max_connects) ->
    Name = port_name(I_port),
    case whereis(Name) of
        undefined ->
    	    case start_raw_server(I_port, fun(Socket)->socket_handler(Socket, A_module) end, I_max_connects, line) of
                {ok, Pid} ->{ok, Pid};
                {error, Reason}->{error, Reason};
                Error ->{error, Error}
            end;
        P_old -> 
            {ok, P_old}            
    end.            

%% --------------------------------------------------------------------
%% Function: stop/1
%% Description: 
%% Params:	
%%          I_port  	    端口号
%% Returns:	ok 					|
%%			{error, not_start}
%% --------------------------------------------------------------------
stop(I_port) when is_integer(I_port) ->
    Name = port_name(I_port),
    case whereis(Name) of
	undefined ->
        {error, not_start};
	Pid ->
	    exit(Pid, kill),
	    (catch unregister(Name)),
	    ok
    end.

    
%% --------------------------------------------------------------------
%% Internal functions
%% --------------------------------------------------------------------
%% 去除尾部回车 兼容win, linux
trim_ln(<<"\n">>) ->
    <<>>;
trim_ln(<<"\r\n">>) ->
    <<>>;
trim_ln(B) ->
    Len = size(B) - 2,
    <<B1:Len/binary, End1, _End2>> = B,
    case End1 of
        13 -> B1;
        _ -> <<B1/binary, End1>>
    end.            

%% 发送回包    
send(Socket, T) when is_tuple(T) ->
    send(Socket, tuple_to_list(T));
send(Socket, A) when is_atom(A)->
    send(Socket, atom_to_list(A));
send(Socket, S) when is_list(S)->
    send(Socket, list_to_binary(S));
send(_Socket, <<>>) ->
    ok;
send(Socket, B) when is_binary(B) ->
    case erlang:split_binary(B,  size(B) - 1) of
        {_, <<"\n">>} ->
            gen_tcp:send(Socket, B);
        _ ->
            gen_tcp:send(Socket, <<B/binary, "\r\n">>)
    end.
    
    
socket_handler(Socket, M) ->
    receive
        {tcp, Socket, B} ->
            case trim_ln(B) of
                <<>> -> 
                    socket_handler(Socket, M);
                B_line ->
                    L_command = string:tokens( binary_to_list(B_line) , " "),
                    case catch( M:process_request( L_command ) ) of
                        {'EXIT', close} ->
                            error_logger:info_msg("close ~w~n",[Socket]),
                            gen_tcp:close(Socket);
                        {'EXIT', Error} ->
                            error_logger:error_msg("~w~n", [{'EXIT', Error}]),
                            send(Socket, {'EXIT', Error}),
                            socket_handler(Socket, M);
                        Response ->
                            send(Socket, Response),
                            socket_handler(Socket, M)
                    end
            end;		
        {tcp_closed, Socket} ->
            error_logger:info_msg("close ~w~n",[Socket]),
            tcp_closed;
        {'EXIT', Pid , Why}->
            error_logger:error_msg("~w~n",[{'EXIT', Pid, Why}]), 
            socket_handler(Socket, M);
        Error ->
            error_logger:error_msg("~w~n", [Error]),
            {error, Error}
    end.

%% --------------------------------------------------------------------
%% joe tcp_server.erl 
%% --------------------------------------------------------------------
%% Note when start_raw_server returns it should be ready to
%% Immediately accept connections

start_raw_server(Port, Fun, Max, Length) ->
    Name = port_name(Port),
    case whereis(Name) of
	undefined ->
	    Self = self(),
	    Pid = spawn_link(fun() ->
				     cold_start(Self, Port, Fun, Max, Length)
			     end),
	    receive
		{Pid, ok} ->
		    register(Name, Pid),
		    {ok, self()};
		{_Pid, Error} ->
		    Error
	    end;
	_Pid ->
	    {error, already_started}
    end.

%%stop(Port) when is_integer(Port) ->
%%    Name = port_name(Port),
%%    case whereis(Name) of
%%	undefined ->
%%	    not_started;
%%	Pid ->
%%	    exit(Pid, kill),
%%	    (catch unregister(Name)),
%%	    stopped
%%   end.

%%children(Port) when is_integer(Port) ->
%%    port_name(Port) ! {children, self()},
%%    receive
%%	{session_server, Reply} -> Reply
%%    end.

port_name(Port) when is_integer(Port) ->
    list_to_atom("portServer" ++ integer_to_list(Port)).

cold_start(Master, Port, Fun, Max, Length) ->
    process_flag(trap_exit, true),
    io:format("Start the telnet service, binding port ~p, maximum number of connections ~w, header length ~w .~n",[Port, Max, Length]),
    case gen_tcp:listen(Port, [binary,
			       %% {dontroute, true},
				   {keepalive, true},
			       {nodelay,true},
			       {packet, Length},
			       {reuseaddr, true}, 
                   {sndbuf, 40960},
                   {recbuf, 40960},
			       {active, false}]) of
	{ok, Listen} ->
	    %% io:format("Listening on:~p~n",[Listen]),
	    Master ! {self(), ok},
	    New = start_accept(Listen, Fun),
	    %% Now we're ready to run
	    socket_loop(Listen, New, [], Fun, Max);
	Error ->
	    Master ! {self(), Error}
    end.

%% Don't mess with the following code uless you really know what you're 
%% doing (and Thanks to Magnus for heping me get it right)

socket_loop(Listen, New, Active, Fun, Max) ->
    receive
	{istarted, New} ->
	    Active1 = [New|Active],
	    possibly_start_another(false, Listen, Active1, Fun, Max);
	{'EXIT', New, _Why} ->
	    %% io:format("Child exit=~p~n",[Why]),
	    possibly_start_another(false, Listen, Active, Fun, Max);
	{'EXIT', Pid, _Why} ->
	    %% io:format("Child exit=~p~n",[Why]),
	    Active1 = lists:delete(Pid, Active),
	    possibly_start_another(New, Listen, Active1, Fun, Max);
	{children, From} ->
	    From ! {session_server, Active},
	    socket_loop(Listen, New, Active, Fun, Max);
	Other ->
	    io:format("Here in loop:~p~n",[Other])
    end.

possibly_start_another(New, Listen, Active, Fun, Max) when is_pid(New) ->
    socket_loop(Listen, New, Active, Fun, Max);
possibly_start_another(false, Listen, Active, Fun, Max) ->
    case length(Active) of
	N when N < Max ->
	    New = start_accept(Listen, Fun),
	    socket_loop(Listen, New, Active, Fun, Max);
	_ ->
	    socket_loop(Listen, false, Active, Fun, Max)
    end.

start_accept(Listen, Fun) ->
    S = self(),
    spawn_link(fun() -> start_child(S, Listen, Fun) end).

start_child(Parent, Listen, Fun) ->
    case gen_tcp:accept(Listen) of
	{ok, Socket} ->
	    Parent ! {istarted,self()},		    % tell the controller
	    inet:setopts(Socket, [{nodelay,true},
				  {active, true}]), % before we activate socket
	    %% io:format("running the child:~p~n",[Socket]),
	    Fun(Socket);
	_Other ->
	    exit(oops)
    end.


