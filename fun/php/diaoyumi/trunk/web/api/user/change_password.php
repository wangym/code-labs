<?php

// change_password.php

require_once('../../include/bootstrap.php');
(!defined('_APP') ? exit('Access Denied!') : '');

/* 基础验证 */
// 修改密码验证
$body = change_password_verify();

/* 业务逻辑 */
$status = STATUS_CHANGE_PASSWORD_FAIL;
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$service->data = $body;
if ($service->is_change_password()) {
	$status = STATUS_OK;
}
unset($service);
unset($mysqli);

/* 返回结果 */
exit(response_json($status));

