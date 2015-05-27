<?php

/**
 * Zend_Validate_Utf8Length
 *
 * @category wolequ
 * @package util
 * @author WANG Yumin
 */
require_once 'Zend/Validate/Abstract.php';

class Zend_Validate_Utf8Length extends Zend_Validate_Abstract
{
	// constants for defining what currency symbol should be displayed
	const TOO_SHORT = '';
	const TOO_LONG = '';

	/**
     * @var array
     */
	protected $_messageTemplates = array(
		self::TOO_SHORT => "'%value%' is less than %min% characters long",
		self::TOO_LONG => "'%value%' is greater than %max% characters long"
	);

	/**
     * @var array
     */
	protected $_messageVariables = array(
		'min' => '_min',
		'max' => '_max'
	);

	/**
     * Minimum length
     *
     * @var integer
     */
	protected $_min;

	/**
     * Maximum length
     * If null, there is no maximum length
     *
     * @var integer|null
     */
	protected $_max;

	/**
     * Sets validator options
     *
     * @param integer $min
     * @param integer $max
     * @return void
     */
	public function __construct($min=0, $max=null)
	{
		$this->setMin($min);
		$this->setMax($max);
	}

	/**
     * Returns the min option
     *
     * @return integer
     */
	public function getMin()
	{
		return $this->_min;
	}

	/**
     * Sets the min option
     *
     * @param integer $min
     * @return Zend_Validate_StringLength Provides a fluent interface
     */
	public function setMin($min)
	{
		$this->_min = max(0, (integer) $min);
		return $this;
	}

	/**
     * Returns the max option
     *
     * @return integer|null
     */
	public function getMax()
	{
		return $this->_max;
	}

	/**
     * Sets the max option
     *
     * @param integer|null $max
     * @return Zend_Validate_StringLength Provides a fluent interface
     */
	public function setMax($max)
	{
		if (null === $max)
		{
			$this->_max = null;
		}
		else
		{
			$this->_max = (integer) $max;
		}

		return $this;
	}

	/**
     * Defined by Zend_Validate_Interface
     *
     * Returns true if and only if the string length of $value is at least the min option and
     * no greater than the max option (when the max option is not null).
     *
     * @param  string $value
     * @return boolean
     */
	public function isValid($value)
	{
		$valueString = (string) $value;
		$this->_setValue($valueString);
		$length = $this->getLength($valueString); //echo $length;exit;
		if ($length < $this->_min)
		{
			$this->_error(self::TOO_SHORT);
		}

		if (null !== $this->_max && $this->_max < $length)
		{
			$this->_error(self::TOO_LONG);
		}

		if (count($this->_messages))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
     * utf8
     * 
     * @param string $input
     * @return numeric
     */
	public static function getLength($input)
	{
		$i = 0;
		$count = 0;
		$len = strlen($input);

		while ($i < $len)
		{
			$chr = ord($input[$i]);
			$count++;
			$i++;

			if ($i >= $len) {break;}

			if ($chr & 0x80)
			{
				$chr <<= 1;
				while ($chr & 0x80)
				{
					$i++;
					$chr <<= 1;
				}
			}
		}

		return $count;
	}
}
