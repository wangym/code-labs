<?php

// validator.php

(!defined('_BODY_SHOW') ? exit('Access Denied!') : '');

/**
 * 对签名参数sign进行md5()字符串校验
 *
 * @param string $sign
 * @param string $content
 * @return 
 */
function sign_verify($sign, $content) {

	$result = false;
	$prefix = "_body_show_";
	$suffix = "_v1.0";
	if (!empty($sign) && $sign === md5($prefix.$content.$suffix)) {
		$result = true;
	}

	return $result;
}

