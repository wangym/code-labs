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
	public static final String TITLE_MY_SOLD = "�������ı���";

	public static final String LABLE_TID = "�������:";
	public static final String LABLE_CREATED = "�ɽ�ʱ��:";
	public static final String LABLE_PAYMENT = "ʵ���տ�:";
	public static final String LABLE_BUYER_NICK = "����ǳ�:";

	public static final String TXT_ALL = "ȫ��";
	public static final String TXT_UNKOWN = "δ֪";
	public static final String TXT_SUFFIX_ORDER_TITLE = "...�ȶ��";

	public static final String UNIT_ITEM = "��";
	public static final String UNIT_RMB = "Ԫ";

	/**
	 * ����״̬
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
		tradeStatusMap.put(TRADE_NO_CREATE_PAY, "δ����֧��������");
		tradeStatusMap.put(WAIT_BUYER_PAY, "�ȴ���Ҹ���");
		tradeStatusMap.put(WAIT_SELLER_SEND_GOODS, "����Ѹ���");
		tradeStatusMap.put(WAIT_BUYER_CONFIRM_GOODS, "�����ѷ���");
		tradeStatusMap.put(TRADE_BUYER_SIGNED, "�����ǩ��");
		tradeStatusMap.put(TRADE_FINISHED, "���׳ɹ�");
		tradeStatusMap.put(TRADE_CLOSED, "���׹ر�");
		tradeStatusMap.put(TRADE_CLOSED_BY_TAOBAO, "���ױ��Ա����ر�");
		tradeStatusMap.put(ALL_WAIT_PAY, "�ȴ���Ҹ���");
		tradeStatusMap.put(ALL_CLOSED, "���׹ر�");
	}
}
