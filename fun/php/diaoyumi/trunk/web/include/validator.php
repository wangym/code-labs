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

/**
 * 唯一检查校验
 *
 * @return array
 */
function unique_check_verify() {

	$body = api_sign_verify();
	$field = get_array_value('field', $body);
	$value = get_array_value('value', $body);

	if (empty($field) || empty($value)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}
	if ('email' !== $field && 'name' !== $field) {
		exit(response_json(STATUS_NOT_SUPPORT));
	}

	return $body;
}

/**
 * 用户登录校验
 *
 * @return array
 */
function auth_verify() {

	$body = api_sign_verify();
	$user = get_array_value('user', $body);
	$type = get_array_value('type', $body);
	$password = get_array_value('password', $body);

	if (empty($user) || empty($type) || empty($password)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}
	if ('email' !== $type && 'name' !== $type) {
		exit(response_json(STATUS_NOT_SUPPORT));
	}

	return $body;
}

/**
 * 修改密码校验
 *
 * @return array
 */
function change_password_verify() {

	$body = api_sign_verify();
	$user_id = get_array_value('user_id', $body);
	$old_password = get_array_value('old_password', $body);
	$new_password = get_array_value('new_password', $body);

	if (empty($user_id) || empty($old_password) || empty($new_password)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}

	return $body;
}

/**
 * 事件发布校验
 *
 * @return array
 */
function event_new_verify() {

	$body = api_sign_verify();
	$user_id = get_array_value('user_id', $body);
	$rid = get_array_value('rid', $body);
	$type = get_array_value('type', $body);
	$event_time = get_array_value('event_time', $body);
	$status = get_array_value('status', $body);

	if (empty($user_id) || empty($rid)  || empty($type) || empty($event_time) || empty($status)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}

	return $body;
}

/**
 * 事件删除校验
 *
 * @return array
 */
function event_delete_verify() {

	$body = api_sign_verify();
	$user_id = get_array_value('user_id', $body);
	$rid = get_array_value('rid', $body);

	if (empty($user_id) || empty($rid)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}

	return $body;
}

/**
 * 事件搜索校验
 *
 * @return array
 */
function event_search_verify() {

	$body = api_sign_verify();
	$_pos = get_array_value('_pos', $body);
	$_limit = get_array_value('_limit', $body);
	$_fields = get_array_value('_fields', $body);

	if (empty($_pos) || empty($_limit) || empty($_fields)) {
		exit(response_json(STATUS_PARAMETER_ERROR));
	}

	return $body;
}

