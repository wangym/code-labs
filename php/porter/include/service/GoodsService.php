<?php

// GoodsService.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IGoodsService {
}

/**
 *
 */
class GoodsService implements IGoodsService {

	/**
	 *
	 */
	private $dao;

	/**
	 *
	 */
	public function __construct() {

		$this->dao = new RedisNoSqlDao();
	}

	/**
	 *
	 */
	public function __destruct() {

		unset($this->dao);
	}

	public function create($params) {

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

