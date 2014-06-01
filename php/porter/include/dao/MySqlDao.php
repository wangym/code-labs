<?php

// MySqlDao.php

(!defined('_APP') ? exit('Access Denied!') : '');

/**
 *
 */
interface IMySqlDao {

    /**
     * 建立连接
     *
     * @param array $database
     */
    public function connection($database);

    /**
     * 获取全部
     *
     * @param object $result
     * @return array $array
     */
    public function fetchAll($result);
}

/**
 *
 */
(!class_exists('mysqli') ? exit('Fatal error: Class mysqli not found!') : '');
class MySqliDao extends mysqli implements IMySqlDao {

	/**
	 *
	 */
	public function __construct() {

        global $_database;
        $this->connection($_database);
        unset($_database);
	}

	/**
	 *
	 */
	public function __destruct() {

		$this->close();
	}

    public function connection($database) {

        parent::__construct(
            $database[_ENV]['host'],
            $database[_ENV]['username'],
            $database[_ENV]['password'],
            $database[_ENV]['database']
        );
        (mysqli_connect_errno() ? exit('Connect failed:' . mysqli_connect_error()) : '');
        $this->query("set names $database[_ENV]['charset']");
    }

    public function fetchAll($result) {

		$array = array();

		$i = 0;
		while($row = $result->fetch_assoc()) {
			$array[$i] = $row;
			$i++;
		}

		return $array;
	}
}

