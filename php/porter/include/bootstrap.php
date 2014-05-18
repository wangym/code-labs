<?php

// bootstrap.php

// setting
//error_reporting(0);
date_default_timezone_set('Asia/Shanghai');

// variable
$_dirname = dirname(__FILE__);
$_pathinfo = pathinfo($_dirname);

// constant
define('_APP', 'porter');
define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
define('_FILENAME', basename($_SERVER['SCRIPT_NAME']));
define('_DATETIME', date('Y-m-d H:i:s', _TIME));
define('_TIME', $_SERVER['REQUEST_TIME']);

// status
define('STATUS_OK', 200);
define('STATUS_ERROR', 500);
define('STATUS_PARAMETER_ERROR', 501);

// require
require('redis.php');
require('service.php');
require('util.php');

