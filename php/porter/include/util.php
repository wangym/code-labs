<?php

// util.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 * 数组转换成字符串支持KeyValue对应
 *
 * @param array $array 必需是一维数组
 * @return string key1=value1&key2=value2&key3=value3....
 */
function array_to_string($array) {

    $string = '';

    if (!empty($array) && is_array($array)) {
        foreach ($array as $key => $value) {
            $string .= '&' . $key . '=' . $value;
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
 * @param array $post 发送的数组(可选)
 * 一维数组格式,如:
 * array(
 *  'key1' => 'val1',
 *  'key2' => 'val2',
 *  'key3' => 'val3'
 * );
 * @return string $result URL服务器的返回结果
 */
function http_post($url, array $post = array()) {

    $query = (!empty($post) && is_array($post) ? http_build_query($post) : '');
    $options = array('http' =>
        array(
            'method' => 'POST',
            'header' => 'Content-type: application/x-www-form-urlencoded',
            'content' => $query,
            'timeout' => 5
        )
    );
    // var_dump($options);
    $context = stream_context_create($options);
    $result = file_get_contents($url, false, $context);

    return $result;
}

/**
 * 按指定键名取HTTP参数并进行安全处理
 *
 * @param string $key 键名称
 * @param mixed $default 默认值
 * @return mixed $value 获取值
 */
function http_receive($key, $default = '') {

    $value = $default;

    if (isset($_REQUEST[$key])) {
        $value = strip_tags(urldecode(trim($_REQUEST[$key])));
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

    if (is_int($status) && 200 === $status) {
        $result = true;
    }

    return $result;
}

/**
 * 渲染HTML页面
 *
 * @param string $filename
 * @param array $data
 */
function render_html($filename, array $data = array()) {

    if (!empty($filename)) {
        // 渲染
        header('Content-type: text/html');
        require(dirname(__FILE__) . '/template/' . $filename . '.tpl.php');
        unset($data);
        exit;
    }
}

/**
 * 渲染JSON页面
 *
 * @param string $json
 */
function render_json($json) {

    if (!empty($json)) {
        // 渲染
        header('Content-type: application/json');
        echo $json;
        unset($json);
        exit;
    }
}

/**
 * 依据入参生成接口返回用的JSON字符串
 *
 * @param int $status
 * @param string $trace
 * @param mixed $data
 * @return string $json
 */
function get_response_json($status, $trace, $data = '') {

    global $_message;
    $json = json_encode(!empty($data) && is_array($data)
            ? array(
                'status' => $status,
                'message' => get_array_value($status, $_message),
                'trace' => $trace,
                'data' => $data
            )
            : array(
                'status' => $status,
                'message' => get_array_value($status, $_message),
                'trace' => $trace
            )
    );
    unset($_message);

    return $json;
}

/**
 * @param object $result (see class:ResultPojo)
 * @param string $from
 * @return string $json
 */
function get_result_json($result, $from) {

    $json = get_response_json(_STATUS_ERROR, __METHOD__);

    if (!empty($result) && !is_null($result->data) && $result->data instanceof KvPojo && is_array($result->data->toArray())) {
        $json = get_response_json($result->status, $from, $result->data->toArray());
        unset($result);
    }

    return $json;
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
        foreach ($defaults as $key => $value) {
            if (!isset($values[$key])) {
                $values[$key] = $value;
            }
        }
    }

    foreach ($keys as $value) {
        $result[$value] = & $values[$value];
    }

    return $result;
}

// =========================
// 以下是生成/验证相关的函数:
// =========================

/**
 * 接口签名验证入口
 *
 * @return array $params 参数集合
 */
function get_api_params() {

    $json = http_receive('json');
    if (empty($json)) {
        render_json(get_response_json(_STATUS_PARAMETER_ERROR, __METHOD__));
    }
    $secret = http_receive('secret');
    $time = http_receive('time');
    if (!_DEBUG && !verify_secret($secret, $json, $time)) {
        render_json(get_response_json(_STATUS_VERIFY_ERROR, __METHOD__));
    }
    $params = json_decode($json, true);
    if (empty($params) || !is_array($params)) {
        render_json(get_response_json(_STATUS_DECODE_ERROR, __METHOD__));
    }

    return $params;
}

/**
 * 生成秘密字符串方法
 *
 * @param string $content
 * @param string $time
 * @return string $secret
 */
function get_secret($content, $time) {

    $secret = '';

    if (!empty($content) && is_numeric($time)) {
        $secret = md5(_SECRET_KEY . $content . $time);
    }

    return $secret;
}

/**
 * 秘密字符串验证方法
 *
 * @param string $secret
 * @param string $content
 * @param int $time
 * @return boolean $result
 */
function verify_secret($secret, $content, $time) {

    if (empty($secret) || empty($content) || !is_numeric($time)) {
        render_json(get_response_json(_STATUS_PARAMETER_ERROR, __METHOD__));
    }
    if (_TIME > (((int)$time) + 1 * 60)) {
        render_json(get_response_json(_STATUS_TIMEOUT_ERROR, __METHOD__));
    }
    $result = ($secret === get_secret($content, $time));
    if (!$result) {
        render_json(get_response_json(_STATUS_VERIFY_ERROR, __METHOD__));
    }

    return $result;
}

