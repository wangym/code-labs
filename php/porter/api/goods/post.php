<?php

// post.php

require($_SERVER['DOCUMENT_ROOT'] . 'include/bootstrap.php');

/* 验证参数 */
$params = api_sign_verify();

/* 执行逻辑 */
$goods = new GoodsService();
$result = $goods->handlingGoods($params);
unset($goods);

/* 响应结果 */
exit(response_json($result, $params));

