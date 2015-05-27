<?php

// validator.php

/**
 * API签名验证
 *
 * @return array
 */
function api_sign_verify() {

	$result = array();
	$time = http_receive('time');
	$body = http_receive('body');
	$sign = http_receive('sign');

	if (empty($time) || empty($body)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}
	if (!sign_verify($sign, $time.$body)) {
		exit(response_json(STATUS_SIGN_FAIL));
	}
	$result = json_decode($body, true);
	if (empty($result) || !is_array($result)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}

	return $result;
}

/**
 * API签名验证
 *
 * @param string $sign
 * @param string $content
 * @return boolean 
 */
function sign_verify($sign, $content) {

	$result = false;
	$key = $content._SECRET_KEY;
	if ('dev' === _ENV) {
		echo "<!--".md5($key)."-->";
	}

	if (!empty($sign) && $sign === md5($key)) {
		$result = true;
	}

	return $result;
}

/**
 * 用户注册校验
 *
 * @return array
 */
function register_verify() {

	$body = api_sign_verify();
	$email = get_array_value('email', $body);
	$name = get_array_value('name', $body);
	$password = get_array_value('password', $body);
	$mobile = get_array_value('mobile', $body);

	if (empty($email) || empty($name) || empty($password)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}

	return $body;
}

