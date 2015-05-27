<?php

/**
 * 公共实用工具类
 *
 * @category wolequ
 * @package util
 * @author WANG Yumin
 */
class Common
{
	/**
     * 默认附件文件夹路径
     *
     * @var string
     */
	public static $attachmentPath = './static/attachment/';

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
	 * 获取客户端真实IP
	 *
	 * @return string
	 */
	public static function getClientIp()
	{
		if (getenv('HTTP_CLIENT_IP') && strcasecmp(getenv('HTTP_CLIENT_IP'), 'unknown'))
		{
			return getenv('HTTP_CLIENT_IP');
		}
		elseif (getenv('HTTP_X_FORWARDED_FOR') && strcasecmp(getenv('HTTP_X_FORWARDED_FOR'), 'unknown'))
		{
			return getenv('HTTP_X_FORWARDED_FOR');
		}
		elseif (getenv('REMOTE_ADDR') && strcasecmp(getenv('REMOTE_ADDR'), 'unknown'))
		{
			return getenv('REMOTE_ADDR');
		}
		elseif (isset($_SERVER['REMOTE_ADDR']) && $_SERVER['REMOTE_ADDR'] && strcasecmp($_SERVER['REMOTE_ADDR'], 'unknown'))
		{
			return $_SERVER['REMOTE_ADDR'];
		}

		return 'error';
	}

	/**
	 * 获取附件目录路径(若不存在则建立支持批量)
	 *
	 * @param string $rule optional 批量规则
	 * @return string
	 */
	public static function getAttachmentPath($rule='')
	{
		// 返回默认附件路径
		if ('__ROOT__' == $rule)
		{
			return self::$attachmentPath;
		}

		// 若未传默认时间规则
		if (empty($rule))
		{
			// 计算当前年月
			$rule = date('Y/m/d/', $_SERVER['REQUEST_TIME']);
		}

		$fullPath = self::$attachmentPath.$rule;
		//echo $fullPath;exit;

		if (!empty($fullPath) && !is_dir($fullPath))
		{
			// 递归建立最大权限目录
			if (!mkdir($fullPath, 0777, true))
			{
				throw new Exception("Folder to create a failure: {$fullPath}!");
			}
		}

		return $fullPath;
	}

	/**
	 * 获取净化后的文件名
	 * Do a cleanup of the file name to avoid possible problems
	 *
	 * @param string $fileName 原文件名
	 * @return string
	 */
	public static function getSanitizeFileName($fileName)
	{
		$fileName = stripslashes($fileName) ;

		// Replace dots in the name with underscores (only one dot can be there... security issue).
		$fileName = preg_replace('/\\.(?![^.]*$)/', '_', $fileName) ;

		// Remove \ / | : ? * " < >
		$fileName = preg_replace('/\\\\|\\/|\\||\\:|\\?|\\*|"|<|>|[[:cntrl:]]/', '_', $fileName);

		return $fileName ;
	}
}
