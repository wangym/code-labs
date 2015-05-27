/**
 * 
 */
package me.yumin.compressor.test;

import me.yumin.compressor.lzf.Constant;
import me.yumin.compressor.lzf.SpeedLZF;
import me.yumin.compressor.lzf.Util;

/**
 * @author yumin
 * 
 */
@SuppressWarnings("unused")
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int src = 9;
		byte[] bytes = Util.intToBytes(src);
		int integer = Util.bytesToInt(bytes);
		boolean result = (src == integer ? true : false);
		System.out.println(result);
	}
}
