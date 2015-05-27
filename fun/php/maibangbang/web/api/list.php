<?php

// list.php

require_once('../../include/bootstrap.php');
(!defined('_APP') ? exit('Access Denied!') : '');

/* 基础验证 */
// 获取参数验证
$uid = http_receive('uid');
if (empty($uid)) {
	exit(response_json(STATUS_PARAMETER_ERROR));
}

/* 业务逻辑 */
$status = STATUS_ERROR;
$data = array();
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$data = $service->get_recommend_list($uid);
if (!empty($data) && is_array($data)) {
	$status = STATUS_OK;
}
unset($service);
unset($mysqli);

/* 返回结果 */
exit(response_json($status, $data));

