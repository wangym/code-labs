<?php

// post.php

require($_SERVER['DOCUMENT_ROOT'].'include/bootstrap.php');

/* 验证参数 */
$params = api_sign_verify();

/* 执行逻辑 */
$goodsService = new GoodsService();
$result = $goodsService->postText($params);
unset($goodsService);

/* 响应结果 */
exit(response_json($result, $params));

