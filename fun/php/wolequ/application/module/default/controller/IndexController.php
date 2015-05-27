<?php

/**
 * 全局主控制器
 *
 * @category wolequ
 * @package default
 * @author WANG Yumin
 */
class IndexController extends Zend_Controller_Action
{
	/**
	 * indexAction
	 *
	 * @return void
	 */
	public function indexAction()
	{
		$this->_forward('index', 'index', 'frontend');
	}
}
