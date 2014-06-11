<?php (!defined('_APP') ? exit('Access Denied!') : ''); ?>
<!DOCTYPE html>
<html>
<head>
    <title>Porter get-view</title>
    <?php echo file_get_contents(dirname(__FILE__) . '/common/head.tpl.php'); ?>
</head>
<body>
<div class="container">
    <div class="row clearfix">
        <div class="col-md-12 column">
            <h3 class="text-left">Porter <a href="get.php">get-view</a></h3>
            <div class="form-group">
                <label for="text">your text is:</label>
                <textarea name="text" class="form-control" readonly><?=$data['text']; ?></textarea>
            </div>
            <h6 class="text-left"><a href="post.php">Goto post-form</a></h6>
        </div>
    </div>
</div>
</body>
</html>

