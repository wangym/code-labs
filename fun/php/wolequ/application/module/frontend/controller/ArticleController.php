<?php

/**
 * 前端文章主控器
 *
 * @category wolequ
 * @package frontend
 * @author WANG Yumin
 */
class Frontend_ArticleController extends Zend_Controller_Action
{
	/**
	 * indexAction
	 *
	 * @return void
	 */
	public function indexAction()
	{
		// 获取文章的编号
		$articleId = $this->getRequest()->getParam('id', 0);
		// 最近的文章列表
		$articleService = new ArticleService();
		$articleRow = $articleService->readByArticleId($articleId);

		// 必须为非空数组
		if (!empty($articleRow) && is_array($articleRow))
		{
			$this->view->articleRow = $articleRow;
		}
		else
		{
			// 为空跳转到首页
			$this->_redirect('/');
		}
	}
}
