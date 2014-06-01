<?php

// create.php

require($_SERVER['DOCUMENT_ROOT'] . 'include/bootstrap.php');

/* 基础验证 */
// 获取参数验证
//$params = api_sign_verify();

/* 业务逻辑 */
$dao = new RedisNoSqlDao();
//$dao->flushdb();
$result = $dao->setex("key", 10, "hello");
//if ($result === false) {
//	die($redis->getLastError());
//} else {
	echo 'key=' . $dao->get("key");
//}

