<?php
	
	
	
	/*
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : mysqli_ext.class.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	((!class_exists(mysqli)) ? exit('Fatal error: Class mysqli not found!') : '');
	
	
	class mysqli_ext extends mysqli
	{
		// ---------- set attribute --------------------
		// end set attribute
		
		
		// ---------- function __construct --------------------
		public function __construct()
		{
			$db = array();
			$db_group = null;
			
			require_once(PATH_LIB_CONFIG.'db.cfg.php');		
			parent::__construct($db[$db_group]['host'],
								$db[$db_group]['user'],
								$db[$db_group]['pswd'],
								$db[$db_group]['dbname']);
			
			//((mysqli_connect_errno()) ? exit('Connect failed:'.mysqli_connect_error()) : '');
			
			$this->execute('set names '.$db[$db_group]['charset']);
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
		
		
		//* ---------- function fetch_all --------------------
		public function fetch_all($result)
		{
			$i = 0;
			while($rows = $result->fetch_array())
			{
				$array[$i] = $rows;
				$i++;
			}
			
			return $array;
		}// end function fetch_all */
		
		
		//* ---------- function execute --------------------
		public function execute($sql)
		{			
			return $this->query($sql);
		}// end function execute */
		
		
		//* ---------- function disconn --------------------
		public function disconn()
		{
			return $this->close();
		}// end function disconn */
		
		
		//* ---------- function next_result --------------------
		function next_result($link)
		{
			while(mysqli_next_result($link));
			//mysqli_next_result($link);
		}// end function next_result */
		
		
		//* ---------- function call_proc --------------------
		function call_proc($name,$array)
		{
			$param = implode(',',$array);
			
			$this->execute("CALL $proc($param)");
		}// end function call_proc */
	}
	
	
	
?>