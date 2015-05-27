/**
 * 
 */
package me.yumin.android.yaya.etc;

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
	public static boolean logged = true; // TODO

	/**
	 * 
	 */
	public static void dump() {

		dumpET();
		Log.v(TAG, String.format("logged=%s", logged));
	}
}
