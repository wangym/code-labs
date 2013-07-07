<?php
session_start();

include_once( 'config.php' );
include_once( 'saetv2.ex.class.php' );

if( isset($_REQUEST['cursor']) ) {
	$cursor = $_REQUEST['cursor'];
} else {
	$cursor = 0;
}

$c = new SaeTClientV2( WB_AKEY , WB_SKEY , $_SESSION['token']['access_token'] );
$ms  = $c->followers_by_id($_SESSION['token']['uid'], $cursor, 200); // done
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>myfans</title>
</head>

<body>

<?php if( is_array( $ms['users'] ) ): ?>
<?php foreach( $ms['users'] as $user ): ?>
<div style="padding:10px;margin:5px;border:1px solid #ccc">
	<?=$user['name'];?>
</div>
<?php endforeach; ?>
<?php endif; ?>
<hr />
<a href="myfans.php?cursor=<?=$ms['previous_cursor'];?>">上一页</a> - <a href="myfans.php?cursor=<?=$ms['next_cursor'];?>">下一页</a>
<hr />
<?=$ms['total_number'];?>
</body>
</html>
