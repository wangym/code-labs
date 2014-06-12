<?php

// ResultPojo.php

/**
 *
 */
class KvPojo {

    /**
     * @var
     */
    private $key;
    private $value;

    /**
     * @result array $array
     */
    public function toArray() {

        $array = array();

        if (!empty($this->key) && !empty($this->value)) {
            $array = array(
                'key' => $this->key,
                'value' => $this->value
            );
        }

        return $array;
    }

    public function __get($name) {
        return $this->$name;
    }

    public function __set($name, $value) {
        $this->$name = $value;
    }
}

/**
 *
 */
class ResultPojo {

    /**
     * @var
     */
    private $status;
    private $data; // such as KvPojo

    public function __get($name) {
        return $this->$name;
    }

    public function __set($name, $value) {
        $this->$name = $value;
    }
}

// =========================
// 以下是配套函数:
// =========================

/**
 * @param object $pojo (@see ResultPojo.php)
 * @param string $trace
 * @return string $json
 */
function get_pojo_json($pojo, $trace) {

    $json = get_response_json(_STATUS_ERROR, __METHOD__);

    if (is_pojo_true($pojo)) {
        $json = get_response_json($pojo->status, $trace, $pojo->data->toArray());
        unset($pojo);
    }

    return $json;
}

/**
 * @param object $pojo (@see ResultPojo.php)
 * @return bool $result
 */
function is_pojo_true($pojo) {

    $result = false;

    if (!empty($pojo) && $pojo instanceof ResultPojo && $pojo->data instanceof KvPojo) {
        $result = true;
    }

    return $result;
}

