/**
 * 
 */
package cn.androidcloud.tao.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liangcha
 * 
 */
public class ConstantsUtil {

	/**
	 * 
	 */
	public final static String PROJECT_PATH = "/data/data/cn.androidcloud.tao.activity/";

	/**
	 * TOP
	 */
	public final static String APP_KEY = "12009979";
	public final static String APP_SERCET = "1bf294e7a5b475f15a5f2d926b2d4c73";
	public final static String SANDBOX_URL = "http://gw.api.tbsandbox.com/router/rest";
	// http://gw.api.tbsandbox.com/router/rest
	// http://gw.api.taobao.com/router/rest
	public final static String GET_SESSION_URL = "http://container.api.tbsandbox.com/container";
	// http://container.api.tbsandbox.com/container
	// http://container.open.taobao.com/container

	/**
	 * 
	 */
	public static final String TITLE_MY_SOLD = "已卖出的宝贝";

	public static final String LABLE_TID = "订单编号:";
	public static final String LABLE_CREATED = "成交时间:";
	public static final String LABLE_PAYMENT = "实际收款:";
	public static final String LABLE_BUYER_NICK = "买家昵称:";

	public static final String TXT_ALL = "全部";
	public static final String TXT_UNKOWN = "未知";
	public static final String TXT_SUFFIX_ORDER_TITLE = "...等多件";

	public static final String UNIT_ITEM = "件";
	public static final String UNIT_RMB = "元";

	/**
	 * 交易状态
	 */
	public static Map<String, String> tradeStatusMap = new HashMap<String, String>();

	public static final String TRADE_NO_CREATE_PAY = "TRADE_NO_CREATE_PAY";
	public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
	public static final String WAIT_SELLER_SEND_GOODS = "WAIT_SELLER_SEND_GOODS";
	public static final String WAIT_BUYER_CONFIRM_GOODS = "WAIT_BUYER_CONFIRM_GOODS";
	public static final String TRADE_BUYER_SIGNED = "TRADE_BUYER_SIGNED";
	public static final String TRADE_FINISHED = "TRADE_FINISHED";
	public static final String TRADE_CLOSED = "TRADE_CLOSED";
	public static final String TRADE_CLOSED_BY_TAOBAO = "TRADE_CLOSED_BY_TAOBAO";
	public static final String ALL_WAIT_PAY = "ALL_WAIT_PAY";
	public static final String ALL_CLOSED = "ALL_CLOSED";

	static {
		tradeStatusMap.put(TRADE_NO_CREATE_PAY, "未创建支付宝交易");
		tradeStatusMap.put(WAIT_BUYER_PAY, "等待买家付款");
		tradeStatusMap.put(WAIT_SELLER_SEND_GOODS, "买家已付款");
		tradeStatusMap.put(WAIT_BUYER_CONFIRM_GOODS, "卖家已发货");
		tradeStatusMap.put(TRADE_BUYER_SIGNED, "买家已签收");
		tradeStatusMap.put(TRADE_FINISHED, "交易成功");
		tradeStatusMap.put(TRADE_CLOSED, "交易关闭");
		tradeStatusMap.put(TRADE_CLOSED_BY_TAOBAO, "交易被淘宝网关闭");
		tradeStatusMap.put(ALL_WAIT_PAY, "等待买家付款");
		tradeStatusMap.put(ALL_CLOSED, "交易关闭");
	}
}
