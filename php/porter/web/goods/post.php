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
    $response = post_action($userId);
}
// 结果
$data['response'] = $response;
$data['time'] = _TIME;
$data['token'] = get_secret($userId, _TIME);

/* 响应结果 */
render_html('goods_post', $data);


// =========================
// 以下是私有函数:
// =========================

/**
 * @param int $userId
 * @return string $response
 */
function post_action($userId) {

    $response = get_response_json(_STATUS_VERIFY_ERROR, __METHOD__);

    if (validate_web_post()) {
        $time = http_receive('time');
        $token = http_receive('token');
        if (verify_secret($token, $userId, $time)) {
            $params = array(
                'userId' => $userId,
                'text' => http_receive('text')
            );
            $goodsService = new GoodsService();
            $resultPojo = $goodsService->postText($params);
            unset($goodsService);
            $response = get_pojo_json($resultPojo, _FILE_NAME);
        }
    } else {
        $response = get_response_json(_STATUS_PARAMETER_ERROR, __METHOD__);
    }

    return $response;
}

