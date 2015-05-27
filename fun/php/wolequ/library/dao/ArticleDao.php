<?php

/**
 * 文章主表数据访问层
 *
 * @category wolequ
 * @package dao
 * @author WANG Yumin
 */
class ArticleDao
{
	/**
     * 数据表名
     *
     * @var string
     */
	protected $_tableName = 'tbl_article';

	/**
     * 数据表主键
     *
     * @var string
     */
	protected $_primaryKey = 'id';

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
	 * @return integer|boolean(false) 记录主键
	 */
	public function insert($row)
	{
		// 进行补全基础数据字段
		$row['created'] = $row['modified'] = REQUEST_TIME;

		// 插入行若成功返回主键
		if ($this->_dao->insert($this->_tableName, $row))
		{
			return $this->_dao->lastInsertId();
		}

		return false;
	}

	/**
	 * 取回最近的结果
	 *
	 * @param integer $limit
	 * @param integer $offset
	 * @return array
	 */
	public function fetchRecentList($limit=1, $offset=0)
	{
		$field = 'id,alias,title,title_image,brief,tags,created';
		$query = "SELECT {$field} FROM {$this->_tableName}
			WHERE status = 1 ORDER BY id DESC LIMIT {$limit} OFFSET {$offset};";

		$stmt = $this->_dao->prepare($query);
		return ($stmt->execute() ? $stmt->fetchAll() : array());
	}

	/**
	 * 取回按编号结果
	 *
	 * @param integer $articleId
	 * @return array
	 */
	public function fetchByArticleId($articleId)
	{
		$field = '*';
		$query = "SELECT {$field} FROM {$this->_tableName}
			WHERE id = :id AND status = 1;";

		$stmt = $this->_dao->prepare($query);
		return ($stmt->execute(array('id' => $articleId)) ? $stmt->fetch() : array());
	}
}
