/**
 * 
 */
package android.wulongdao.etc;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import android.wulongdao.thirdparty.fastmd5.MD5;

/**
 * @author yumin
 * 
 */
public class CommonUtil {

	/**
	 * 
	 */
	private CommonUtil() {

	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static boolean createFolders(String path) {

		boolean result = false;

		if (isNotEmpty(path)) {
			File file = new File(path);
			if (null != file && !file.exists()) {
				result = file.mkdirs();
			}
		}

		return result;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {

		boolean result = false;

		if (isNotEmpty(path)) {
			File file = new File(path);
			if (null != file && file.exists()) {
				result = file.delete();
			}
		}

		return result;
	}

	/**
	 * 
	 * @param map
	 * @param key
	 * @return
	 */
	public static Object getMapValue(HashMap<String, Object> map, String key) {

		Object object = null;

		if (isNotEmpty(map) && isNotEmpty(key) && map.containsKey(key)) {
			object = map.get(key);
		}

		return object;
	}

	/**
	 * 
	 * @return
	 */
	public static long getTimestamp() {

		return new Date().getTime() / 1000;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String MD5(String key) {

		return new MD5(key).asHex();
	}

	/**
	 * 
	 * @param string
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean isBetweenLength(String string, int begin, int end) {

		boolean result = false;

		if (isNotEmpty(string)) {
			int length = string.length();
			if (begin <= length && length <= end) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {

		boolean result = false;

		if (isNotEmpty(email)) {
			Matcher asciiMatcher = Pattern.compile("^\\p{ASCII}+$").matcher(email);
			Matcher emailMatcher = Pattern.compile("^\\s*?(.+)@(.+?)\\s*$").matcher(email);
			if (!email.endsWith(".") && asciiMatcher.matches() && emailMatcher.matches()) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty(Map map) {

		boolean result = false;

		if (null != map && 0 < map.size()) {
			result = true;
		}

		return result;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNotEmpty(String string) {

		boolean result = false;

		if (null != string && 0 < string.length() && !"null".equalsIgnoreCase(string)) {
			result = true;
		}

		return result;
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isNotNull(Object object) {

		boolean result = false;

		if (null != object) {
			result = true;
		}

		return result;
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static JSONObject toJSONObject(String json) {

		JSONObject object = null;

		if (isNotEmpty(json)) {
			if (json.startsWith("{") && json.endsWith("}")) {
				try {
					object = new JSONObject(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return object;
	}

}
