<?php

// mysqli_ext.php

(!defined('_BODY_SHOW') ? exit('Access Denied!') : '');
(!class_exists('mysqli') ? exit('Fatal error: Class mysqli not found!') : '');

/**
 *
 */
class mysqli_ext extends mysqli {

	/**
	 *
	 */
	public function __construct($env) {

		require('config.php');
		parent::__construct(
			$_db[$env]['host'], 
			$_db[$env]['username'], 
			$_db[$env]['password'], 
			$_db[$env]['database'] 
		);
		(mysqli_connect_errno() ? exit('Connect failed:'.mysqli_connect_error()) : '');

		$this->query('set names '.$_db[$env]['charset']);
	}

	/**
	 *
	 */
	public function __destruct() {
		//$this->close();
	}

	/**
	 *
	 */
	public function fetch_all($result) {

		$array = array();
		$i = 0;
		while($rows = $result->fetch_array()) {
			$array[$i] = $rows;
			$i++;
		}

		return $array;
	}
}

