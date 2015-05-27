/**
 * 
 */
package com.taobao.eticketmobile.android.seller.etc;

/**
 * @author yumin
 * 
 */
public final class Constant {

	// taobao mtop app_key
	public static final String APP_KEY = "21321654";
	// taobao mtop app_secret
	public static final String APP_SECRET = "689f2a5d66b7ada74f53349369e35e62 ";
	// 延迟毫秒
	public static final long DELAYED = 2000;
	// eticket mtop biz_code
	public static final String ET_BIZ_CODE = "eticket";
	public static final String ET_DES_SECRET = "qwertyuio8765fdstryuhgtr";
	public static final long ET_USER_ID = 854379759L; // 电子凭证问题答疑
	// taobao mtop ttid
	public static final String TTID = "700342@eticket_android1.6_1.0.0";
	// 键名列表
	public static final String K_CONSUME = "consume";
	public static final String K_ET = "ET";
	public static final String K_LOGIN = "login";

	// 公用项
	public static final int EXCEPTION = 00000;
	public static final int NETWORK_UNAVAILABLE = 00001;
	public static final int BITMAP_LOAD_SUCCESS = 00002;
	public static final int BITMAP_LOAD_FAIL = 00003;
	// 欢迎页
	public static final int WELCOME_TO_LOGIN = 10101;
	public static final int WELCOME_TO_MAIN = 10102;
	// 登录页
	public static final int LOGIN_SUCCESS = 10201;
	public static final int LOGIN_FAIL = 10202;
	// 查询页(核销)
	public static final int CONSUME_QUERY_SUCCESS = 10301;
	public static final int CONSUME_QUERY_FAIL = 10302;
	// 核销页(核销)
	public static final int CONSUME_EXECUTE_SUCCESS = 10401;
	public static final int CONSUME_EXECUTE_FAIL = 10402;
}
