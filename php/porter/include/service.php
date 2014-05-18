<?php

// service.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
class service {

	/**
	 *
	 */
	public $dao;

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

	/**
	 * 获取用户的推荐数据列表
	 * 
	 * @param integer $uid 必选
	 * @param string $type 可选
	 * @return array
	 */
	public function get_recommend_list($uid, $type) {

		$data = array();

		$sql = "SELECT * FROM `notify` WHERE user_id = $uid";
		if (!empty($type)) {
			$sql .= " AND type=$type"; 
		}
		$sql .= " GROUP BY buyer_id";
		//echo $sql;
		$data = $this->dao->fetch_all($this->dao->query($sql));

		return $data;
	}

}

