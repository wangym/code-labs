<?php

/**
 * 输入过滤验证类
 *
 * @category wolequ
 * @package util
 * @author WANG Yumin
 */
class Input
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
	 * 后端文章发布
	 *
     * @param array $args
	 * @return array|string|boolean(false)
	 */
	public function publish($args)
	{
		// 必须非空数组
		if (empty($args) || !is_array($args))
		{
			return false;
		}

		// Zend_Validate_Utf8Length
		Zend_Loader::loadFile('Utf8Length.php');

		// 设置过滤规则
		$filters = array(
			'*' => 'StringTrim',
			'alias' => 'StripTags',
			'title' => 'StripTags',
			'tags' => 'StripTags'
		);

		// 设置验证规则
		$validators = array(
			'title' => array(array('Utf8Length', '5', '100'), 'presence' => 'required'),
			'alias' => array(array('Utf8Length', '10', '50'), 'presence' => 'required'),
			'brief' => array(array('Utf8Length', '10', '250'), 'presence' => 'required'),
			'content' => array(array('Utf8Length', '30', '6000'), 'presence' => 'required'),
			'tags' => array(array('Utf8Length', '2', '40'), 'presence' => 'required')
		);

		$input = new Zend_Filter_Input($filters, $validators, $args);

		if ($input->hasInvalid() || $input->hasMissing())
		{
			foreach ($input->getInvalid() as $key => $value)
			{
				$value;
				return $key;
			}
		}
		else
		{
			return array(
				'alias' => $input->getUnescaped('alias'), 'title' => $input->getUnescaped('title'),
				'brief' => $input->getUnescaped('brief'), 'content' => $input->getUnescaped('content'),
				'tags' => $input->getUnescaped('tags')
			);
		}

		return false;
	}
}
