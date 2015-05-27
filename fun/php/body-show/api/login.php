<?php

// login.php

require_once('../include/common.php');
(!defined('_BODY_SHOW') ? exit('Access Denied!') : '');
$code = 200;

// 参数获取
$sign = http_receive('_sign'); // 必传
$mobile = http_receive('mobile'); // 必传
$password = http_receive('password'); // 必传
$log = addslashes(http_receive('log'));

// 参数验证
if (!sign_verify($sign, $mobile.$password)) {
	$code = 50021; // 签名错误
}

// 通过验证
if (200 === $code) {
	// 初始参数
	$db = new mysqli_ext(_ENV);
	// 判断登录
	$select = 'SELECT user_id FROM `user` '.
		"WHERE `mobile` = \"$mobile\" AND `password` = \"$password\";";
	$result = $db->query($select);
	if (1 !== $result->num_rows) {
		$code = 50022; // 帐密错误
	}
	// 连接断开
	$result->close();
	$db->close();
}

// 日志记录
write_log(_FLAG_API, $code, $_REQUEST);
// 结果输出
exit(response_json($code));

