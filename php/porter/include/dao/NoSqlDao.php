<?php

// NoSqlDao.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface INoSqlDao {

	/**
	 * 建立连接
	 *
	 * @param array $database
	 */
	public function connection($database);
}

/**
 *
 */
(!class_exists('Redis') ? exit('Fatal error: Class Redis not found!') : '');
class RedisNoSqlDao extends Redis implements INoSqlDao {

	/**
	 *
	 */
	public function __construct() {

		global $_database;
		$this->connection($_database);
		unset($_database);
	}

	/**
	 *
	 */
	public function __destruct() {

        $this->close();
	}

	public function connection($database) {

		try {
			$result = $this->connect($database[_ENV]['host'], $database[_ENV]['port']);
			if (false === $result) {
				exit($this->getLastError());
			}
			$result = $this->auth($database[_ENV]['username'] . "-" . $database[_ENV]['password'] . "-" . $database[_ENV]['dbname']);
			if (false === $result) {
				exit($this->getLastError());
			}
		} catch (RedisException $e) {
			exit($e->getMessage());
		}
	}

	/**
	 * @param string $name
	 * @return mixed $value
	 */
	public function __get($name) {

		return $this->$name;
	}

	/**
	 * @param string $name
	 * @param mixed $value
	 */
	public function __set($name, $value) {

		$this->$name = $value;
	}
}

