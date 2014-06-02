<?php

// cli.php

require($_SERVER['DOCUMENT_ROOT'].'include/bootstrap.php');

/* 验证参数 */
$action = http_receive('action', 'none');
$userId = http_receive('userId', 0);

/* 执行逻辑 */
// flushDB
if ('flushDB' === $action) {

    $driver = new RedisNoSqlDriver();
    echo $driver->flushDB();
}

