<?php

/**
 * 标签从表数据访问层
 *
 * @category wolequ
 * @package dao
 * @author WANG Yumin
 */
class TagDao
{
	/**
     * 数据表名
     *
     * @var string
     */
	protected $_tableName = 'tbl_tag';

	/**
     * 数据表主键
     *
     * @var string
     */
	protected $_primaryKey = '';

	/**
     * 数据表访问
     * @var object
     */
	protected $_dao = null;

	/**
     * 构造方法
     *
     * @return void
     */
	public function __construct()
	{
		$this->_dao = Db::getDao();
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
	 * 写入一行新记录
	 *
	 * @param array $row 写入数组
	 * @return integer 影响行数
	 */
	public function insert($row)
	{
		// 插入行若成功返回主键
		return $this->_dao->insert($this->_tableName, $row);
	}
}
