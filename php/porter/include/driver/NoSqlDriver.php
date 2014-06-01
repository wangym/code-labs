<?php

// NoSqlDriver.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface INoSqlDriver {

	/**
	 * 建立连接
	 *
	 * @param array $database
	 */
	public function connect($database);
}

/**
 *
 */
(!class_exists('Redis') ? exit('Fatal error: Class Redis not found!') : '');
class RedisNoSqlDriver extends Redis implements INoSqlDriver {

	/**
	 *
	 */
	public function __construct() {

		global $_database;
		$this->connect($_database);
		unset($_database);
	}

	/**
	 *
	 */
	public function __destruct() {

		$this->close();
	}

	public function connect($database) {

		try {
			$result = parent::connect($database[_ENV]['host'], $database[_ENV]['port']);
			if (false === $result) {
				exit($this->getLastError());
			}
			$result = $this->auth($database[_ENV]['username']."-".$database[_ENV]['password']."-".$database[_ENV]['dbname']);
			if (false === $result) {
				exit($this->getLastError());
			}
		} catch (RedisException $e) {
			exit($e->getMessage());
		}
	}
}

