<?php

// config.php

// 数据库连接参数
$_db = array(
	'dev' => array(
		'host' => 'localhost',
		'username' => 'root',
		'password' => '',
		'database' => 'body_show',
		'charset' => 'utf8'
	),
	'prod' => array(
		'host' => '127.0.0.1',
		'username' => 'a0825155602',
		'password' => 'xs86bodyshow99',
		'database' => 'a0825155602',
		'charset' => 'utf8'
	),
);

// status
define('STATUS_OK', 200);
define('STATUS_ERROR', 500);
define('STATUS_PARAMETER_ERROR', 501);

// message
$_message = array(
	200 => '成功',
	500 => '失败',
	501 => '参数错误'
);

