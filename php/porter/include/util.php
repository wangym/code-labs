<?php

// util.php

(!defined('_APP') ? exit('Access Denied!') : '');

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
 * @return string URL服务器的返回结果
 */
function http_post($url, array $data = array()) {

	$result = '';

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
 * @return string 获取值
 */
function http_receive($key, $default = '') {

	$value = $default;

	if (isset($_REQUEST[$key])) {
		$value = strip_tags(trim($_REQUEST[$key]));
	}

	return $value;
}

/**
 * 依据入参生成接口响应用的JSON字符串
 *
 * @param int $status
 * @param mixed $data
 * @return string JSON
 */
function response_json($status, $data = '') {

	$json = json_encode(!is_array($data) 
		? array(
			'status' => $status
		)
		: array(
			'status' => $status, 
			'data' => $data
		)
	);

	return $json;
}

/**
 * 获取访问客户端的真实IP地址(加强版)
 *
 * @return string IP地址
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
 * 数组转换成字符串支持KeyValue对应
 *
 * @param array $array
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
		$value = isset($array[$key]) ? $array[$key] : '';
	}

	return $value;
}

/**
 * 按键数组生成KV对应的数组
 *
 * @param array $keys 键数组
 * @param array $vals 值数组
 * @param array $defaults 默认补充,可选
 * @return array
 */
function to_kv_array($keys, $vals, $defaults = array()) {

	$result = array();

	if (!empty($defaults) && is_array($defaults)) {
		foreach($defaults as $key => $value) {
			if (!isset($vals[$key])) {
				$vals[$key] = $value;
			}
		}
	}
	//print_r($keys);exit;

	foreach($keys as $value) {
		$result[$value] = &$vals[$value];
	}
	//var_dump($result);

	return $result;
}

