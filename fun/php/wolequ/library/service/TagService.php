<?php

/**
 * 标签相关操作服务类
 *
 * @category wolequ
 * @package service
 * @author WANG Yumin
 */
class TagService
{
	/**
     * 标签默认间隔(英文空格)
     */
	const INTERVAL_DEFAULT = ' ';

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
	 * 提交多行新标签
	 *
	 * @param integer $articleId 文章编号
	 * @param string $tags 标签字串
	 * @param string $interval 间隔标志
	 * @return integer 成功行数
	 */
	public function createTag($articleId, $tags, $interval='')
	{
		if (!is_numeric($articleId) || empty($tags))
		{
			return false;
		}

		// 未传间隔标志则用默认
		if (empty($interval))
		{
			$interval = self::INTERVAL_DEFAULT;
		}

		// 间隔符必须存在
		if (false === strpos($tags, $interval))
		{
			return false;
		}

		// 标签字串折数组
		$tagArray = explode($interval, $tags);
		// 结果必须是数组
		if (empty($tagArray) || !is_array($tagArray))
		{
			return false;
		}

		// 开启数据访问层
		$tagDao = new TagDao();
		// 相关变量初始化
		$success = 0;

		// 逐个写入数据表
		foreach ($tagArray as $value)
		{
			$row = array(
				'articleId' => $articleId,
				'tag' => trim($value)
			);

			if ($tagDao->insert($row))
			{
				$success++;
			}

			$row = array();
		}

		return $success;
	}
}
