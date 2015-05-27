<?php

// delete.php

require_once('../../include/bootstrap.php');
(!defined('_APP') ? exit('Access Denied!') : '');

/* 基础验证 */
// 事件删除验证
$body = event_delete_verify();

/* 业务逻辑 */
$status = STATUS_ERROR;
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$service->data = $body;
if (!$service->is_rid_permission()) {
	$status = STATUS_RID_NOT_PERMISSION; 
} else if ($service->is_event_delete()) {
	$status = STATUS_OK;
}
unset($service);
unset($mysqli);

/* 返回结果 */
exit(response_json($status));

