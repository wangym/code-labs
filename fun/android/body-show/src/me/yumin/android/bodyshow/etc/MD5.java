/**
 * 
 */
package me.yumin.android.bodyshow.etc;

import java.security.MessageDigest;

/**
 * @author yumin
 * 
 */
public class MD5 {

	/**
	 * 
	 */
	private static final char[] KEY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String hash(String text) {

		String hash = null;

		try {
			byte[] bytes = text.getBytes();
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] toChapter1Digest = md.digest(bytes);
			hash = new String(encodeHex(toChapter1Digest));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	private static char[] encodeHex(byte[] data) {

		int l = data.length;
		char[] out = new char[l << 1];

		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = KEY[(0xF0 & data[i]) >>> 4];
			out[j++] = KEY[0x0F & data[i]];
		}

		return out;
	}
}
