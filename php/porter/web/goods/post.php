<?php

// post.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
$action = http_receive('action');
$userId = 1; // TODO:获取登录用户

/* 执行逻辑 */
$data = array();
$response = '';
if ('post' === $action) {
    // 发布
    $response = call_api_post($userId);
}
// 展现
$data['response'] = $response;
$data['time'] = _TIME;
$data['token'] = get_secret($userId, _TIME);

/* 响应结果 */
render_html(dirname(__FILE__), 'post', $data);


// =========================
// 以下是私有函数:
// =========================

/**
 * @param int $userId
 * @return string $response
 */
function call_api_post($userId) {

    $response = get_response_json(_STATUS_VERIFY_ERROR, __METHOD__);

    if (validate_web_post()) {
        $time = http_receive('time');
        $token = http_receive('token');
        if (verify_secret($token, $userId, $time)) {
            $json = json_encode(array(
                'userId' => $userId,
                'text' => http_receive('text')
            ));
            $post = array(
                'json' => $json,
                'time' => _TIME,
                'secret' => get_secret($json, _TIME)
            );
            $response = http_post(_API_POST, $post);
        }
    } else {
        $response = get_response_json(_STATUS_PARAMETER_ERROR, __METHOD__);
    }

    return $response;
}

