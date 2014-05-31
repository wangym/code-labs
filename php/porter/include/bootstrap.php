<?php

// bootstrap.php

// set
//error_reporting(0);
date_default_timezone_set('Asia/Shanghai');

// variable
$_dirname = dirname(__FILE__);
$_pathinfo = pathinfo($_dirname);

// constant
// TODO:use 'parse_ini_file()'
define('_APP', 'porter');
define('_DATETIME', date('Y-m-d H:i:s', $_SERVER['REQUEST_TIME']));
define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
define('_FILENAME', basename($_SERVER['SCRIPT_NAME']));
define('_SECRET_KEY', 'porter_my_20140518*#');
define('_TIME', $_SERVER['REQUEST_TIME']);
// constant - status
define('STATUS_OK', 200);
define('STATUS_PARAMETER_ERROR', 901);
define('STATUS_SIGN_ERROR', 902);
define('STATUS_ERROR', 999);

// _message
$_message = array(
	200 => '成功',
	901 => '参数错误',
	902 => '签名错误',
	999 => '未知错误'
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

// require
require($_SERVER['DOCUMENT_ROOT'] . 'include/Dao.php');
require($_SERVER['DOCUMENT_ROOT'] . 'include/Service.php');
require($_SERVER['DOCUMENT_ROOT'] . 'include/util.php');
require($_SERVER['DOCUMENT_ROOT'] . 'include/validator.php');

