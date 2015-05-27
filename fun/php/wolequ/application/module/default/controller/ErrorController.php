<?php

/**
 * 全局错误控制器
 *
 * @category wolequ
 * @package default
 * @author WANG Yumin
 */
class ErrorController extends Zend_Controller_Action
{
	/**
	 * errorAction
	 *
	 * @return void
	 */
	public function errorAction()
	{
		// 生成日志文件名
		$file = '../data/log/web/'.date('Ymd').'-exception.log';
		// 若不存在则创建
		if (!file_exists($file))
		{
			file_put_contents($file, REQUEST_TIME."\r\n");
		}

		// 若容量小则追加
		if (filesize($file) < 102400) // 102400 bytes
		{
			$error = $this->getRequest()->getParam('error_handler');
			// Log the exception:
			$exception = $error->exception;
			$log = new Zend_Log(new Zend_Log_Writer_Stream($file));
			$log->debug($exception->getMessage()); //.$exception->getTraceAsString()."\n"
		}

		$this->_redirect('/');
	}
}
