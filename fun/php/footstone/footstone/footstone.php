<?php
	
	
	
	/*
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : footstone.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	// ----- 框架目录配置 -----
	// 目录计算
	$dirname  = dirname(__FILE__);
	$pathinfo = pathinfo($dirname);
	
	//根目录
	//define('PATH_ROOT',$_SERVER['DOCUMENT_ROOT']);
	
	//仓库主目录
	define('PATH_LIB_COMMON',$pathinfo['dirname'].'\\common');
	
	//插件主目录
	define('PATH_LIB_PLUGIN',$pathinfo['dirname'].'\\plugin');
	
	//框架主目录
	define('PATH_LIB_FRAMEWORK',$dirname);
	
	//框架配置文件目录
	define('PATH_LIB_CONFIG',PATH_LIB_FRAMEWORK.'\\config');
	
	//框架类库文件目录
	define('PATH_LIB_CORE',PATH_LIB_FRAMEWORK.'\\core');
	
	//公共文件目录
	define('PATH_PUB_ROOT','http://'.$_SERVER["HTTP_HOST"].'/static');
	
	
	//echo PATH_PUB_ROOT;exit;
	
	
	// ----- 框架全局文件 -----
	//框架全局配置文件
	require_once(PATH_LIB_CONFIG .'\\core.cfg.php');
	
	//框架全局函数文件
	require_once(PATH_LIB_CORE.'\\core.func.php');
	
	//框架入口基类文件
	require_once(PATH_LIB_CORE.'\\core.class.php');
	
	
	
?>