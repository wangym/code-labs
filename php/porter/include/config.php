<?php

// config.php

(!defined('_APP') ? exit('Access Denied!') : '');

// status
define('STATUS_OK', 200);
define('STATUS_INTERNAL_SERVER_ERROR', 500);
define('STATUS_PARAMETER_ERROR', 901);
define('STATUS_SIGN_ERROR', 902);

// _message
$_message = array(
	200 => '成功',
	500 => '内部服务器错误',
	901 => '参数错误',
	902 => '签名错误'
);

// _database 
$_database = array(
	'dev' => array(
		'host' => '',
		'port' => '',
		'username' => '',
		'password' => '',
		'dbname' => '',
		'charset' => 'utf8'
	),
	'prod' => array(
		'host' => 'redis.duapp.com',
		'port' => '80',
		'username' => '0lNAAGGEQNQ0a2M9GbdKYFaw',
		'password' => 'yEPaNPgEld5jU6xe4jvAGuCYrF8O4yw8',
		'dbname' => 'bBVhEajyQnpEjIxBUwmv',
		'charset' => 'utf8'
	),
);

