<?php

// business.php

(!defined('_APP') ? exit('Access Denied!') : '');

// adapter
require($_SERVER['DOCUMENT_ROOT'] . '/include/adapter/MySqlAdapter.php');
require($_SERVER['DOCUMENT_ROOT'] . '/include/adapter/NoSqlAdapter.php');
// dao
require($_SERVER['DOCUMENT_ROOT'] . '/include/dao/PorterNoSqlDao.php');
// pojo
require($_SERVER['DOCUMENT_ROOT'] . '/include/pojo/ResultPojo.php');
// service
require($_SERVER['DOCUMENT_ROOT'] . '/include/service/AdminService.php');
require($_SERVER['DOCUMENT_ROOT'] . '/include/service/GoodsService.php');

