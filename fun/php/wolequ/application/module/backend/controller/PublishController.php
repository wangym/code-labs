<?php

/**
 * 后端发布控制器
 *
 * @category wolequ
 * @package backend
 * @author WANG Yumin
 */
class Backend_PublishController extends Zend_Controller_Action
{
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
		$this->_cfgCommon = Zend_Registry::get('cfgCommon');
	}

	/**
	 * indexAction
	 *
	 * @return void
	 */
	public function indexAction()
	{
		$this->view->headLink()->appendStylesheet('/static/style/backend.css', 'screen');
		$this->view->headScript()->appendFile('/static/script/3rd_part/fckeditor/fckeditor.js');

		// 获取_POST全部参数
		$postArgs = $this->getRequest()->getPost(); //var_dump($postArgs);
		if ($this->getRequest()->isPost())
		{
			// 参数过滤验证处理
			$input = new Input();
			$publishData = $input->publish($postArgs);
			// 若非数组则已失败
			if (!is_array($publishData))
			{
				// 错误提示词句
				$this->view->message = $this->_cfgCommon->msgFailure->$publishData;
				// 输入的原内容
				$this->view->data = $postArgs;
			}
			else
			{
				// 进行封面图片上传
				$upload = new Upload();
				$uploadData = $upload->file('title_image');
				// 若非数组则已失败
				if (!is_array($uploadData))
				{
					// 错误提示词句
					$this->view->message = $this->_cfgCommon->msgFailure->title_image;
				}
				else
				{
					// 将字段发布结果与图片上传结果两数组合并可得写表数组
					$createRow = array_merge($publishData, $uploadData);

					// 发布业务逻辑
					$articleService = new ArticleService();
					$articleId = $articleService->createArticle($createRow);
					// 开始关联逻辑
					if (0 < $articleId)
					{
						// 操作标签专表
						$tagService = new TagService();
						$tagService->createTag($articleId, $publishData['tags']);

						// 发布成功提示语
						$this->view->message = $this->_cfgCommon->msgSuccess->publish;
						// 跳转以重新发布
						$this->_redirect('backend/publish');
					}
					else
					{
						// 发布错误提示语
						$this->view->message = $this->_cfgCommon->msgError->publish;
					}
				}

				// 断开数据库的连接
				Db::closeConnection();
			}

			// 用户输入原内容
			$this->view->data = $postArgs;
		}
		else
		{
			// 默认的提示词句
			$this->view->message = $this->_cfgCommon->msgGeneral->publish;
		}
	}
}
