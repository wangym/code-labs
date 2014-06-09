<?php

// business.php

(!defined('_APP') || !defined('_DIR_NAME') ? exit('Access Denied!') : '');

// adapter
require(_DIR_NAME . '/adapter/MySqlAdapter.php');
require(_DIR_NAME . '/adapter/NoSqlAdapter.php');
// dao
require(_DIR_NAME . '/dao/PorterNoSqlDao.php');
// pojo
require(_DIR_NAME . '/pojo/ResultPojo.php');
// service
require(_DIR_NAME . '/service/AdminService.php');
require(_DIR_NAME . '/service/GoodsService.php');

