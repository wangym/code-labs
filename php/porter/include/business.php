<?php

// business.php

(!defined('_APP') ? exit('Access Denied!') : '');

// dao
require($_SERVER['DOCUMENT_ROOT'] . 'include/dao/GoodsDao.php');
// driver
require($_SERVER['DOCUMENT_ROOT'] . 'include/driver/MySqlDriver.php');
require($_SERVER['DOCUMENT_ROOT'] . 'include/driver/NoSqlDriver.php');
// service
require($_SERVER['DOCUMENT_ROOT'] . 'include/service/GoodsService.php');

