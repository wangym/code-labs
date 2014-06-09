<?php

// util.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 * 接口签名验证入口
 *
 * @return array $params 参数集合
 */
function api_sign_verify() {

	$json = http_receive('json');
	if (empty($json)) {
		exit(response_json(_STATUS_PARAMETER_ERROR));
	}
	$sign = http_receive('sign');
	$time = http_receive('time');
	if (!_DEBUG && !sign_verify($sign, $json . $time)) {
		exit(response_json(_STATUS_SIGN_ERROR));
	}
	$params = json_decode($json, true);
	if (empty($params) || !is_array($params)) {
		exit(response_json(_STATUS_PARAMETER_ERROR));
	}

	return $params;
}

/**
 * 数组转换成字符串支持KeyValue对应
 *
 * @param array $array 必需是一维数组
 * @return string key1=value1,key2=value2,....
 */
function array_to_string($array) {

	$string = '';

	if (!empty($array) && is_array($array)) {
		foreach ($array as $key => $value) {
			$string .= ",$key=$value";
		}
		$string = substr($string, 1);
	}

	return $string;
}

/**
 * 按数组键名从数组中获取一个值
 *
 * @param mixed $key
 * @param array $array
 * @return mixed $value
 */
function get_array_value($key, $array) {

	$value = '';

	if (!empty($key) && !empty($array) && is_array($array)) {
		$value = (isset($array[$key]) ? $array[$key] : '');
	}

	return $value;
}

/**
 * 获取访问客户端的真实IP地址(加强版)
 *
 * @return string $ip IP地址
 */
function get_ip() {

	$ip = '';

	if (getenv('HTTP_CLIENT_IP') && strcasecmp(getenv('HTTP_CLIENT_IP'), 'unknown')) {
		$ip = getenv('HTTP_CLIENT_IP');
	} else if (getenv('HTTP_X_FORWARDED_FOR') && strcasecmp(getenv('HTTP_X_FORWARDED_FOR'), 'unknown')) {
		$ip = getenv('HTTP_X_FORWARDED_FOR');
	} else if (getenv('REMOTE_ADDR') && strcasecmp(getenv('REMOTE_ADDR'), 'unknown')) {
		$ip = getenv('REMOTE_ADDR');
	} else if (isset($_SERVER['REMOTE_ADDR']) && $_SERVER['REMOTE_ADDR'] && strcasecmp($_SERVER['REMOTE_ADDR'], 'unknown')) {
		$ip = $_SERVER['REMOTE_ADDR'];
	}

	return $ip;
}

/**
 * 将数据集POST到指定URL并获取返回结果
 *
 * @param string $url 请求的地址
 * @param array $data 发送的数组(可选)
 * 一维数组格式,如:
 * array(
 *  'key1' => 'val1',
 *  'key2' => 'val2',
 *  'key3' => 'val3'
 * );
 * @return string $result URL服务器的返回结果
 */
function http_post($url, array $data = array()) {

	$query = (!empty($data) && is_array($data) ? http_build_query($data) : '');
	$options = array('http' => 
		array(
			'method' => 'POST',
			'header' => 'Content-type: application/x-www-form-urlencoded',
			'content' => $query,
			'timeout' => 5
		)
	);
	$context = stream_context_create($options);
	$result = file_get_contents($url, false, $context);

	return $result;
}

/**
 * 按指定键名取HTTP参数并进行安全处理
 *
 * @param string $key 键名称
 * @param mixed $default 默认值
 * @return string $value 获取值
 */
function http_receive($key, $default = '') {

	$value = $default;

	if (isset($_REQUEST[$key])) {
		$value = strip_tags(trim($_REQUEST[$key]));
	}

	return $value;
}

/**
 * 状态值是否是正确
 *
 * @param int $status
 * @return boolean $result
 */
function is_true_status($status) {

	$result = false;

	if (200 === $status) {
		$result = true;
	}

	return $result;
}

/**
 * 渲染页面并传参数
 *
 * @param string $dirname
 * @param string $template
 * @param mixed $data
 */
function render_html($dirname, $template, $data = '') {

    $data;
    header('Content-type: text/html');
    echo require($dirname . '/template/' . $template . '.php');
    unset($data);
}

/**
 * 依据入参生成接口响应用的JSON字符串
 *
 * @param int $status
 * @param mixed $data
 */
function response_json($status, $data = '') {

	global $_message;
	$json = json_encode(!empty($data) && is_array($data)
		? array(
			'status' => $status,
			'message' => get_array_value($status, $_message),
			'data' => $data
		)
		: array(
			'status' => $status,
			'message' => get_array_value($status, $_message)
		)
	);
	unset($_message);

    header('Content-type: application/json');
    echo $json;
    unset($json);
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

/**
 * 按键数组生成KV对应的数组
 *
 * @param array $keys 键数组
 * @param array $values 值数组
 * @param array $defaults 默认补充,可选
 * @return array $result
 */
function to_kv_array($keys, $values, $defaults = array()) {

	$result = array();

	if (!empty($defaults) && is_array($defaults)) {
		foreach($defaults as $key => $value) {
			if (!isset($values[$key])) {
				$values[$key] = $value;
			}
		}
	}
	//print_r($keys);exit;

	foreach($keys as $value) {
		$result[$value] = &$values[$value];
	}
	//var_dump($result);

	return $result;
}

