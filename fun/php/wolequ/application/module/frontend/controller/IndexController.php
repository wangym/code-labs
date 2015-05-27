<?php

/**
 * 前端主控制器
 *
 * @category wolequ
 * @package frontend
 * @author WANG Yumin
 */
class Frontend_IndexController extends Zend_Controller_Action
{
	/**
	 * indexAction
	 *
	 * @return void
	 */
	public function indexAction()
	{
		// 最近的文章列表
		$articleService = new ArticleService();
		$articleRecentList = $articleService->readRecentList(20, 0);

		// 必须为非空数组
		if (!empty($articleRecentList) && is_array($articleRecentList))
		{
			$this->view->articleRecentList = $articleRecentList;
		}
		else
		{
			$this->_helper->viewRenderer->setNoRender();
		}
	}
}
