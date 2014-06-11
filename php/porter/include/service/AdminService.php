<?php

// AdminService.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IAdminService {

    /**
     * @return bool $result
     */
    public function flushDB();
}

/**
 *
 */
class AdminService implements IAdminService {

    /**
     * @var PorterNoSqlDao
     */
    private $dao;

    /**
     *
     */
    public function __construct() {
        $this->dao = new PorterNoSqlDao();
    }

    /**
     *
     */
    public function __destruct() {
        unset($this->dao);
    }

    public function flushDB() {

        $result = $this->dao->flushDB();

        return $result;
    }
}

