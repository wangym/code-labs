<?php

// get.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
$params = get_api_params();

/* 执行逻辑 */
$goodsService = new GoodsService();
$resultPojo = $goodsService->getText($params);
unset($goodsService);

/* 响应结果 */
render_json(get_pojo_json($resultPojo, _FILE_NAME));

