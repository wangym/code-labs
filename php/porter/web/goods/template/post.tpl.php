<?php (!defined('_APP') ? exit('Access Denied!') : ''); ?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Porter post-form</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Porter">
    <meta name="author" content="yumin">
    <!--link rel="stylesheet/less" href="../less/bootstrap.less" type="text/css" /-->
    <!--link rel="stylesheet/less" href="../less/responsive.less" type="text/css" /-->
    <!--script src="../js/less-1.3.3.min.js"></script-->
    <!--append ‘#!watch’ to the browser URL, then refresh the page. -->
    <link href="../asset/css/bootstrap.min.css" rel="stylesheet">
    <link href="../asset/css/style.css" rel="stylesheet">
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="../asset/js/html5shiv.js"></script>
    <![endif]-->
    <!-- Fav and touch icons -->
    <script type="text/javascript" src="../asset/js/jquery.min.js"></script>
    <script type="text/javascript" src="../asset/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../asset/js/scripts.js"></script>
</head>
<body>
<div class="container">
    <div class="row clearfix">
        <div class="col-md-12 column">
            <h3 class="text-left">Porter post-form</h3>
            <form role="form" action="post.php" method="post">
                <div class="form-group">
                    <label for="text">Text</label>
                    <textarea name="text" class="form-control" maxlength="200"></textarea>
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
                <input name="action" type="hidden" value="post"/>
                <input name="time" type="hidden" value="<?=$data['time']; ?>"/>
                <input name="token" type="hidden" value="<?=$data['token']; ?>"/>
            </form>
            <label for="text"><?=$data['response']; ?></label>
        </div>
    </div>
</div>
</body>
</html>

