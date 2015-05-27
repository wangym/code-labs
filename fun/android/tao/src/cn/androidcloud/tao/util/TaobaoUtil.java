/**
 * 
 */
package cn.androidcloud.tao.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import com.taobao.api.TaobaoApiException;
import com.taobao.api.TaobaoJsonRestClient;

/**
 * 
 */
public class TaobaoUtil {

	/**
	 * @throws TaobaoApiException
	 * 
	 */
	public static TaobaoJsonRestClient getJsonRestClient()
			throws TaobaoApiException {

		return new TaobaoJsonRestClient(ConstantsUtil.SANDBOX_URL,
				ConstantsUtil.APP_KEY, ConstantsUtil.APP_SERCET);
	}

	/**
	 * 二进制转字符串
	 */
	private static String byte2hex(byte[] b) {

		StringBuffer buffer = new StringBuffer();
		String tmp = "";

		for (int n = 0; n < b.length; n++) {

			tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

			if (1 == tmp.length()) {

				buffer.append("0").append(tmp);

			} else {

				buffer.append(tmp);

			}
		}

		return buffer.toString().toUpperCase();
	}

	/**
	 * 用于生成签名方法
	 */
	public static String sign(TreeMap<String, String> params, String secret) {

		String result = null;

		if (null == params) {
			return result;
		}

		Iterator<String> iter = params.keySet().iterator();
		StringBuffer orgin = new StringBuffer(secret);
		while (iter.hasNext()) {

			String name = (String) iter.next();
			orgin.append(name).append(params.get(name));
		}

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));

		} catch (Exception ex) {

			throw new java.lang.RuntimeException("sign error !");

		}

		return result;
	}

	/**
	 * 请求并且返回内容
	 */
	public static String getResult(String requestUrl, String content) {

		// System.out.println("=== requestUrl:" + requestUrl);
		// System.out.println("=== content:" + content);

		URL url = null;
		HttpURLConnection connection = null;

		try {

			url = new URL(requestUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.connect();

			DataOutputStream out = new DataOutputStream(connection
					.getOutputStream());
			out.writeBytes(content);
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

			reader.close();
			return buffer.toString();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (null != connection) {
				connection.disconnect();
			}
		}

		return null;
	}

	/**
	 * 请且并返回会话值
	 */
	public static String getSessionKey(String authCode) {

		String sessionKey = null;

		if (null != authCode && 0 < authCode.length()) {

			String returnParam = getResult(ConstantsUtil.GET_SESSION_URL,
					"authcode=" + authCode);
			String[] params = returnParam.split("&");

			for (String string : params) {

				if (string.startsWith("top_session")) {

					sessionKey = string.split("=")[1];
					break;
				}
			}
		}

		// System.out.println("=== sessionKey:" + sessionKey);
		return sessionKey;
	}

	/**
	 * 组装API请求参数
	 */
	public static String createRequestParam(
			TreeMap<String, String> apiParamsMap, String sessionKey) {

		// API系统参数
		if (null != sessionKey && 0 < sessionKey.length()) {
			apiParamsMap.put("session", sessionKey);
		}
		apiParamsMap.put("timestamp", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date()));
		apiParamsMap.put("format", "json");
		apiParamsMap.put("app_key", ConstantsUtil.APP_KEY);
		apiParamsMap.put("v", "1.0");
		apiParamsMap.put("sign", sign(apiParamsMap, ConstantsUtil.APP_SERCET));

		StringBuilder param = new StringBuilder();
		for (Iterator<Map.Entry<String, String>> it = apiParamsMap.entrySet()
				.iterator(); it.hasNext();) {

			Map.Entry<String, String> e = it.next();
			param.append("&").append(e.getKey()).append("=").append(
					e.getValue());
		}

		return param.toString().substring(1);
	}
}
