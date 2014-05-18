<?php

// RedisExt.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
class RedisExt extends Redis {

	/**
	 *
	 */
	public function __construct() {

		global $_database;
		try {
			$ret = $this->connect($_database[_ENV]['host'], $_database[_ENV]['port']);
			if (false === $ret) {
				die($this->getLastError());
			}
			$ret = $this->auth($_database[_ENV]['username'] . "-" . $_database[_ENV]['password'] . "-" . $_database[_ENV]['dbname']);
			if (false === $ret) {
				die($this->getLastError());
			}
		} catch (RedisException $e) {
			die("Uncaught exception " . $e->getMessage());
		}
		unset($_database);
	}

	/**
	 *
	 */
	public function __destruct() {
	}
}

