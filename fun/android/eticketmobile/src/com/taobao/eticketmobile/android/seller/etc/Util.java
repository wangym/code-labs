/**
 * 
 */
package com.taobao.eticketmobile.android.seller.etc;

import me.yumin.android.common.etc.CommonUtil;

/**
 * @author yumin
 * 
 */
public class Util {

	/**
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile4(String mobile) {

		boolean result = false;

		if (CommonUtil.isNotEmpty(mobile) && CommonUtil.isIntegerStr(mobile) && 4 == mobile.length()) {
			result = true;
		}

		return result;
	}
}
