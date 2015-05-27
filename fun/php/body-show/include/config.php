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

// 接口的响应提示
$_msg = array(
	// _common
	200 => '成功',
	500 => '失败',
	// register
	50011 => '签名验证失败请正确注册',
	50012 => '该手机号码已是注册用户',
	50013 => '注册失败请您稍后再尝试',
	// login
	50021 => '签名验证失败请正确登录',
	50022 => '登录失败请检查帐号密码',
	// upload
	50031 => '签名验证失败请正确上传',
);

