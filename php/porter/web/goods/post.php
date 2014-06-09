<?php

// post.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
/* 执行逻辑 */
/* 响应结果 */
$test = 'hello, world!';
render_html(dirname(__FILE__), 'post', $test);

