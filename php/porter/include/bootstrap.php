<?php

// bootstrap.php

/* set */
// error_reporting(0);
date_default_timezone_set('Asia/Shanghai');

/* constant */
define('_APP', 'APP');
define('_DATE_TIME', date('Y-m-d H:i:s', $_SERVER['REQUEST_TIME']));
define('_DEBUG', (true && isset($_REQUEST['_debug']) ? true : false));
define('_DIR_NAME', dirname(__FILE__));
define('_ENV', ('localhost' === $_SERVER['HTTP_HOST'] ? 'dev' : 'prod'));
define('_FILE_NAME', basename($_SERVER['SCRIPT_NAME']));
define('_SECRET_KEY', 'my_app_19831028*#');
define('_TIME', $_SERVER['REQUEST_TIME']);

/* require */
require(_DIR_NAME . '/business.php');
require(_DIR_NAME . '/config.php');
require(_DIR_NAME . '/util.php');

