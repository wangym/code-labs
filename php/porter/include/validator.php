<?php

// validator.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 * 接口签名验证入口
 *
 * @return array 参数集合
 */
function api_sign_verify() {

	$result = array();

	$data= http_receive('data');
	$time = http_receive('time');
	if (empty($data) || empty($time)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}
	$sign = http_receive('sign');
	if (!sign_verify($sign, $data . $time)) {
		exit(response_json(STATUS_SIGN_ERROR));
	}
	$result = json_decode($data, true);
	if (empty($result) || !is_array($result)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}

	return $result;
}

/**
 * 签名验证最小方法
 *
 * @param string $sign
 * @param string $content
 * @return boolean 
 */
function sign_verify($sign, $content) {

	$result = false;

	$key = _SECRET_KEY.$content;
	if ('dev' === _ENV) {
		echo "<!--" . md5($key) . "-->";
	}
	if (!empty($sign) && $sign === md5($key)) {
		$result = true;
	}

	return $result;
}

