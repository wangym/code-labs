<?php
	
	
	
	/*	
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : route.class.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	class route
	{
		// ---------- set attribute --------------------
		// URI类库名
		private $_class  = null;
		
		
		// URI方法名
		private $_method = null;
		
		
		// URI参数集
		private $_param  = array();
		// end set attribute
		
		
		// ---------- function __construct --------------------
		public function __construct()
		{
			(1 == ROUTE_MODE ? $this->_uri_sysvar() : (2 == ROUTE_MODE ? $this->_uri_assoc() : ''));
		}// end function __construct
		
		
		/* ---------- function __destruct --------------------
		public function __destruct()
		{
		}// end function __destruct */
		
		
		// ---------- function __call --------------------
		public function __call($method,$param)
		{
			$this->cannot_found_method($method,$param);
		}// end function __call
		
		
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
		
		
		// ---------- function get_class --------------------
		public function get_class()
		{			
			return (isset($this->_class) ? $this->_class : DEFAULT_CLASS);
		}// end function get_class
		
		
		// ---------- function get_method --------------------
		public function get_method()
		{
			return (isset($this->_method) ? $this->_method : DEFAULT_METHOD);
		}// end function get_method
		
		
		// ---------- function get_param --------------------
		public function get_param($key=null)
		{
			return ((isset($key)) ? $this->_param[$key] : $this->_param);
		}// end function get_param
		
		
		// ---------- function cannot_found_method --------------------
		public static function cannot_found_method($method,$param)
		{
			exit("can not found method: $method() with $param!");
		}// end function cannot_found_method
		
		
		// ---------- function cannot_found_page --------------------
		public static function cannot_found_page()
		{
			exit('can not found page!');
		}// end function cannot_found_page
		
		
		// ---------- function cannot_found_data --------------------
		public static function cannot_found_data()
		{
			exit('can not found data!');
		}// end function cannot_found_data		
		
		
		// ---------- function redirect --------------------
		public static function redirect($url)
		{
			header('LOCATION:'.($url)); //urlencode
			exit;
		}// end function cannot_found_page
		
		
		// ---------- function _uri_sysvar --------------------
		private function _uri_sysvar()
		{		
			((!empty($_POST) && !empty($_GET)) ? $param = $this->_uri_param_append() : (!empty($_POST) ? $param = $_POST : (!empty($_GET) ? $param = $_GET : '')));
			
			if (!empty($param) && is_array($param))
			{
				//类名
				$this->_class  = remove_ill($param[ROUTE_CLASS]);unset($param[ROUTE_CLASS]);
				
				//方法
				$this->_method = remove_ill($param[ROUTE_METHOD]);unset($param[ROUTE_METHOD]);
				
				//参数
				$this->_param  = $param;
			}
		}// end function _uri_sysvar
		
		
		// ---------- function _uri_assoc --------------------
		// examples http://localhost/index.php/blog/view/id-11-name-wym.html
		private function _uri_assoc()
		{
			$uri = explode(DEFAULT_PAGE,REQUEST_URI);
			if (isset($uri[1]))
			{
				//去除尾巴及之后的内容
				$uri[1] = cut_tail($uri[1],EXT_URI);
				//echo $uri[1];exit;
				
				//模块
				$core_arr  = explode('/',$uri[1]);
				$this->_class  = remove_ill(array_shift($core_arr));
				$this->_method = remove_ill(array_shift($core_arr));
				//print_r($core_arr);echo '<br>';
				
				//参数
				$param_arr = explode('-',$core_arr[0]);
				//print_r($param_arr);echo '<br>';
				$param_cnt = (is_array($param_arr) ? count($param_arr) : 0);
				for($i=0; $i<$param_cnt; $i++)
				{
					$this->_param[$param_arr[$i]] = $param_arr[$i+1];
					$i++;
				}
				// 追加$_POST $_GET内的参数
				((!empty($_POST) || !empty($_GET)) ? $this->_param = array_merge($this->_uri_param_append(),$this->_param) : '');
			}
			//exit;
		}// function _uri_assoc end
		
		
		// ---------- function _uri_param_append --------------------
		private function _uri_param_append()
		{
			//return $_REQUEST;
			return array_merge($_POST,$_GET);
		}// end function _uri_param_append
	}
	
	
	
?>