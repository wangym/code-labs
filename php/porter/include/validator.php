<?php

// validator.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 * @return bool $result
 */
function validate_web_post() {

    $result = false;

    $time = http_receive('time');
    $token = http_receive('token');
    $text = http_receive('text');

    if (is_numeric($time) && !empty($token) && !empty($text)) {
        $result = true;
    }

    return $result;
}

