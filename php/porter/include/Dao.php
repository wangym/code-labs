<?php

// Dao.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface INoSqlDao {

	public function set($key, $value);

	public function get($key);
}

/**
 *
 */
(!class_exists('Redis') ? exit('Fatal error: Class Redis not found!') : '');
class RedisDao extends Redis implements INoSqlDao {

	/**
	 *
	 */
	public function __construct() {

		global $_database;
		try {
			$ret = $this->connect($_database[_ENV]['host'], $_database[_ENV]['port']);
			if (false === $ret) {
				exit($this->getLastError());
			}
			$ret = $this->auth($_database[_ENV]['username'] . "-" . $_database[_ENV]['password'] . "-" . $_database[_ENV]['dbname']);
			if (false === $ret) {
				exit($this->getLastError());
			}
		} catch (RedisException $e) {
			exit("Uncaught exception " . $e->getMessage());
		}
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
	public function set($key, $value) {

		$result = false;

		try {
			$ret = $this->set($key, $value);
			if (true === $ret) {
				$result = true;
			}
		}  catch (RedisException $e) {
			exit("Uncaught exception " . $e->getMessage());
		}
	}
}

