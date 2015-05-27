<?php
	
	
	
	/*
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : core.func.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	// ---------- function remove_ill --------------------
	// Remove illegal string
    function remove_ill($str)
    {
		return (!get_magic_quotes_gpc() ? addslashes(trim($str)) : trim($str));
    }// function remove_ill end
	
	
    // ---------- function random_str --------------------
	function random_str($base,$len)
    {
		return substr(md5(uniqid(rand()*$base)),0,$len);
	}// function random_str end
	
	
    // ---------- function format_time --------------------
    function format_time($mode='Y-m-d H:i:s')
    {
		return date($mode,$_SERVER['REQUEST_TIME']);
    }// function format_time end
	
	
	// ---------- function init_class --------------------
	// ��������Ϊtest�����ļ�Ϊtest.class.php
	// $class_name:����,�ش���
	// $class_path:��Ŀ¼,Ĭ��Ϊ�����Ŀ¼
	// $param     :ʵ����ʱ����,Ĭ��Ϊnull
	function init_class($class_name,$class_path=PATH_LIB_CORE,$param=null)
	{
		static $objects = array();
		
	    if (isset($objects[$class_name]))
		{
			//echo $class_name.' is old class<br>';
			return $objects[$class_name];
	    }
		
		if (isset($class_name) && isset($class_path))
		{
			$class_path = "$class_path\\$class_name.class.php";
			//echo "$class_path<br>";
			if (file_exists($class_path))
			{
				require_once($class_path);
				
				if (class_exists($class_name,FALSE))
				{
					//echo $class_name.' is new class-----<br>';
					$objects[$class_name] = new $class_name($param);
					
					return $objects[$class_name];
			    }
			}
		}
		
		$objects[$class_name] = null;
		
		return false;
	}// function init_model end
	
	
    /* ---------- function __autoload --------------------
	function __autoload($class)
	{
		require_once "./library/common/$class.class.php";
	}//*/
	
	
	// ---------- function get_ip --------------------
	// ��ȡ�ͻ���IP��ַ
    function get_ip()
	{
		return $_SERVER['REMOTE_ADDR'];
    }// function get_ip end
	
	
	// ---------- function trans_charset --------------------
	// ת���ַ�������
    function trans_charset($string,$target)
	{
		return iconv(mb_detect_encoding($string),$target,$string);
    }// function trans_charset end
	
	
    // ---------- function check_ref --------------------
	function check_ref()
	{
		$srv = $_SERVER['SERVER_NAME']; //��������
		
		if (substr($_SERVER['HTTP_REFERER'],7,strlen($srv)) != $srv)
	    {
			//header('Location: /');
	 	    exit;
	    }
		
		return true;
	}// function check_ref end
	
	
	// ---------- function check_ip --------------------
	function check_ip($ip)
	{
		return (!strcmp(long2ip(sprintf('%u',ip2long($ip))),$ip) ? true : false);
	}// function check_ip end
	
	
	// ---------- function check_formkey --------------------
	function check_formkey($formkey)
	{
		$code = decrypt($formkey);
		
		return (($code >= 1199116800 && REQUEST_TIME - $code <= FORMKEY_TIMEOUT) ? true : false);
		
	}// function check_formkey end
	
	// ---------- function cut_tail --------------------
	// ȥ��$str�к�$flag��֮�����������
    function cut_tail($str,$flag)
    {
    	return (strpos($str,$flag) ? substr($str,0,strpos($str,$flag)) : $str);
    }// function cut_tail end
    
    
	// ---------- function array_fetch2opt --------------------
	/*
		for example:
		
		$src = array(0=>array('id'=>3,'name'=>'wang'),1=>array('id'=>4,'name'=>'zhang'));
		$key = array('id','name');
		print_r(array_fetch2opt($src,$key));exit;
		
		display:Array ( [3] => wang [4] => zhang ) ;
	*/
	// ˵������$key�����еĵ�һ��ֵ���$src��ά�����еļ���ֵ~
	//      Ϊ���������ͬ��,$key�����еĵڶ���ֵΪ�������ֵ��
	// ע��: $src:��ά���� $key:һά����,��ֻ�ж����ֵ
	// ���ڣ���mysqli_ext->fetch_array_all()��smarty��select��ʾ
	function array_fetch2opt($src,$key)
	{
		if (2 == is_array($src) && count($key))
		{
			$cnt = count($src);
			$array[] = '...'; //Ĭ����
			
			for ($i=0;$i<$cnt;$i++)
			{
				$array[$src[$i][$key[0]]] = $src[$i][$key[1]];
			}
			
			return $array;
		}
		
		return $src;
	}// function array_fetch2opt end
	
	
	// ---------- function encrypt --------------------
	// ����
	function encrypt($code)
	{
		$code     = base64_encode(strrev(str_rot13($code)));
        $code_len = strlen($code);
        $key      = md5(CRYPT_KEY); //��Կ
        $key_len  = strlen($key); 
        $i	      = 0;
        
        while ($i < $code_len)
       	{
            $str .= sprintf ("%'02s",base_convert(ord($code{$i})+ord($key{$key_len % $i+1}),10,32));
            $i++;
        }

        return $str;
    }// function encrypt end
	
	
	// ---------- function decrypt --------------------
	// ����
    function decrypt($code)
	{
		$arr = array();
       
        preg_match_all("/.{2}/", $code, $arr);
        $arr     = $arr[0];
        $key     = md5(CRYPT_KEY); //��Կ
        $key_len = strlen($key); 
        $i       = 0;
        
        foreach ($arr as $value)
        {
			$str .= chr(base_convert($value,32,10)-ord($key{$key_len % $i+1}));
            $i++;
        }
        
        $str = str_rot13(strrev(base64_decode($str)));
		
        return $str;
    }// function decrypt end
	
	
	
?>