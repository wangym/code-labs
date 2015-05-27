/**
 * 
 */
package me.yumin.android.bodyshow.etc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.Build;

/**
 * @author yumin
 * 
 */
@SuppressWarnings("rawtypes")
public class Util {

	/**
	 * 
	 * @return
	 */
	public static long getCurrentTimestamp() {

		long time = new Date().getTime() / 1000;

		return time;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumericString(String str) {

		if (null != str && 0 < str.length()) {
			for (int i = str.length(); --i >= 0;) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 
	 * @param length
	 * @return
	 */
	public static String random(int length) {

		StringBuffer buffer = new StringBuffer();
		StringBuffer dict = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Random random = new Random();
		int range = dict.length();
		String result = "";

		for (int i = 0; i < length; i++) {
			buffer.append(dict.charAt(random.nextInt(range)));
		}
		result = buffer.toString();

		return result;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static final int toInt(String str) {

		int result = 0;

		if (null != str && !"".equals(str.trim())) {
			try {
				result = Integer.parseInt(str);
			} catch (Exception e) {
				result = 0;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {

		boolean result = false;

		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(mobile);
		result = matcher.matches();

		return result;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotNullAndEmpty(String str) {

		boolean result = false;

		if (null != str && 0 < str.length()) {
			result = true;
		}

		return result;
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNotNull(Object obj) {

		boolean result = false;

		if (null != obj) {
			result = true;
		}

		return result;
	}

	/**
	 * 是否非空Map
	 * 
	 * @param map
	 * @return true非空|false为空
	 */
	public static boolean isNotNullAndEmpty(Map map) {

		boolean result = false;

		if (null != map && 0 < map.size()) {
			result = true;
		}

		return result;
	}

	/**
	 * 
	 * @param env
	 * @param name
	 * @return
	 */
	public static String getAPIUrl(String env, String name) {

		String url = null;

		if (null != env) {

			Map<String, String> map = null;
			if ("dev".equalsIgnoreCase(env)) {
				map = Constant.API_DEV;
			} else if ("prod".equalsIgnoreCase(env)) {
				map = Constant.API_PROD;
			}

			if (null != map && 0 < map.size()) {
				url = map.get(name);
			}
		}

		return url;
	}

	/**
	 * 
	 * @param content
	 * @return
	 */
	public static String getSign(String content) {

		String sign = null;

		sign = MD5.hash(Constant.SIGN_PREFIX + content + Constant.SIGN_SUFFIX);

		return sign;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public static String httpPost(String url, Map<String, String> map) {

		String result = null;

		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				params.add(new BasicNameValuePair(key, value));
			}
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(post);
			if (200 == response.getStatusLine().getStatusCode()) {
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public static String getLoginLog() {

		String json = null;

		Map<String, String> map = new HashMap<String, String>();
		map.put("os", Build.VERSION.RELEASE);
		map.put("model", Build.MODEL);
		JSONObject jsonObject = new JSONObject(map);
		json = jsonObject.toString();

		return json;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public static String getLoginSign(Map<String, String> map) {

		String sign = null;

		String mobile = (map.containsKey(Constant.KEY_LOGIN_MOBILE) ? map.get(Constant.KEY_LOGIN_MOBILE) : null);
		String password = (map.containsKey(Constant.KEY_LOGIN_PASSWORD) ? map.get(Constant.KEY_LOGIN_PASSWORD) : null);
		if (null != mobile && null != password) {
			sign = Util.getSign(mobile + password);
		}

		return sign;
	}
}
