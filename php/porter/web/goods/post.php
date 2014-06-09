<?php

// post.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

$test = 'hello, world!';
// render
renderHtml(dirname(__FILE__), 'post');

