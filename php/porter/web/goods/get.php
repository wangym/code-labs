<?php

// get.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
$userId = 1; // TODO:获取登录用户

/* 执行逻辑 */
$data = array();
$text = get_action($userId);
// 结果
$data['text'] = $text;

/* 响应结果 */
render_html('goods_get', $data);


// =========================
// 以下是私有函数:
// =========================

/**
 * @param int $userId
 * @return string $text
 */
function get_action($userId) {

    $params = array(
        'userId' => $userId
    );
    $goodsService = new GoodsService();
    $result = $goodsService->getText($params);
    unset($goodsService);
    $text = (!empty($result) && !is_null($result->data) && $result->data instanceof KvPojo
        ? $result->data->value : '');

    return $text;
}

