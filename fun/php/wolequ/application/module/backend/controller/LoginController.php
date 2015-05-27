<?php

/**
 * 后端登录控制器
 *
 * @category wolequ
 * @package backend
 * @author WANG Yumin
 */
class Backend_LoginController extends Zend_Controller_Action
{
	/**
     * 访客session
     *
     * @var object
     */
	private $_sessVisitor = null;

	/**
     * 全局config
     *
     * @var object
     */
	private $_cfgCommon = null;

	/**
     * initialization
     *
     * @return void
     */
	public function init()
	{
		$this->_sessVisitor = Zend_Registry::get('sessVisitor');
		$this->_cfgCommon = Zend_Registry::get('cfgCommon');
	}

	/**
	 * indexAction
	 *
	 * @return void
	 */
	public function indexAction()
	{
		//var_dump($this->_sessVisitor->data);

		// 获取_POST全部参数
		$postArgs = $this->getRequest()->getPost(); //var_dump($postArgs);
		// _POST请求执行逻辑
		if ($this->getRequest()->isPost())
		{
			// 生成账号和密码变量
			$account = (isset($postArgs['account']) ? $postArgs['account'] : '');
			$password = (isset($postArgs['password']) ? $postArgs['password'] : '');
			// 变量数据的常规过滤
			$account = trim(strtolower(strip_tags($account)));
			$password = trim(strtolower(strip_tags($password)));

			// 获取管理员登录结果
			$adminService = new AdminService();
			$loginResult = $adminService->getLoginResult($account, $password);
			if ($loginResult && is_array($loginResult))
			{
				// 设定管理员登录数据
				$this->_sessVisitor->data = $loginResult; // 登录成功的结果即登录数据
				// 设定会话的过期时间
				$this->_sessVisitor->setExpirationSeconds($this->_cfgCommon->session->expiration);
				// 锁定会话设为只读
				$this->_sessVisitor->lock();

				$this->_redirect('backend/publish');
			}
			else
			{
				Zend_Session::destroy(true);
				// 其余均视为错误
				$this->view->message = $this->_cfgCommon->msgFailure->login;
			}
		}
	}
}
