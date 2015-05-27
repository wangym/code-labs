<?php

/**
 * 数据库辅助工具类
 *
 * @category wolequ
 * @package util
 * @author WANG Yumin
 */
class Db
{
	/**
     * 构造方法
     *
     * @return void
     */
	public function __construct()
	{
	}

	/**
     * 析构方法
     *
     * @return void
     */
	public function __destruct()
	{
	}

	/**
	 * 数据访问对象初始化
	 *
	 * @return void
	 */
	private static function _setDao()
	{
		$params = array(
			'host' => 'localhost',
			'username' => 'root',
			'password' => '123123',
			'dbname' => 'wolequ',
		);

		$dao = Zend_Db::factory('PDO_MYSQL', $params);
		$dao->query('set names utf8');
		Zend_Db_Table::setDefaultAdapter($dao);

		return $dao;
	}

	/**
	 * 全局注册获取并返回
	 *
	 * @return object $dao
	 */
	public static function getDao()
	{
		if (Zend_Registry::isRegistered('dao'))
		{
			$dao = Zend_Registry::get('dao');
		}
		else
		{
			$dao = self::_setDao();
			Zend_Registry::set('dao', $dao);
		}

		return $dao;
	}

	/**
	 * 断开数据库的连接
	 *
	 * @return void
	 */
	public static function closeConnection()
	{
		if (Zend_Registry::isRegistered('dao'))
		{
			Zend_Registry::get('dao')->closeConnection();
		}
	}
}
