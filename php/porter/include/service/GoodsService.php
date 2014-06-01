<?php

// GoodsService.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IGoodsService {

    /**
     * @param array $params
     * @return boolean $result
     */
    public function handlingText($params);
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

		$this->dao = new GoodsDao();
	}

	/**
	 *
	 */
	public function __destruct() {

		unset($this->dao);
	}

	public function handlingText($params) {

        $result = false;

        if (!empty($params) && !is_array($params)) {
            $text = get_array_value('text', $params);
            if (!empty($text)) {
            }
        }

        return $result;
    }

    /**
	 * @param string $name
	 * @return mixed $value
	 */
	public function __get($name) {

		return $this->$name;
	}

	/**
	 * @param string $name
	 * @param mixed $value
	 */
	public function __set($name, $value) {

		$this->$name = $value;
	}
}

