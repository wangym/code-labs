<?php

// validator.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 * 接口签名验证入口
 *
 * @return array $params 参数集合
 */
function api_sign_verify() {

	$json = http_receive('json');
    if (empty($json)) {
        exit(response_json(STATUS_PARAMETER_ERROR));
    }
    $sign = http_receive('sign');
    $time = http_receive('time');
    if (!_DEBUG && !sign_verify($sign, $json . $time)) {
        exit(response_json(STATUS_SIGN_ERROR));
    }
    $params = json_decode($json, true);
    if (empty($params) || !is_array($params)) {
        exit(response_json(STATUS_PARAMETER_ERROR));
    }

	return $params;
}

/**
 * 签名验证最小方法
 *
 * @param string $sign
 * @param string $content
 * @return boolean $result true=签名验证正确|false=失败
 */
function sign_verify($sign, $content) {

	$result = false;

	$key = _SECRET_KEY . $content;
	if ('dev' === _ENV) {
		echo "<!-- md5($key) -->";
	}
	if (!empty($sign) && $sign === md5($key)) {
		$result = true;
	}

	return $result;
}

