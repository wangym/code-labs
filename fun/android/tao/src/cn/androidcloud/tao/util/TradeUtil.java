/**
 * 
 */
package cn.androidcloud.tao.util;

/**
 * @author liangcha
 * 
 */
public class TradeUtil {

	/**
	 * 
	 * @param status
	 * @return
	 */
	public static String getNameByStatus(String status) {

		if (!CommonUtil.isNotEmpty(status)) {

			return ConstantsUtil.TXT_ALL;
		}

		String name = ConstantsUtil.tradeStatusMap.get(status);

		if (CommonUtil.isNotEmpty(name)) {

			name = ConstantsUtil.TXT_UNKOWN;
		}

		return name;
	}
}
