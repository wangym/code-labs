/**
 * 
 */
package me.shuotao.etc;

import java.util.Date;

/**
 * @author yumin
 * 
 */
public class Util {

	/**
	 * 
	 * @return
	 */
	public static long getUnixTimestamp() {

		long time = new Date().getTime() / 1000;

		return time;
	}
}
