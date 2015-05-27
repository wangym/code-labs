/**
 * 
 */
package android.wulongdao.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.wulongdao.thirdparty.mime.MultipartEntity;
import android.wulongdao.thirdparty.mime.content.FileBody;

/**
 * @author yumin
 * 
 */
public class HTTPUtil {

	/**
	 * 
	 */
	private HTTPUtil() {

	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String url) throws IOException {

		HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);
		httpConnection.setUseCaches(false);
		httpConnection.setReadTimeout(20000);
		httpConnection.setConnectTimeout(10000);
		httpConnection.setRequestMethod("POST");
		httpConnection.setRequestProperty("Connection", "Keep-Alive");
		httpConnection.setRequestProperty("Charset", "UTF-8");
		// connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		return httpConnection;
	}

	/**
	 * 
	 * @param url 必传,请求地址
	 * @param parameters 可选,附带参数
	 * @return
	 */
	public static String postParameters(String url, Map<String, String> parameters) {

		String result = null;

		String queryString = parametersToQueryString(parameters);
		byte[] buffer = (CommonUtil.isNotEmpty(queryString) ? queryString.getBytes() : null);

		HttpURLConnection httpConnection = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			httpConnection = getHttpURLConnection(url);
			outputStream = httpConnection.getOutputStream();
			outputStream.write(buffer);
			inputStream = httpConnection.getInputStream();
			result = inputStreamToString(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != outputStream) {
					outputStream.close();
					outputStream = null;
				}
				if (null != inputStream) {
					inputStream.close();
					inputStream = null;
				}
				if (null != httpConnection) {
					httpConnection.disconnect();
					httpConnection = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 
	 * @param url 必传,请求地址
	 * @param key 必传,文件键名
	 * @param body 必传,文件内容
	 * @param parameters 可选,附带参数
	 * @return
	 */
	public static String postFile(String url, String key, File body) {

		String result = null;

		if (CommonUtil.isNotEmpty(url) && CommonUtil.isNotEmpty(key) && null != body && body.exists()) {
			try {
				MultipartEntity entity = new MultipartEntity();
				entity.addPart(key, new FileBody(body));
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(entity);
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
				if (null != httpResponse && 200 == httpResponse.getStatusLine().getStatusCode()) {
					result = EntityUtils.toString(httpResponse.getEntity());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private static String inputStreamToString(InputStream inputStream) throws IOException {

		String string = null;

		if (null != inputStream) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			string = bufferedReader.readLine();
			// String line = bufferedReader.readLine();
			// StringBuilder stringBuilder = new StringBuilder();
			// while (null != line) {
			// stringBuilder.append(line);
			// }
			// string = stringBuilder.toString();
		}

		return string;
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private static String parametersToQueryString(Map<String, String> parameters) {

		String queryString = null;

		if (CommonUtil.isNotEmpty(parameters)) {
			StringBuilder stringBuilder = new StringBuilder();
			Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				stringBuilder.append("&").append(key).append("=").append(value);
			}
			queryString = stringBuilder.toString();
		}

		return queryString;
	}

}
