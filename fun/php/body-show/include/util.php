<?php

// util.php

(!defined('_BODY_SHOW') ? exit('Access Denied!') : '');

/**
 * 将数据集POST到指定URL并获取返回结果
 *
 * @param string $url 请求的地址 必传
 * @param array $data 发送的数组 可选
 * 一维数组格式,如:
 * array(
 *  'key1' => 'val1',
 *  'key2' => 'val2',
 *  'key3' => 'val3'
 * );
 * @return string URL服务器的返回结果
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
 * @return string 获取值
 */
function http_receive($key, $default = '') {

	if (isset($_REQUEST[$key])) {
		$default = trim(strip_tags($_REQUEST[$key]));
	}

	return $default;
}

/**
 * 依据入参生成接口响应用的JSON字符串
 *
 * @param int $code
 * @param string $message
 * @param mixed $data
 * @return string JSON
 */
function response_json($code, $data = "") {

	require('config.php');
	$message = (isset($_msg[$code]) ? $_msg[$code] : "unkown");
	$array = array(
		'code' => $code,
		'message' => $message,
		'data' => $data
	);
	$json = json_encode($array);

	return $json;
}

/**
 * 获取访问客户端的真实IP地址(加强版)
 *
 * @return string IP地址
 */
function get_ip() {

	$ip = "-";
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

	$string = "";

	if (!empty($array) && is_array($array)) {
		foreach ($array as $key => $value) {
			$string .= ",$key=$value";
		}
		$string = substr($string, 1);
	}

	return $string;
}

/**
 * 将内容写到日志目录内已包括前缀目录
 *
 * @param string $flag 业务名称
 * @param string $key 关键字段
 * @param mixed $content 文件内容
 * @return int
 */
function write_log($flag, $key, $content) {

	$filepath = _DIR.'log/'.date('Y', _TIME).'/'.date('m', _TIME).'/';
	(file_exists($filepath) ? '' : mkdir($filepath, 0777, true));
	$filename = $flag.date('Ymd', _TIME);
	$content = array_to_string($content);

	$result = file_put_contents($filepath.$filename, '-'._DATETIME."\0"._FILENAME."\0"._IP."\0$key\0$content\n", FILE_APPEND);

	return $result;
}

