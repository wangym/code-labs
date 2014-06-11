<?php

// get.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
$userId = 1; // TODO:获取登录用户

/* 执行逻辑 */
$data = array();
$goodsService = new GoodsService();
$result = $goodsService->getText(array('userId' => $userId));
unset($goodsService);
// 结果
$data['text'] = $result->data->value;

/* 响应结果 */
render_html('goods_get', $data);

