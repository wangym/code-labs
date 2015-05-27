<?php

// auth.php

require_once('../../include/bootstrap.php');
(!defined('_APP') ? exit('Access Denied!') : '');

/* 基础验证 */
// 用户登录验证
$body = auth_verify();

/* 业务逻辑 */
$status = STATUS_AUTH_FAIL;
$data = '';
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$service->data = $body;
if (0 < $id = $service->get_auth_id()) {
	$status = STATUS_OK;
	$data = array('id' => $id);
}
unset($service);
unset($mysqli);

/* 返回结果 */
exit(response_json($status, $data));

