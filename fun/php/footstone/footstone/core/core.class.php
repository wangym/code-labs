<?php
	
	
	
	/*
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : core.class.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	abstract class core
	{
		// ---------- set attribute --------------------
		// data access object
		protected $_dao = null;
		
		
		// route object
		protected $_route = null;
		
		
		// template engine object
		protected $_view = null;
		// end set attribute
		
		
		// ---------- function __construct --------------------
		protected function __construct()
		{
		}// end function __construct
		
		
		/* ---------- function __destruct --------------------
		public function __destruct()
		{
		}// end function __destruct */
		
		
		// ---------- function __call --------------------
		public function __call($method,$param)
		{
			route::cannot_found_method($method,$param);
		}// end function __call
		
		
		/* ---------- function __set --------------------
		public function __set($name,$value)
		{
			$this->$name = $value;
		}// end function __set */
		
		
		/*/ ---------- function __get --------------------
		public function __get($name)
		{
			return $this->$name;
		}// end function __get */
		
		
		//* ---------- function _load --------------------
		// load object attrib
		protected function _load($name)
		{
			if (!isset($this->$name))
			{
				//echo 'new attrib:'.$name.'<br>';
				//((method_exists($this,$name)) ? $this->$name() : '');
				$this->$name();
			}
			//echo 'old attrib:'.$name.'<br>';
			
			return $this->$name;
		}// end function _load */
		
		
		// ---------- function _dao --------------------
		protected function _dao()
		{
			((!is_object($this->_dao)) ? $this->_dao = init_class(DAO_NAME) : '');
		}// end function _dao
		
		
		// ---------- function _route --------------------
		protected function _route()
		{
			((!is_object($this->_route)) ? $this->_route = init_class(ROUTE_NAME) : '');
		}// end function _route
		
		
		// ---------- function _view --------------------
		protected function _view()
		{
			((!is_object($this->_view)) ? $this->_view = init_class(VIEW_NAME) : '');
		}// end function _view	
		
		
		// ---------- function run --------------------
    	public function run()
    	{    		
        	return call_user_func_array(array($this, $this->_load('_route')->get_method()),$this->_load('_route')->get_param());
    	}// end function run
	}
	
	
	
?>
