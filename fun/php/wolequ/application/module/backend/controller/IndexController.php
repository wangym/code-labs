<?php

/**
 * 后端主控制器
 *
 * @category wolequ
 * @package backend
 * @author WANG Yumin
 */
class Backend_IndexController extends Zend_Controller_Action
{
	/**
	 * indexAction
	 *
	 * @return void
	 */
	public function indexAction()
	{
		// 转向登录入口
		$this->_forward('index', 'login');
	}
}
