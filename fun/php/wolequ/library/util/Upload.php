<?php

/**
 * 文件上传工具类
 *
 * @category wolequ
 * @package util
 * @author WANG Yumin
 */
class Upload
{
	/**
     * 允许文件类型
     *
     * @var string
     */
	private $_fileType =array('image/gif', 'image/jpg', 'image/jpeg', 'image/pjpeg', 'image/png');

	/**
     * 文件最大容量(单位:字节)
     *
     * @var string
     */
	private $_maxSize = '60000';

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
	 * @param string $field 上传表单变量
	 * @return boolean(false)|string(url)
	 */
	public function file($field)
	{
		// 判断表单变量是否存在
		$file = (isset($_FILES[$field]) ? $_FILES[$field] : '');
		//var_dump($file);

		// 不能为空且是数组格式
		if (empty($file) || !is_array($file))
		{
			return false;
		}

		// 进行文件格式安全判断
		if (!in_array(strtolower($file['type']), $this->_fileType))
		{
			return false;
		}

		// 进行上传代号错误判断
		if (0 != $file['error'])
		{
			return false;
		}

		// 进行文件容量大小判断
		if ($file['size'] > $this->_maxSize || 0 == $file['size'])
		{
			return false;
		}

		// 净化文件名
		$fileName = Common::getSanitizeFileName($file['name']);
		// 获取扩展名
		$fileExt = substr($fileName, (strrpos($fileName, '.'))) ;
		// 保存的路径
		$savePath = Common::getAttachmentPath();
		// 文件新名称
		$saveFile = $field.'_'.md5(uniqid(rand(), true)).$fileExt;
		// 完整的保存
		$saveFull = "{$savePath}{$saveFile}"; //echo $saveFull;

		// copy方式上传
		if (@copy($file['tmp_name'], $saveFull))
		{
			chmod($saveFull, 0777);
			return array($field => $saveFull);
		}

		// move方式上传
		if (@move_uploaded_file($file['tmp_name'], $saveFull))
		{
			chmod($saveFull, 0777);
			return array($field => $saveFull);
		}

		return false;
	}
}
