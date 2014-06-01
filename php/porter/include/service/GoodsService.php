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
	public $dao;
	public $params;

	/**
	 *
	 */
	public function __construct() {
	}

	/**
	 *
	 */
	public function __destruct() {
	}
}

