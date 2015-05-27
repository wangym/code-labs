<?php
	
	
	
	/*
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : core.cfg.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	header('Content-Type:text/html;charset=utf-8');
	//error_reporting('ALL');
	//set_magic_quotes_runtime(0); //Kill magic quotes	
	//ini_set('date.timezone','Asia/Shanghai');
	
	define('ROUTE_MODE',2); //1:普通方式GENERAL 2:伪静态PSEUDO 3:重写REWIRTE
	define('ROUTE_CLASS','class');
	define('ROUTE_METHOD','method');
	
	define('EXT_SELF','.php'); //echo pathinfo(__FILE__, PATHINFO_EXTENSION);exit;
	(2 == ROUTE_MODE ? define('EXT_URI','.') : ''); //伪扩展名 通用性:'.' 针对性:'.html'
	
	define('VIEW_NAME','smarty_ext'); //模板引擎类名
	define('VIEW_TPLEXT','.tpl'); //模板文件扩展名
	
	define('DAO_NAME','mysqli_ext'); //数据访问类名
	
	define('ROUTE_NAME','route'); //route访问类名
	
	define('REQUEST_TIME',$_SERVER['REQUEST_TIME']);
	define('REQUEST_URI',$_SERVER['REQUEST_URI']);
	define('QUERY_STRING',$_SERVER['QUERY_STRING']);
	
	define('DEFAULT_CLASS','base'); //默认类
	define('DEFAULT_METHOD','index'); //默认方法
	define('DEFAULT_PAGE','index.php/'); //默认主控页
	
	define('CRYPT_KEY','powered_by_zjuhz'); //密钥
	
	// 表单处理码过期时限 formkey_timeout
	// 1.无此处理码或处理码错误或过期则表单无法提交
	// 2.设置时请结合考虑:SESSION超时时长和用户体验
	// 3.单位:秒 如:60秒x60分x24小时 = 86400秒/天
	define('FORMKEY_TIMEOUT','600');
	
	
	
?>