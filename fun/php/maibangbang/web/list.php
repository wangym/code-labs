<?php

// list.php

require_once('../include/bootstrap.php');
require_once(_SMARTY_DIR.'Smarty.class.php');

/* 数据准备 */
// 获取参数验证
$uid = http_receive('uid');
$type = http_receive('type');
if (empty($uid)) {
	exit("uid is required!");	
}

/* 业务逻辑 */
$data = array();
$nickname = '';
$mysqli = new mysqli_ext();
$service = new service();
$service->dao = $mysqli;
$data = $service->get_recommend_list($uid, $type);
if (!empty($data) && is_array($data)) {
	$nickname = $data[0]['user_nick'];
}
unset($service);
unset($mysqli);

/* 模板渲染 */
$smarty = new Smarty();
$smarty->left_delimiter = "<{";
$smarty->right_delimiter = "}>";
$smarty->setTemplateDir(SMARTY_DIR.'templates/');
$smarty->setCompileDir(SMARTY_DIR.'templates_c/');
$smarty->setConfigDir(SMARTY_DIR.'configs/');
$smarty->setCacheDir(SMARTY_DIR.'cache/');
$smarty->assign('uid', $uid);
$smarty->assign('type', $type);
$smarty->assign('data', $data);
$smarty->assign('nickname', $nickname);
$smarty->display('list.tpl');

