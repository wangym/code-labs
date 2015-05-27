<?php

// common.php

// global setting
//error_reporting(0);
date_default_timezone_set('Asia/Shanghai');

// variable
$_dirname = dirname(__FILE__);
$_pathinfo = pathinfo($_dirname);

// constant
define('_BODY_SHOW', true);
//define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
define('_ENV', 'dev');
define('_ROOT', $_SERVER['DOCUMENT_ROOT'].'/');
define('_DIR', $_pathinfo['dirname'].'/');
define('_FILENAME', basename($_SERVER['SCRIPT_NAME']));
define('_IP', $_SERVER['REMOTE_ADDR']);
define('_TIME', $_SERVER['REQUEST_TIME']);
define('_DATETIME', date('Y-m-d H:i:s', _TIME));
define('_FLAG_API', 'api');

// require file
require_once('util.php'); // 工具类
require_once('validator.php'); // 验证类
require_once('mysqli_ext.php'); // 数据库

