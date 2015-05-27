<?php

/**
 * 数据分页工具类
 *
 * @category wolequ
 * @package util
 * @author WANG Yumin
 */
class Page
{
	/**
     * 页码名称
     */
	const PRE_PAGE = '上一页';
	const NEXT_PAGE = '下一页';
	const FIRST_PAGE = '首页';
	const LAST_PAGE = '尾页';

	/**
     * 数据总记录数 Total RecordSet
     *
     * @var integer
     */
	private $_totalRs = 0;

	/**
     * 每页显示记录数
     *
     * @var integer
     */
	private $_perPage = 20;

	/**
     * 总页数
     *
     * @var integer
     */
	private $_totalPage = 0;

	/**
     * 当前页码
     *
     * @var integer
     */
	private $_nowPage = 0;

	/**
     * url
     *
     * @var string
     */
	private $_url = null;

	/**
     * 下一页码编号
     *
     * @var string
     */
	private $_nextPage = 0;

	/**
     * 上一页码编号
     *
     * @var string
     */
	private $_prePage = 0;

	/**
     * 页码标签
     *
     * @var string
     */
	private $_tagPage = 'page';

	/**
     * 页码左显示记录数
     *
     * @var string
     */
	private $_leftNum = 4;

	/**
     * 每页显示记录数
     *
     * @var string
     */
	private $_rightNum = 5;

	/**
     * 构造方法
     *
     * @param array $args
     * @return void
     */
	public function __construct($args)
	{
		$this->_totalRs = (int)$args['totalRs']; // 获取总数
		$this->_perPage = ($args['perPage'] > 0 ? $args['perPage'] : $this->_perPage); // 每页显示数
		$this->_totalPage = ceil($this->_totalRs / $this->_perPage); // 总共应为多少页
		// 获取当前页 若调用处未传递则本类自行获取 调用处可传递支持url重写后的参数
		$this->_nowPage = ($args['nowPage'] > 0 ? $args['nowPage'] : $_GET[$this->_tagPage]);
		$this->_nowPage = ($this->_nowPage > $this->_totalPage || 0 >= $this->_nowPage ? 1 : $this->_nowPage);
		$this->_url = $this->_setUrl(); // 设定分页链接
		$this->_nextPage = $this->_nowPage + 1; // 下一页码
		$this->_prePage = $this->_nowPage - 1; // 上一页码
	}

	/**
     * 析构方法
     * 
     * @return void
     */
	public function __destruct()
	{
	}

	/**
     * 生成用于SQL的LIMIT
     *
     * @return string
     */
	public function limit()
	{
		$limit = ($this->_nowPage - 1) * $this->_perPage; // 左标值(limit LEFT, RIGHT)

		return "{$limit}, {$this->_perPage}";
	}

	/**
     * 显示完整的分页(含HTML代码)
     *
     * @return string
     */
	public function show()
	{
		$show = '<div class="page" id="page">';

		//*
		$show .= '共<span class="num">'.$this->_totalRs.
			'</span>条数据&nbsp;-&nbsp;分<span class="num">'.
			$this->_totalPage.'</span>页显示&nbsp;&nbsp;'; //*/

		$show .= ($this->_perPage < $this->_totalRs ?
		$this->_preBar().$this->_mainBar().$this->_nextBar().$this->_jumpBar() : '');

		$show .= '</div>';

		return $show;
	}

	/**
     * 显示上一页(若有的话)和首页
     *
     * @return string
     */
	private function _preBar()
	{
		$preBar = (($this->_leftNum + 1) < $this->_nowPage ?
			"<span><a class='text' href='{$this->_url}1'>".self::FIRST_PAGE.'</a></span>' : '');

		$preBar .= (1 != $this->_nowPage ?
			"<span><a class='text' href='{$this->_url}{$this->_prePage}'>".self::PRE_PAGE.'</a></span>' : '');

		return $preBar;
	}

	/**
     * 显示主分页
     *
     * @return string
     */
	private function _mainBar()
	{
		// ----- 页码循环值 -----
		// 页码显示范围计算
		$start = $this->_nowPage - $this->_leftNum;
		$end = $this->_nowPage + $this->_rightNum;

		for ($i=$start; $i<=$end; $i++)
		{
			if (0 >= $i || $i > $this->_totalPage) { continue; }

			$mainBar .= ($i == $this->_nowPage ?
				"<span class='curr'>{$i}</span>" :
				"<span><a class='num' href='{$this->_url}{$i}'>{$i}</a></span>");
		}

		return $mainBar;
	}

	/**
     * 显示下一页(若有的话)和末页
     *
     * @return string
     */
	private function _nextBar()
	{
		$nextBar = ($this->_nowPage < $this->_totalPage ?
			"<span><a class='text' href='{$this->_url}{$this->_nextPage}'>".self::NEXT_PAGE.'</a></span>' : '');

		$nextBar .= (($this->_totalPage - $this->_nowPage) > $this->_rightNum ?
			"<span><a class='text' href='{$this->_url}{$this->_totalPage}'>".self::LAST_PAGE.'</a></span>' : '');

		return $nextBar;
	}

	/**
     * 显示页码入跳转框(含HTML表单等)
     *
     * @return string
     */
	private function _jumpBar()
	{
	}

	/**
     * 分页链接url
     *
     * @return void
     */
	private function _setUrl()
	{
		// 判断原始Url是否存有参数
		if(empty($_SERVER['QUERY_STRING']))
		{
			// 若原始Url中不存有参数
			$setUrl = $_SERVER['REQUEST_URI'] . "?{$this->_tagPage}="; // 附上分页参数
		}
		else
		{
			// 若原始Url中已存有参数
			if(stristr($_SERVER['QUERY_STRING'], "{$this->_tagPage}=")) // 是否存有分页本身
			{
				// 将当前页码链接去除
				$setUrl = strtr($_SERVER['REQUEST_URI'], array("{$this->_tagPage}={$this->_nowPage}" => ''));

				// 通过判断末位字符选择附加起始字符是''或'&'
				$setUrl .= ('?' == $setUrl[strlen($setUrl)-1] || '&' == $setUrl[strlen($setUrl)-1] ?
					"{$this->_tagPage}=" : "&{$this->_tagPage}=");
			}
			else
			{
				// 若原始Url中存有参数且非分页本身则直接无需去除当前页码链接可直接附加
				$setUrl = $_SERVER['REQUEST_URI']."&{$this->_tagPage}=";
			}
		}

		return $setUrl;
	}
}
