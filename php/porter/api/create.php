<?php

// create.php

require_once($_SERVER['DOCUMENT_ROOT'] . 'include/bootstrap.php');

/* 基础验证 */
// 获取参数验证
$params = api_sign_verify();

/* 业务逻辑 */
$redis = new RedisExt();
$redis->flushdb();
$ret = $redis->set("key", "value");
if ($ret === false) {
	die($redis->getLastError());
} else {
	echo "OK";
}
