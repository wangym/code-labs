<?php

/**
 * 后端退出控制器
 *
 * @category wolequ
 * @package backend
 * @author WANG Yumin
 */
class Backend_LogoutController extends Zend_Controller_Action
{
	/**
     * initialization
     *
     * @return void
     */
	public function init()
	{
		// 销毁会话数据
		Zend_Session::destroy(true);
	}

	/**
	 * indexAction
	 *
	 * @return void
	 */
	public function indexAction()
	{
		// 禁用渲染视图
		$this->_helper->viewRenderer->setNoRender();
		// 禁用嵌入布局
		$this->_helper->layout->disableLayout();

		// 跳到网站首页
		$this->_redirect('/', array('exit' => true));
	}
}
