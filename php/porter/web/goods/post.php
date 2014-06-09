<?php

// post.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
$action = http_receive('action');
/* 执行逻辑 */
if ('set' === $action) {
    // 保存
} else {
    // 展现
}
/* 响应结果 */
render_html(dirname(__FILE__), 'post', $data);

