<?php

/**
 * 基础信息数据访问层
 *
 * @category wolequ
 * @package dao
 * @author WANG Yumin
 */
class BaseDao
{
	/**
     * 数据表名
     *
     * @var string
     */
	protected $_tableName = 'tbl_base';

	/**
     * 数据表主键
     *
     * @var string
     */
	protected $_primaryKey = 'name_space';

	/**
     * 表主键的值
     *
     * @var string
     */
	protected $_primaryValue = 'wolequ';

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
	 * 改一行基础记录
	 *
	 * @param array $set 修改数组
	 * @return integer 影响行数
	 */
	public function update($set)
	{
		// where语句
		$where = $this->_dao->quoteInto("{$this->_primaryKey} = ?", $this->_primaryValue);
		// 更新表数据返回更新的行数
		return $this->_dao->update($this->_tableName, $set, $where);
	}
}
