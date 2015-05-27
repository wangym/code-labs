<?php

// unique_check.php

require_once('../../include/bootstrap.php');
(!defined('_APP') ? exit('Access Denied!') : '');

/* 基础验证 */
// 唯一检查验证
$body = unique_check_verify();

/* 业务逻辑 */
$status = STATUS_FIELD_NOT_UNIQUE;
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$service->data = $body;
if ($service->is_unique_check()) {
	$status = STATUS_OK;
}
unset($service);
unset($mysqli);

/* 返回结果 */
exit(response_json($status));

