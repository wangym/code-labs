<?php

/**
 * 文章相关操作服务类
 *
 * @category wolequ
 * @package service
 * @author WANG Yumin
 */
class ArticleService
{
	/**
     * Consts for article status (0是未知)
     */
	const STATUS_AVAILABLE = 1; // 文章可用(可在前台展现)
	const STATUS_CLOSE = -1; // 文章关闭(管理员手工关闭)

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
	 * 发布一篇新文章
	 *
	 * @param array $row 写入数组
	 * @return integer|boolean(false) 记录主键
	 */
	public function createArticle($row)
	{
		if (empty($row) || !is_array($row))
		{
			return false;
		}

		// 进行补全逻辑数据字段
		$row['status'] = self::STATUS_AVAILABLE;

		$articleDao = new ArticleDao();
		return $articleDao->insert($row);
	}

	/**
	 * 取回最近的结果
	 *
	 * @param integer $limit
	 * @param integer $offset
	 * @return array
	 */
	public function readRecentList($limit=1, $offset=0)
	{
		$limit = intval($limit);
		$offset = intval($offset);

		$articleDao = new ArticleDao();
		return $articleDao->fetchRecentList($limit, $offset);
	}

	/**
	 * 按文章编号读取
	 *
	 * @param integer $articleId
	 * @return array
	 */
	public function readByArticleId($articleId)
	{
		if (0 < $articleId)
		{
			$articleId = intval($articleId);

			$articleDao = new ArticleDao();
			return $articleDao->fetchByArticleId($articleId);
		}

		return array();
	}
}
