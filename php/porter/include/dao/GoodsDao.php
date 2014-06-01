<?php

// GoodsDao.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IGoodsDao {

    /**
     * @param string $key
     * @param string $value
     * @param int $ttl (optional)
     * @return boolean $result
     */
    public function set($key, $value, $ttl);
}

/**
 *
 */
class GoodsDao implements IGoodsDao {

    /**
     *
     */
    private $driver;

    /**
     *
     */
    public function __construct() {

        $this->driver = new RedisNoSqlDriver();
    }

    /**
     *
     */
    public function __destruct() {

        unset($this->driver);
    }

    public function set($key, $value, $ttl = 0) {

        $result = false;

        if (!empty($key) && !empty($value)) {
            if (0 <= $ttl) {
                $result = $this->driver->setex($key, $ttl, $value);
            } else {
                $result = $this->driver->set($key, $value);
            }
        }

        return $result;
    }
}

