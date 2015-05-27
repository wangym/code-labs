<?php

// fckeditor security check
define('ALLOW_IP_FILE', '../../../../../attachment/allow.ip');

// 判断存在且可读
if (is_readable(ALLOW_IP_FILE))
{
	$long = file_get_contents(ALLOW_IP_FILE);
	// 内容非空且应是长整型
	if (!empty($long))
	{
		$longIp = long2ip($long); // 文件中的IP
		$currIp = $_SERVER['REMOTE_ADDR']; // 获取到的IP
		$hour = date('Hi'); // 当前的小时
		// 进行IP的校验
		if ($longIp == $currIp && ('2330' > $hour && $hour > '0030'))
		{
			// 访问权限开启
			define('ALLOW_ACCESS', 'allow');
		}
	}
}

// 统一权限判断
if (defined('ALLOW_ACCESS') && 'allow' === ALLOW_ACCESS)
{
	//echo ALLOW_ACCESS;
}
else
{
	if (function_exists('SendError'))
	{
		// 调用fckeditor函数
		SendError('1', 'Access denied!');
	}

	exit;
}
