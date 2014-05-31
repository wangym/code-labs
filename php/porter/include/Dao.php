<?php

// Dao.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IDao {

	/**
	 * 建立连接
	 *
	 * @param array $database
	 * @return boolean $result
	 */
	public function connection($database);
}

/**
 *
 */
(!class_exists('Redis') ? exit('Fatal error: Class Redis not found!') : '');
class RedisDao extends Redis implements IDao {

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
	}

	/**
	 *
	 */
	public function connection($database) {

		$result = false;

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

		return $result;
	}
}

