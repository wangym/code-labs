<?php

// post.tpl.php

(!defined('_APP') ? exit('Access Denied!') : '');

$html = <<<EOF
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
            <form role="form" action="?action=set">
                <div class="form-group">
                    <label for="text">Text</label>
                    <textarea id="text" class="form-control" maxlength="200"></textarea>
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
                <input id="userId" type="hidden" value="0" ></input>
            </form>
            <label for="text"></label>
        </div>
    </div>
</div>
</body>
</html>
EOF;

return $html;

