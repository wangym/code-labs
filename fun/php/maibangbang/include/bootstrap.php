<?php

// bootstrap.php

// setting
//error_reporting(0);
date_default_timezone_set('Asia/Shanghai');
// variable
$_dirname = dirname(__FILE__);
$_pathinfo = pathinfo($_dirname);
// constant
define('_APP', 'maibangbang');
define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
//define('_ENV', 'dev');
define('_FILENAME', basename($_SERVER['SCRIPT_NAME']));
define('_TIME', $_SERVER['REQUEST_TIME']);
define('_DATETIME', date('Y-m-d H:i:s', _TIME));
define('_SECRET_KEY', 'maibangbang_20120816');
define('_SMARTY_DIR', '../third-party/smarty/');
// api
define('STATUS_OK', 200);
define('STATUS_ERROR', 500);
define('STATUS_PARAMETER_ERROR', 501);
// require
require('mysqli_ext.php'); // 数据库
require('service.php'); // 逻辑层
require('util.php'); // 工具类

