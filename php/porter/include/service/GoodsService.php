<?php

// GoodsService.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IGoodsService {

    /**
     * @param array $params 原始参数举例:json={"userId":"1","text":"yumin"}
     * @return object $resultPojo
     */
    public function postText($params);

    /**
     * @param array $params 原始参数举例:json={"userId":"1"}
     * @return object $resultPojo
     */
    public function getText($params);
}

/**
 *
 */
class GoodsService implements IGoodsService {

    /**
     * @var PorterNoSqlDao
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
        $this->dao = new PorterNoSqlDao();
    }

    /**
     *
     */
    public function __destruct() {
        unset($this->dao);
    }

    public function postText($params) {

        $resultPojo = new ResultPojo();
        $resultPojo->status = _STATUS_PARAMETER_ERROR;
        $data = new KvPojo();

        if (!empty($params) && is_array($params)) {
            $key = self::getCacheKey($params);
            $value = get_array_value('text', $params);
            if (!empty($key) && !empty($value)) {
                $resultPojo->status = ($this->dao->set($key, $value, self::TTL) ? _STATUS_OK : _STATUS_POST_ERROR);
                $data->key = $key;
                $data->value = $value;
            }
        }
        $resultPojo->data = $data;

        return $resultPojo;
    }

    public function getText($params) {

        $resultPojo = new ResultPojo();
        $resultPojo->status = _STATUS_PARAMETER_ERROR;
        $data = new KvPojo();

        if (!empty($params) && is_array($params)) {
            $key = self::getCacheKey($params);
            if (!empty($key)) {
                $value = $this->dao->get($key);
                $resultPojo->status = (!empty($value) ? _STATUS_OK : _STATUS_GET_ERROR);
                $data->key = $key;
                $data->value = $value;
            }
        }
        $resultPojo->data = $data;

        return $resultPojo;
    }

    /**
     * @param array $params
     * @return string $key
     */
    private function getCacheKey($params) {

        $key = '';

        $userId = get_array_value('userId', $params);
        if (!empty($userId)) {
            $key = 'text-user-' . $userId;
        }

        return $key;
    }
}

