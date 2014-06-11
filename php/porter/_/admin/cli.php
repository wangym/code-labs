<?php

// cli.php

require(dirname(dirname(__DIR__)) . '/include/bootstrap.php');

/* 验证参数 */
$action = http_receive('action', 'none');
$userId = http_receive('userId', 0);

/* 执行逻辑 */
// flushDB
if ('flushDB' === $action) {
    $adminService = new AdminService();
    $result = $adminService->flushDB();
    unset($adminService);
    exit('flushDB=' . $result);
}

