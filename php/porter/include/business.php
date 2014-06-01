<?php

// business.php

(!defined('_APP') ? exit('Access Denied!') : '');

// dao
require($_SERVER['DOCUMENT_ROOT'].'include/dao/PorterDao.php');
// driver
require($_SERVER['DOCUMENT_ROOT'].'include/driver/MySqlDriver.php');
require($_SERVER['DOCUMENT_ROOT'].'include/driver/NoSqlDriver.php');
// pojo
require($_SERVER['DOCUMENT_ROOT'].'include/pojo/ResultPojo.php');
// service
require($_SERVER['DOCUMENT_ROOT'].'include/service/GoodsService.php');

