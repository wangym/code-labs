<?php

// upload.php

require_once('../include/common.php');
(!defined('_BODY_SHOW') ? exit('Access Denied!') : '');
$code = 200;

// 参数获取
$sign = http_receive('_sign'); // 必传
$mobile = http_receive('mobile'); // 必传
$title = http_receive('title'); // 必传
$picture = http_receive('picture'); // 必传
$log = addslashes(http_receive('log'));

// 参数验证
if (!sign_verify($sign, $mobile.$title.$picture)) {
	$code = 50031; // 签名错误
}

