<?php

// mysqli_ext.php

(!defined('_APP') ? exit('Access Denied!') : '');
(!class_exists('mysqli') ? exit('Fatal error: Class mysqli not found!') : '');

/**
 *
 */
class mysqli_ext extends mysqli {

	// 配置集 
	private $config = array(
		'dev' => array(
			'host' => '10.232.15.31',
			'username' => 'root',
			'password' => '',
			'database' => 'wuma'
		),
		'prod' => array(
			'host' => '',
			'username' => '',
			'password' => '',
			'database' => ''
		),
	);
	// 字符集
	private $charset = 'gbk';

	/**
	 *
	 */
	public function __construct() {

		parent::__construct(
			$this->config[_ENV]['host'], 
			$this->config[_ENV]['username'], 
			$this->config[_ENV]['password'], 
			$this->config[_ENV]['database'] 
		);
		(mysqli_connect_errno() ? exit('Connect failed:'.mysqli_connect_error()) : '');

		$this->query("set names $this->charset");
	}

	/**
	 *
	 */
	public function __destruct() {
		$this->close();
	}

	/**
	 *
	 */
	public function fetch_all($result) {

		$array = array();
		$row = array();
		$i = 0;

		while($row = $result->fetch_assoc()) {
			$array[$i] = $row;
			$i++;
		}

		return $array;
	}

}

