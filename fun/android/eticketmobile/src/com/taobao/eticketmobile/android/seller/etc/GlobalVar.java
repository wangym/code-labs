/**
 * 
 */
package com.taobao.eticketmobile.android.seller.etc;

import me.yumin.android.common.etc.CommonGlobalVar;
import android.util.Log;

/**
 * @author yumin
 */
public final class GlobalVar extends CommonGlobalVar {

	/**
	 * 
	 */
	private static final String TAG = GlobalVar.class.getSimpleName();

	// 是否登录
	public static boolean logged = false;
	// taobao mtop sid
	public static String sid = "";
	// taobao sid
	public static String userId = "";

	/**
	 * 
	 */
	public static void dump() {

		dumpET();
		Log.v(TAG, String.format("logged=%s", logged));
		Log.v(TAG, String.format("sid=%s", sid));
		Log.v(TAG, String.format("userId=%s", userId));
	}
}
