%%% -------------------------------------------------------------------
%%% Author  : changgb
%%% Description :
%%%		标准TCP Server
%%%     具体实现类必须实现 
%%%     process_request(I_request_cod, B_data)方法
%%%     unpack(B) 方法
%%% Created : 2010-2-2
%%% Example:
%%%            case elib_tcpserver:start_link(kvdb_spserver, 2009, 300) of
%%%                ok ->
%%%                      ok;
%%%                Error ->
%%%                    Error
%%%            end.
%%%
%%%            elib_tcpserver:stop(2009).
%%%
%%%            elib_tcpserver:status(2009) -> [{connects, 20}]
%%% -------------------------------------------------------------------
-module(elib_tcpserver).

%% --------------------------------------------------------------------
%% Include files
%% --------------------------------------------------------------------

%% --------------------------------------------------------------------
%% External exports
%% --------------------------------------------------------------------

-export([behaviour_info/1]).

-export([stop/1, start_link/2, start_link/3, start_link/4, status/1]).

%% --------------------------------------------------------------------
%% Function: start_link/2, start_link/3, start_link/4
%% Description: 启动
%% Params:  A_module_name   具体处理TCP请求的module名称
%%          I_port  侦听端口号
%%          I_max_clients   最大连接数
%%          I_length        包头长度
%% Returns: {ok, Pid}       |
%%			{error , Reason}
%% --------------------------------------------------------------------
start_link(A_module_name, I_port) ->
    start_link(A_module_name, I_port, 300).

start_link(A_module_name, I_port, I_max_clients) ->
    start_link(A_module_name, I_port, I_max_clients, 4).

start_link(A_module_name, I_port, I_max_clients, I_length) ->
	process_flag(trap_exit, true),
	case start_raw_server(I_port,
					fun(Socket)-> socket_handler(Socket, A_module_name) end,  
					I_max_clients, 
					I_length) of
		{ok, _Pid} ->
            %% joe返回是创建者pid, 需要重新获取port监听进程对应的pid
            Name = port_name(I_port),
			{ok, whereis(Name)};
        {error, Reason}->
            {error, Reason};
		Error ->
			{error, Error}
	end.


%% --------------------------------------------------------------------
%% Function: stop/1
%% Description: 停止
%% Params:  I_port  侦听端口号
%% Returns: ok  |
%%			{error , Reason}
%% --------------------------------------------------------------------
stop(I_port) when is_integer(I_port) ->
    Name = port_name(I_port),
    case whereis(Name) of
    	undefined ->
            {error,not_start};
    	Pid ->
    	    exit(Pid, kill),
    	    (catch unregister(Name)),
    	    ok
   end.
%% --------------------------------------------------------------------
%% Function: status/1
%% Description:返回服务状态
%% Params:  I_port  端口号
%% Returns: List 例如: [{connects, Int()}]
%% --------------------------------------------------------------------
status(I_port)->
    A_port_name = list_to_atom("portServer" ++ integer_to_list(I_port)),
    case whereis(A_port_name) of
        undefined ->
            [{connects, 0}];
        _Any->
            [{connects,length(children(I_port))}]
    end.


%% --------------------------------------------------------------------
%%% Internal functions
%% --------------------------------------------------------------------
socket_handler(Socket, A_module_name) ->
	receive
		{tcp, Socket, B} ->
			Packet = A_module_name:unpack(B),
			case A_module_name:process_request(Packet) of
				{continue, B_response} ->
					%发送响应包 erlang自动加包长
					gen_tcp:send(Socket, B_response),
					socket_handler(Socket, A_module_name);
				{close, B_response} ->
					case size(B_response) > 0 of
						true ->
							gen_tcp:send(Socket, B_response);
						false ->
							void
					end,
					gen_tcp:close(Socket),
					tcp_closed;
				break ->
					tcp_break
			end;
		{tcp_closed, Socket} ->
			tcp_closed;
		Error ->
			{error, Error}
	end.


%%%=========================================================================
%%%  API
%%%=========================================================================

behaviour_info(callbacks) ->
	[{process_request,1},{unpack, 1}];
behaviour_info(_Other) ->
	undefined.	


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

children(Port) when is_integer(Port) ->
    port_name(Port) ! {children, self()},
    receive
    	{session_server, Reply} -> Reply
    end.

port_name(Port) when is_integer(Port) ->
    list_to_atom("portServer" ++ integer_to_list(Port)).

cold_start(Master, Port, Fun, Max, Length) ->
    process_flag(trap_exit, true),
    io:format("Start the tcpserver, binding port ~p, maximum number of connections ~w, header length ~w .~n",[Port, Max, Length]),
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

    
