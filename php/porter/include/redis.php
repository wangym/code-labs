<?php

// redis.php

$dbname = 'bBVhEajyQnpEjIxBUwmv';
$host = 'redis.duapp.com';
$port = '80';
$user = '0lNAAGGEQNQ0a2M9GbdKYFaw';
$pwd = 'yEPaNPgEld5jU6xe4jvAGuCYrF8O4yw8';

try {
	$redis = new Redis();
	$ret = $redis->connect($host, $port);
	if ($ret === false) {
		die($redis->getLastError());
	}
	$ret = $redis->auth($user . "-" . $pwd . "-" . $dbname);
	if ($ret === false) {
		die($redis->getLastError());
	}
	$redis->flushdb();
	$ret = $redis->set("key", "value");
	if ($ret === false) {
		die($redis->getLastError());
	} else {
		echo "OK";
	}
} catch (RedisException $e) {
	die("Uncaught exception " . $e->getMessage());
}

