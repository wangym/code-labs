<?php

// GoodsService.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IGoodsService {

    /**
     * @param array $params 原始参数举例:json={"userId":"1","text":"yumin"}
     * @return object $result
     */
    public function postText($params);

    /**
     * @param array $params 原始参数举例:json={"userId":"1"}
     * @return object $result
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
    const TTL = 2592000; // 30-days

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

        $result = new GoodsResultPojo();
        $result->status = _STATUS_ERROR;
        $data = new KvPojo();

        if (!empty($params) && is_array($params)) {
            $key = self::getTextKey($params);
            $value = get_array_value('text', $params);
            if (!empty($key) && !empty($value)) {
                $result->status = ($this->dao->set($key, $value, self::TTL) ? _STATUS_OK : _STATUS_POST_ERROR);
                $data->key = $key;
                $data->value = $value;
            }
        }

        $result->data = $data;
        return $result;
    }

    public function getText($params) {

        $result = new GoodsResultPojo();
        $result->status = _STATUS_ERROR;
        $data = new KvPojo();

        if (!empty($params) && is_array($params)) {
            $key = self::getTextKey($params);
            if (!empty($key)) {
                $value = $this->dao->get($key);
                $result->status = (!empty($value) ? _STATUS_OK : _STATUS_GET_ERROR);
                $data->key = $key;
                $data->value = $value;
            }
        }

        $result->data = $data;
        return $result;
    }

    /**
     * @param array $params
     * @return string $key
     */
    private function getTextKey($params) {

        $key = '';

        $userId = get_array_value('userId', $params);
        if (!empty($userId)) {
            $key = "text-user-$userId";
        }

        return $key;
    }
}

