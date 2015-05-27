<?php
	
	
	
	/*
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : smarty_ext.class.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	require_once(PATH_LIB_PLUGIN.'\\smarty\\libs\\Smarty.class.php');
	
	
	((!class_exists(Smarty)) ? exit('Fatal error: Class Smarty not found!') : '');	
	
	
	class smarty_ext extends Smarty
	{
		// ---------- set attribute --------------------
		// route object
		private $_route = null;
		
		
		// template file name
		private $_view_tplname = null;
		// end set attribute
		
		
		// ---------- function __construct --------------------
		public function __construct()
		{
			$this->template_dir    = PATH_APP_VIEW.'\\template'; // 模板文件夹
			$this->compile_dir     = PATH_APP_VIEW.'\\compile'; // 编译文件夹
			$this->cache_dir	   = PATH_APP_VIEW.'\\cache'; // 缓存文件夹
			$this->left_delimiter  = '<{';
			$this->right_delimiter = '}>';
			
			$this->_route = init_class(ROUTE_NAME);
		}// end function __construct
		
		
		/* ---------- function __destruct --------------------
		public function __destruct()
		{
		}// end function __destruct */
		
		
		//* ---------- function __call --------------------
		public function __call($method,$param)
		{
			route::cannot_found_method($method,$param);
		}// end function __call */
		
		
		/* ---------- function __set --------------------
		public function __set($name,$value)
		{
			$this->$name = $value;
		}// end function __set */
		
		
		/* ---------- function __get --------------------
		public function __get($name)
		{
			return $this->$name;
		}// end function __get */
		
		
		//* ---------- function _get_view_tplname --------------------
		private function _get_view_tplname()
		{
			//for example: class_method.tpl
			((!isset($this->_view_tplname)) ? $this->_view_tplname = $this->_route->get_class().'_'.$this->_route->get_method().VIEW_TPLEXT : '');
			
			return $this->_view_tplname;
		}// end function _get_view_tplname */
		
		
		//* ---------- function _put_view_pubpath --------------------
		private function _put_view_pubpath()
		{
			$this->assign('view_pubpath',PATH_PUB_ROOT);
		}// end function _put_view_pubpath */
		
		
		//* ---------- function _put_view_formkey --------------------
		private function _put_view_formkey()
		{
			$this->assign('view_formkey',encrypt(time()));
		}// end function _put_view_formkey */
		
		
		//* ---------- function output --------------------
		public function output()
		{
			$this->_put_view_pubpath(); //用于js/css/swf等
			$this->_put_view_formkey(); //用于表单
			
			$this->display($this->_get_view_tplname());//,QUERY_STRING,QUERY_STRING
			exit;
		}// end function output */
	}
	
	
	
?>