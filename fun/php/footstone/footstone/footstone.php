<?php
	
	
	
	/*
		[footstone] (C)2007-2008 Yumin Wang
		
		$File   : footstone.php $
		$Author : wangym $
	*/
	
	
	((!defined('FOOTSTONE')) ? exit('Access Denied!') : '');
	
	
	// ----- ���Ŀ¼���� -----
	// Ŀ¼����
	$dirname  = dirname(__FILE__);
	$pathinfo = pathinfo($dirname);
	
	//��Ŀ¼
	//define('PATH_ROOT',$_SERVER['DOCUMENT_ROOT']);
	
	//�ֿ���Ŀ¼
	define('PATH_LIB_COMMON',$pathinfo['dirname'].'\\common');
	
	//�����Ŀ¼
	define('PATH_LIB_PLUGIN',$pathinfo['dirname'].'\\plugin');
	
	//�����Ŀ¼
	define('PATH_LIB_FRAMEWORK',$dirname);
	
	//��������ļ�Ŀ¼
	define('PATH_LIB_CONFIG',PATH_LIB_FRAMEWORK.'\\config');
	
	//�������ļ�Ŀ¼
	define('PATH_LIB_CORE',PATH_LIB_FRAMEWORK.'\\core');
	
	//�����ļ�Ŀ¼
	define('PATH_PUB_ROOT','http://'.$_SERVER["HTTP_HOST"].'/static');
	
	
	//echo PATH_PUB_ROOT;exit;
	
	
	// ----- ���ȫ���ļ� -----
	//���ȫ�������ļ�
	require_once(PATH_LIB_CONFIG .'\\core.cfg.php');
	
	//���ȫ�ֺ����ļ�
	require_once(PATH_LIB_CORE.'\\core.func.php');
	
	//�����ڻ����ļ�
	require_once(PATH_LIB_CORE.'\\core.class.php');
	
	
	
?>