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
	public $data;

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
	 * email是否不存在
	 * 
	 * @param string $email 可选
	 * @return boolean true不存在|false存在
	 */
	public function is_email_not_exist($email = '') {

		$exist = false; // 悲观存在
		if (empty($email)) {
			$email = get_array_value('email', $this->data);
		}

		$sql = 'SELECT COUNT(*) FROM `user` WHERE `email`=?';
		if ($stmt = $this->dao->prepare($sql)) {
			$stmt->bind_param('s', $email);
			$stmt->execute();
			$stmt->bind_result($count);
			$stmt->fetch();
			if (0 === $count) {
				$exist = true;
			}

			$stmt->close();
		}

		return $exist;
	}

	/**
	 * name是否不存在
	 *
	 * @param string $name 可选
	 * @return boolean true不存在|false存在
	 */
	public function is_name_not_exist($name = '') {

		$exist = false; // 悲观存在
		if (empty($name)) {
			$name = get_array_value('name', $this->data);
		}

		$sql = 'SELECT COUNT(*) FROM `user` WHERE `name`=?';
		if ($stmt = $this->dao->prepare($sql)) {
			$stmt->bind_param('s', $name);
			$stmt->execute();
			$stmt->bind_result($count);
			$stmt->fetch();
			if (0 === $count) {
				$exist = true;
			}
			$stmt->close();
		}

		return $exist;
	}

	/**
	 * 提交注册用户
	 *
	 * @return integer 记录主键
	 */
	public function get_register_id() {

		$id = 0;
		$keys = array('email', 'name', 'password', 'mobile');

		$sql = 'INSERT INTO `user` (created,'.implode(',', array_values($keys)).') VALUE (now(),?,?,?,?)';
		if ($stmt = $this->dao->prepare($sql)) {
			call_user_func_array(
				array(&$stmt, 'bind_param'),
				array_merge(array('ssss'), to_kv_array($keys, $this->data))
			);
			$stmt->execute();
			//echo $stmt->error;
			if (1 === $stmt->affected_rows) {
				$id = $stmt->insert_id;
			}
			$stmt->close();
		}

		return $id;
	}

	/**
	 * 判断是否唯一
	 *
	 * @return boolean true唯一|false不唯一
	 */
	public function is_unique_check() {

		$result = false; // 悲观不唯一
		$field = get_array_value('field', $this->data);
		$value = get_array_value('value', $this->data);

		if ('email' === $field) {
			$result = $this->is_email_not_exist($value);
		} else if ('name' === $field) {
			$result = $this->is_name_not_exist($value);
		}

		return $result;
	}

	/**
	 * 用户登录判断
	 *
	 * @return integer 记录主键
	 */
	public function get_auth_id() {

		$id = 0; // 悲观失败
		$user = get_array_value('user', $this->data);
		$type = get_array_value('type', $this->data);
		$password = get_array_value('password', $this->data);
		$sql = '';

		if ('email' === $type) {
			$sql = 'SELECT id FROM `user` WHERE `email`=? AND `password`=?'; 
		} else if ('name' === $type) {
			$sql = 'SELECT id FROM `user` WHERE `name`=? AND `password`=?'; 
		}
		if (!empty($sql)) {
			if ($stmt = $this->dao->prepare($sql)) {
				$stmt->bind_param('ss', $user, $password);
				$stmt->execute();
				$stmt->bind_result($id);
				$stmt->fetch();
			}
		}

		return $id;
	}

	/**
	 * 用户修改密码
	 *
	 * @return boolean true修改成功|false失败
	 */
	public function is_change_password() {

		$result = false; // 悲观失败
		$user_id = get_array_value('user_id', $this->data);
		$old_password = get_array_value('old_password', $this->data);
		$new_password = get_array_value('new_password', $this->data);

		$sql = 'UPDATE `user` SET `password`=? WHERE `id`=? AND `password`=?';
		if ($stmt = $this->dao->prepare($sql)) {
			$stmt->bind_param('sis', $new_password, $user_id, $old_password);
			$stmt->execute();
			if (1 === $stmt->affected_rows) {
				$result = true;
			}
		}

		return $result;
	}

	/**
	 * 用户发布事件
	 *
	 * @return boolean true发布成功|false失败
	 */
	public function is_event_new() {

		$result = false; // 悲观失败
		$keys = array('user_id', 'rid', 'type', 'event_time', 'lat', 'lng', 'place', 'is_new_place', 
			'companion', 'pictrue', 'title', 'price', 'intro', 'properties', 'status');
		$defaults = array(
			'is_new_place' => 'N',
			'status' => 0
		);

		$sql = 'INSERT INTO `event` (created,'.implode(',', array_values($keys)).') VALUE 
			(now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)';
		if ($stmt = $this->dao->prepare($sql)) {
			call_user_func_array(
				array(&$stmt, 'bind_param'), 
				array_merge(array('isssddsssssdssi'), to_kv_array($keys, $this->data, $defaults))
			);
			$stmt->execute();
			//echo $stmt->error;
			if (1 === $stmt->affected_rows) {
				$result = true;
			}
			$stmt->close();
		}

		return $result;
	}

	/**
	 * rid是否是唯一
	 * 
	 * @param string $rid 可选
	 * @return boolean true唯一|false不唯一
	 */
	public function is_rid_unique($rid = '') {

		$unique = false; // 悲观不唯一
		if (empty($rid)) {
			$rid = get_array_value('rid', $this->data);
		}

		$sql = 'SELECT COUNT(*) FROM `event` WHERE `rid`=?';
		if ($stmt = $this->dao->prepare($sql)) {
			$stmt->bind_param('s', $rid);
			$stmt->execute();
			$stmt->bind_result($count);
			$stmt->fetch();
			if (0 === $count) {
				$unique = true;
			}
			$stmt->close();
		}

		return $unique;
	}

	/**
	 * 有权限操作资源
	 * 
	 * @return boolean true有权限|false无权限
	 */
	public function is_rid_permission() {

		$permission = false; // 悲观无权限
		$user_id = get_array_value('user_id', $this->data);
		$rid = get_array_value('rid', $this->data);

		$sql = 'SELECT COUNT(*) FROM `event` WHERE `user_id`=? AND `rid`=?';
		if ($stmt = $this->dao->prepare($sql)) {
			$stmt->bind_param('is', $user_id, $rid);
			$stmt->execute();
			$stmt->bind_result($count);
			$stmt->fetch();
			if (1 === $count) {
				$permission = true;
			}
			$stmt->close();
		}

		return $permission;
	}

	/**
	 * 用户删除事件 
	 * 
	 * @return boolean true删除成功|false失败
	 */
	public function is_event_delete() {

		$result = false; // 悲观失败
		$user_id = get_array_value('user_id', $this->data);
		$rid = get_array_value('rid', $this->data);

		$sql = 'DELETE FROM `event` WHERE `user_id`=? AND `rid`=? LIMIT 1';
		if ($stmt = $this->dao->prepare($sql)) {
			$stmt->bind_param('is', $user_id, $rid);
			$stmt->execute();
			if (1 === $stmt->affected_rows) {
				$result = true;
			}
			$stmt->close();
		}

		return $result;
	}

	/**
	 * 用户搜索事件 
	 * 
	 * @return array
	 */
	public function get_event_search() {

		$data = array(); // 搜索结果
		$_pos = get_array_value('_pos', $this->data);
		$_limit = get_array_value('_limit', $this->data);
		$_fields = get_array_value('_fields', $this->data);

		$sql = "SELECT $_fields FROM `event` LIMIT $_pos,$_limit";
		$data = $this->dao->fetch_all($this->dao->query($sql));

		return $data;
	}

}

