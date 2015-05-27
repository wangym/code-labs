<?php

// register.php

require_once('../../include/bootstrap.php');
(!defined('_APP') ? exit('Access Denied!') : '');

/* 基础验证 */
// 用户注册验证
$body = register_verify();

/* 业务逻辑 */
$status = STATUS_ERROR;
$data = '';
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$service->data = $body;
if (!$service->is_email_not_exist()) {
	$status = STATUS_EMAIL_EXIST;
}
else if (!$service->is_name_not_exist()) {
	$status = STATUS_NAME_EXIST;
}
else {
	if (0 < $id = $service->get_register_id()) {
		$status = STATUS_OK;
		$data = array('id' => $id);
	}
}
unset($service);
unset($mysqli);

/* 返回结果 */
exit(response_json($status, $data));

