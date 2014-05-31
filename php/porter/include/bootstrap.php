<?php

// bootstrap.php

// set
//error_reporting(0);
date_default_timezone_set('Asia/Shanghai');

// variable
$_dirname = dirname(__FILE__);
$_pathinfo = pathinfo($_dirname);

// constant
define('_APP', 'porter');
define('_DATETIME', date('Y-m-d H:i:s', $_SERVER['REQUEST_TIME']));
define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
define('_FILENAME', basename($_SERVER['SCRIPT_NAME']));
define('_SECRET_KEY', 'porter_my_20140518*#');
define('_TIME', $_SERVER['REQUEST_TIME']);

// require
require('config.php');
require('Dao.php');
require('Service.php');
require('util.php');
require('validator.php');
