<?php

// config.php
// TODO:use 'parse_ini_file()'

(!defined('_APP') ? exit('Access Denied!') : '');

/* constant-api */
define('_API_POST', 'http://' . $_SERVER['SERVER_NAME'] . '/api/goods/post.php');

/* constant-status */
define('_STATUS_OK', 200);
define('_STATUS_TIMEOUT_ERROR', 992);
define('_STATUS_ENCODE_ERROR', 993);
define('_STATUS_DECODE_ERROR', 994);
define('_STATUS_VERIFY_ERROR', 995);
define('_STATUS_GET_ERROR', 996);
define('_STATUS_POST_ERROR', 997);
define('_STATUS_PARAMETER_ERROR', 998);
define('_STATUS_ERROR', 999);

/* _message */
$_message = array(
    _STATUS_OK => '成功',
    _STATUS_TIMEOUT_ERROR => '超时错误',
    _STATUS_ENCODE_ERROR => '译码错误',
    _STATUS_DECODE_ERROR => '解码错误',
    _STATUS_VERIFY_ERROR => '验证错误',
    _STATUS_GET_ERROR => '资源获取错误',
    _STATUS_POST_ERROR => '资源创建错误',
    _STATUS_PARAMETER_ERROR => '参数错误',
    _STATUS_ERROR => '错误'
);

/* _database */
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
        'dbname' => 'agSSlrsJSjUzdphovsiD',
        'charset' => 'utf8'
    ),
);

