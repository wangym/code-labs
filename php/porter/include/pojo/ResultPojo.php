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

