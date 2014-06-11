<?php (!defined('_APP') ? exit('Access Denied!') : ''); ?>
<!DOCTYPE html>
<html>
<head>
    <title>Porter post-form</title>
    <?php echo file_get_contents(dirname(__FILE__) . '/common/head.tpl.php'); ?>
</head>
<body>
<div class="container">
    <div class="row clearfix">
        <div class="col-md-12 column">
            <h3 class="text-left">Porter <a href="post.php">post-form</a></h3>
            <form role="form" action="post.php" method="post">
                <div class="form-group">
                    <label for="text">please input text:</label>
                    <textarea name="text" class="form-control" maxlength="200"></textarea>
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
                <input name="action" type="hidden" value="post"/>
                <input name="time" type="hidden" value="<?=$data['time']; ?>"/>
                <input name="token" type="hidden" value="<?=$data['token']; ?>"/>
            </form>
            <h6 class="text-left"><a href="get.php">Goto get-view</a></h6>
            <label for="text"><?=$data['response']; ?></label>
        </div>
    </div>
</div>
</body>
</html>

