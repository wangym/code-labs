<?php

$host = 'redis.duapp.com';
$port = '80';
$username = '0lNAAGGEQNQ0a2M9GbdKYFaw';
$password = 'yEPaNPgEld5jU6xe4jvAGuCYrF8O4yw8';
$dbname = 'bBVhEajyQnpEjIxBUwmv';

try {
	$redis = new Redis();
	$result = $redis->connect($host, $port);
	if (false === $result) {
		exit($redis->getLastError());
	}
	$result = $redis->auth("$username-$password-$dbname");
	if (false === $result) {
		exit($redis->getLastError());
	}
	exit(var_dump($redis->keys('text*')));
} catch (RedisException $e) {
	exit($e->getMessage());
}

