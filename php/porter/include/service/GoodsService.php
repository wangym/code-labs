<?php

// GoodsService.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IGoodsService {

    /**
     * @param array $params
     * @return int $status
     */
    public function postText($params);

    /**
     * @param array $params
     * @return string $text
     */
    public function getText($params);
}

/**
 *
 */
class GoodsService implements IGoodsService {

	/**
	 *
	 */
	private $dao;

    /**
     *
     */
    const TTL = 604800;

	/**
	 *
	 */
	public function __construct() {

		$this->dao = new PorterDao();
	}

	/**
	 *
	 */
	public function __destruct() {

		unset($this->dao);
	}

    public function postText($params) {

        $status = _STATUS_ERROR;

        if (!empty($params) && !is_array($params)) {
            $userId = get_array_value('userId', $params);
            $text = get_array_value('text', $params);
            if (!empty($userId) && !empty($text)) {
                if ($this->dao->set("user-$userId-", $text, TTL)) {
                    $status = _STATUS_OK;
                } else {
                    $status = _STATUS_GOODS_POST_ERROR;
                }
            }
        }

        return $status;
    }

    public function getText($params) {

        $text = '';

        if (!empty($params) && !is_array($params)) {
            $userId = get_array_value('userId', $params);
            if (!empty($userId)) {
                $text = $this->dao->get("user-$userId-");
            }
        }

        return $text;
    }
}

