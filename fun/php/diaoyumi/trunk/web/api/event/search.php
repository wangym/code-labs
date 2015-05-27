<?php

// search.php

require_once('../../include/bootstrap.php');
(!defined('_APP') ? exit('Access Denied!') : '');

/* 基础验证 */
// 事件搜索验证
$body = event_search_verify();

/* 业务逻辑 */
$status = STATUS_ERROR;
$data = '';
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$service->data = $body;
if (is_array($data = $service->get_event_search())) {
	$status = STATUS_OK;
}
unset($service);
unset($mysqli);

/* 返回结果 */
exit(response_json($status, $data));

