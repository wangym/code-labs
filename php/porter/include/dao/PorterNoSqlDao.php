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
class PorterNoSqlDao extends RedisNoSqlAdapter implements IPorterNoSqlDao {

    /**
     *
     */
    public function __construct() {
        parent::__construct();
    }

    /**
     *
     */
    public function __destruct() {
        parent::__destruct();
    }
}

