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
	
	define('ROUTE_MODE',2); //1:��ͨ��ʽGENERAL 2:α��̬PSEUDO 3:��дREWIRTE
	define('ROUTE_CLASS','class');
	define('ROUTE_METHOD','method');
	
	define('EXT_SELF','.php'); //echo pathinfo(__FILE__, PATHINFO_EXTENSION);exit;
	(2 == ROUTE_MODE ? define('EXT_URI','.') : ''); //α��չ�� ͨ����:'.' �����:'.html'
	
	define('VIEW_NAME','smarty_ext'); //ģ����������
	define('VIEW_TPLEXT','.tpl'); //ģ���ļ���չ��
	
	define('DAO_NAME','mysqli_ext'); //���ݷ�������
	
	define('ROUTE_NAME','route'); //route��������
	
	define('REQUEST_TIME',$_SERVER['REQUEST_TIME']);
	define('REQUEST_URI',$_SERVER['REQUEST_URI']);
	define('QUERY_STRING',$_SERVER['QUERY_STRING']);
	
	define('DEFAULT_CLASS','base'); //Ĭ����
	define('DEFAULT_METHOD','index'); //Ĭ�Ϸ���
	define('DEFAULT_PAGE','index.php/'); //Ĭ������ҳ
	
	define('CRYPT_KEY','powered_by_zjuhz'); //��Կ
	
	// �����������ʱ�� formkey_timeout
	// 1.�޴˴���������������������޷��ύ
	// 2.����ʱ���Ͽ���:SESSION��ʱʱ�����û�����
	// 3.��λ:�� ��:60��x60��x24Сʱ = 86400��/��
	define('FORMKEY_TIMEOUT','600');
	
	
	
?>