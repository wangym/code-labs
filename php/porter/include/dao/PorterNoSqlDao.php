<?php

// PorterNoSqlDao.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IPorterNoSqlDao {
}

/**
 *
 */
class PorterNoSqlDao extends RedisNoSqlDriver implements IPorterNoSqlDao {

    /**
     *
     */
    public function __construct() {
    }

    /**
     *
     */
    public function __destruct() {
    }
}

