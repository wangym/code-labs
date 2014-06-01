<?php

// config.php
// TODO:use 'parse_ini_file()'

(!defined('_APP') ? exit('Access Denied!') : '');

/* constant */
// status - custom
define('_STATUS_GOODS_POST_ERROR', 901);
// status - system
define('_STATUS_OK', 200);
define('_STATUS_ERROR', 999);
define('_STATUS_PARAMETER_ERROR', 998);
define('_STATUS_SIGN_ERROR', 997);

/* _message */
$_message = array(
    // custom
    _STATUS_GOODS_HANDLING_ERROR => '签名错误',
    // system
    _STATUS_OK => '成功',
    _STATUS_ERROR => '错误',
    _STATUS_PARAMETER_ERROR => '参数错误',
    _STATUS_SIGN_ERROR => '签名错误'
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
        'dbname' => 'bBVhEajyQnpEjIxBUwmv',
        'charset' => 'utf8'
    ),
);

