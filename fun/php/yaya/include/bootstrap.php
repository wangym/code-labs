<?php

// bootstrap.php

// setting
//error_reporting(0);
date_default_timezone_set('Asia/Shanghai');
// variable
$_dirname = dirname(__FILE__);
$_pathinfo = pathinfo($_dirname);
// constant
define('_APP', 'diaoyumi');
define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
//define('_ENV', 'dev');
define('_FILENAME', basename($_SERVER['SCRIPT_NAME']));
define('_TIME', $_SERVER['REQUEST_TIME']);
define('_DATETIME', date('Y-m-d H:i:s', _TIME));
define('_SECRET_KEY', 'diaoyumi_a78102-');
// api
define('STATUS_OK', 200);
define('STATUS_ERROR', 500);
define('STATUS_PARAMETER_ERROR', 401); // 参数错误
define('STATUS_SIGN_FAIL', 402); // 签名错误
define('STATUS_EMAIL_EXIST', 403); // 邮箱存在
define('STATUS_NAME_EXIST', 404); // 帐号存在
define('STATUS_NOT_SUPPORT', 405); // 字段错误
define('STATUS_FIELD_NOT_UNIQUE', 406); // 字段存
define('STATUS_AUTH_FAIL', 407); // 验证失败
define('STATUS_CHANGE_PASSWORD_FAIL', 408); // 改密失败
define('STATUS_RID_NOT_UNIQUE', 409); // 资源存在
define('STATUS_RID_NOT_PERMISSION', 410); // 没有权限
// require
require('mysqli_ext.php'); // 数据库
require('service.php'); // 逻辑层
require('util.php'); // 工具类
require('validator.php'); // 验证类

