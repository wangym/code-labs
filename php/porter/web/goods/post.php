<?php

// post.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

$test = 'hello, world!';
// render
$html = render(dirname(__FILE__), 'post');
echo $html;

