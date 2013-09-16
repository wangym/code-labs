-define(DB_BTREE, 1).
-define(DB_HASH,  2).
-define(DB_RECNO, 3).
-define(DB_QUEUE, 4).
-define(DB_UNKNOWN, 5).

-define(DB_AUTO_COMMIT, 16#100).
-define(DB_CREATE, 16#1).
-define(DB_EXCL, 16#40).
-define(DB_MULTIVERSION, 16#4).
-define(DB_NOMMAP,16#8).
-define(DB_RDONLY, 16#400).
-define(DB_READ_UNCOMMITTED, 16#200).
-define(DB_THREAD, 16#10).
-define(DB_TRUNCATE, 16#4000).

-define(B_OK, <<131,100,0,2,111,107>>).

-define(PORTSHELL, "bdb_port").
-define(DRIVERSHELL, "bdb_drv").
-define(FINDSCOPE,	["./", "./ebin", "../ebin/", "./c_src/", "../c_src/"]).

%% erlang:term_to_binary({error,"DB_NOTFOUND: No matching key/data pair found"})
-define(B_NOTFOUND, <<131,104,2,100,0,5,101,114,114,111,114,107,0,44,68,66,95,78,79,84,70,79,85,78,68,58,32,78,111,32,109,97,116,99,104,105,110,103,32,107,101,121,47,100,97,116,97,32,112,97,105,114,32,102,111,117,110,100>>).

-define(B_CHECK, "____bdb_check_____").


