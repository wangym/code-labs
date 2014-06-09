<?php

// get.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
$params = api_sign_verify();

/* 执行逻辑 */
$goodsService = new GoodsService();
$result = $goodsService->getText($params);
unset($goodsService);

/* 响应结果 */
response_json($result->status, $result->data->toArray());

