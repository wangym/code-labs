<?php

// register.php

require_once('../include/common.php');
(!defined('_BODY_SHOW') ? exit('Access Denied!') : '');
$code = 200;

// 参数获取
$sign = http_receive('_sign'); // 必传
$mobile = http_receive('mobile'); // 必传
$password = http_receive('password'); // 必传
$sex = http_receive('sex'); // 必传
$log = addslashes(http_receive('log'));

// 参数验证
if (!sign_verify($sign, $mobile.$password.$sex)) {
	$code = 50011; // 签名错误
}

// 通过验证
if (200 === $code) {
	// 初始参数
	$db = new mysqli_ext(_ENV);
	// 是否存在
	$select = 'SELECT user_id FROM `user` '.
		"WHERE `mobile` = \"$mobile\";";
	$result = $db->query($select);
	if (0 === $result->num_rows) {
		// 用户新增
		$insert = 'INSERT INTO `user` '.
			'(`mobile`,`password`,`sex`,`total_picture`,`total_score`,`total_view`,`created`,`modified`) VALUE '.
			"(\"$mobile\",\"$password\",$sex,0,0,0,"._TIME.","._TIME.");";
		if (!$db->query($insert)) {
			$code = 50013; // 写入错误
		}
	} else {
		$code = 50012; // 已经存在
	}
	// 连接断开
	$result->close();
	$db->close();
}

// 日志记录
write_log(_FLAG_API, $code, $_REQUEST);
// 结果输出
exit(response_json($code));

