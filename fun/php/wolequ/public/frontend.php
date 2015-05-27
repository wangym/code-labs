<?php

/**
 * 整站程序初始入口
 *
 * @category wolequ
 * @package public
 * @author WANG Yumin
 */
// set error_reporting level
//error_reporting('ALL');

// set gzip output
//(substr_count($_SERVER['HTTP_ACCEPT_ENCODING'], 'gzip') ? ob_start('ob_gzhandler') : ob_start());

// set time zone
date_default_timezone_set('Asia/Shanghai');

// set include_path
set_include_path('../library/3rd_part/'
.PATH_SEPARATOR.'../library/config/'
.PATH_SEPARATOR.'../library/dao/'
.PATH_SEPARATOR.'../library/service/'
.PATH_SEPARATOR.'../library/util/'
//.PATH_SEPARATOR.get_include_path()
);

// Zend_Loader autoloader callback
require_once('Zend/Loader.php');
Zend_Loader::registerAutoload();

// define constant
define('DOCUMENT_ROOT', $_SERVER['DOCUMENT_ROOT']);
define('REQUEST_TIME', $_SERVER['REQUEST_TIME']);
define('MARGIC_QUOTES_GPC', get_magic_quotes_gpc());

// registry something
Zend_Registry::set('cfgCommon', new Zend_Config_Ini('../library/config/Common.ini'));
Zend_Registry::set('sessVisitor', new Zend_Session_Namespace('visitor'));

// init layout
Zend_Layout::startMvc(array(
	'layoutPath' => '../library/layout/',
	'layout' => 'frontend')
);

// get instance and dispatch
Zend_Controller_Front::getInstance()
	->setDefaultModule('default')
	->setDefaultControllerName('index')
	->setDefaultAction('index')
	->setModuleControllerDirectoryName('controller')
	->addModuleDirectory('../application/module/')
	->setParam('useDefaultControllerAlways', true)
	->throwExceptions(false)
	->dispatch();
