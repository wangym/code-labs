<?php

/**
 * 管理员相关操作服务类
 *
 * @category wolequ
 * @package service
 * @author WANG Yumin
 */
class AdminService
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
	 * 获取管理员登录结果
	 *
	 * @param string $account 管理账号
	 * @param string $password 登录密码
	 * @return array|boolean(false)
	 */
	public function getLoginResult($account, $password)
	{
		if (empty($account) || empty($password))
		{
			return false;
		}

		// 临时账号密码校验算法
		$token = $account.date('mdHi'); // 后缀为动态日期
		if (!is_numeric($account) && $token == $password)
		{
			// 客户端真实IP
			$ip = Common::getClientIp();
			// 开通目录权限(面向fckeditor自带的图片上传功能会读取该IP进行校验)
			file_put_contents(Common::getAttachmentPath('__ROOT__').'allow.ip', ip2long($ip));

			// 登录成功
			return array(
				'role' => 'admin',
				'account' => $account,
				'name' => 'wolequ',
				'ip' => $ip,
				'time' => REQUEST_TIME,
			);
		}

		// 登录失败
		return false;
	}
}
