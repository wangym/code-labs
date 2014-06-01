<?php

// GoodsDao.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IPorterDao {

    /**
     * @param string $key
     * @param string $value
     * @param int $ttl (optional)
     * @return boolean $result
     */
    public function set($key, $value, $ttl);

    /**
     * @param string $key
     * @return string $value
     */
    public function get($key);

    /**
     * @param string $pattern
     * @return array $array
     */
    public function keys($pattern);
}

/**
 *
 */
class PorterDao implements IPorterDao {

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

    public function get($key) {

        $value = '';

        if (!empty($key)) {
            $value = $this->driver->get($key);
        }

        return $value;
    }

    public function keys($pattern) {

        $array = array();

        if (!empty($pattern)) {
            $array = $this->driver->keys($pattern);
        }

        return $array;
    }
}

