/**
 * 
 */
package com.wulongdao.android.etc;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.wulongdao.android.domain.enumtype.APIEnum;
import android.wulongdao.etc.CommonUtil;

/**
 * 
 * @author yumin
 * 
 */
public class Util {

	/**
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getLoadTime(long timestamp) {

		return new SimpleDateFormat("MM-dd HH:mm").format(new Date(1000 * timestamp));
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	public static String getSign(String... values) {

		String sign = null;

		if (null != values && 0 < values.length) {
			StringBuilder stringBuilder = new StringBuilder();
			for (String value : values) {
				stringBuilder.append(value);
			}
			sign = CommonUtil.MD5(stringBuilder.toString());
		}

		return sign;
	}

	/**
	 * 
	 * @param api
	 * @return
	 */
	public static String getURL(APIEnum api) {

		return api.URL();
	}

}
