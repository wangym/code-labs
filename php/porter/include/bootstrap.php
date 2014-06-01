<?php

// bootstrap.php

/* set */
//error_reporting(0);
date_default_timezone_set('Asia/Shanghai');

/* variable */
$_dirname = dirname(__FILE__);
$_pathinfo = pathinfo($_dirname);

/* constant */
define('_APP', 'APP');
define('_DATETIME', date('Y-m-d H:i:s', $_SERVER['REQUEST_TIME']));
define('_DEBUG', true);
define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
define('_FILENAME', basename($_SERVER['SCRIPT_NAME']));
define('_SECRET_KEY', 'my_app_19831028*#');
define('_TIME', $_SERVER['REQUEST_TIME']);

/* require */
require($_SERVER['DOCUMENT_ROOT'].'include/business.php');
require($_SERVER['DOCUMENT_ROOT'].'include/config.php');
require($_SERVER['DOCUMENT_ROOT'].'include/util.php');

