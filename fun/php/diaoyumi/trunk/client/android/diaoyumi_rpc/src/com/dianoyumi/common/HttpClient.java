package com.dianoyumi.common;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.*;

public class HttpClient {
	private static ILogger logger = LoggerFactory.getLogger(HttpClient.class);
	
	public static String get(String url){
		if (logger.isEnabledDebug()) logger.debug("get " + url);
		org.apache.http.impl.client.DefaultHttpClient httpClient = new DefaultHttpClient(); 
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				HttpEntity entity = httpResponse.getEntity();
				String res = EntityUtils.toString(entity);
				if (logger.isEnabledDebug()) logger.debug("response:" + res);
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String postFile(String url, String filePath){
		if (logger.isEnabledDebug()) logger.debug("postFile " + url + "," + filePath);
		org.apache.http.impl.client.DefaultHttpClient httpClient = new DefaultHttpClient(); 
		HttpPost httpPost = new HttpPost(url);
		File file = new File(filePath);
		if (! file.exists()) return null;
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("file", new FileBody(file));
		httpPost.setEntity(reqEntity);
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				HttpEntity entity = httpResponse.getEntity();
				String res = EntityUtils.toString(entity);
				if (logger.isEnabledDebug()) logger.debug("response:" + res);
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {

		String url = "http://localhost/diaoyumi/api/user/auth.php?time=1336320388&sign=ca9aa035a19a09dbe02a69dc0ae5524a&body=";
		String body = "{\"type\":\"email\",\"user\":\"changgb@hotmail.com\",\"password\":\"e2fc714c4727ee9395f324cd2e7f331f\"}";
		url = url + URLEncoder.encode(body,"utf-8");
		
		System.out.println(HttpClient.get(url));
	}
}
