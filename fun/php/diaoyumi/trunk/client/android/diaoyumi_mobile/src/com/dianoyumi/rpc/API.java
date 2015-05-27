package com.dianoyumi.rpc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dianoyumi.vo.Event;
import com.diaoyumi.android.etc.HttpClient;
import com.diaoyumi.android.etc.Util;


/**
 * 
 * @author changgb
 *
 */

public class API {
	
	
	private static final String FIELD_TIME_NAME = "time";
	private static final String FIELD_BODY_NAME = "body";
	private static final String FIELD_SIGN_NAME = "sign";
	private static final String FIELD_STATUS_NAME = "status";
	
	public static final int RESPONSE_OK = 200;
	public static final int RESPONSE_PARAMETER_ERROR = 410;
	public static final int RESPONSE_UNIQUE_FAIL = 411;
	public static final int RESPONSE_EMAIL_NOT_UNIQUE = 412;
	public static final int RESPONSE_NAME_NOT_UNIQUE = 413;
	public static final int RESPONSE_CHECK_PASSWORD_FAIL = 414;
	public static final int RESPONSE_USER_NOT_AUTH = 415; 	

	private String baseUrl;
	private Long time;
	private String body;
	private String sign;
	
	private String secretKey = "diaoyumi";	
	
	public API(String baseUrl){
		this.baseUrl = baseUrl;
	}
	
	/**
	 * @return
	 */
	private boolean checkSign(Long signTime, String content, String sign){
		String newSign = generateSign(signTime, content);
		return Util.equals(newSign, sign);
	}
	
	
	/**
	 * @return
	 */
	private String generateSign(Long signTime, String content){
		if (signTime > 0 && content != null) return Util.md5(signTime.toString() + content + secretKey);
		return null;
	}
	
	private JSONObject httpGet(String actionUrl, JSONObject jsonBody){
		long time = Util.time();
		String body = jsonBody.toString();
		String sign = generateSign(time, body);
		StringBuffer query = new StringBuffer();
		query.append(FIELD_TIME_NAME + "=" + time);
		query.append("&" + FIELD_SIGN_NAME + "=" + sign);
		try {
			query.append("&" + FIELD_BODY_NAME + "=" + URLEncoder.encode(body,"UTF-8"));
			String response = HttpClient.get(baseUrl + actionUrl + "?" + query.toString());
			if (Util.isNotEmpty(response)){
				JSONObject ret = new JSONObject(Util.jsonClean(response));
				return ret;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private JSONObject httpPostFile(String actionUrl, JSONObject jsonBody, String filePath){
		long time = Util.time();
		String body = jsonBody.toString();
		String sign = generateSign(time, body);
		StringBuffer query = new StringBuffer();
		query.append(FIELD_TIME_NAME + "=" + time);
		query.append("&" + FIELD_SIGN_NAME + "=" + sign);
		try {
			query.append("&" + FIELD_BODY_NAME + "=" + URLEncoder.encode(body,"UTF-8"));
			String response = HttpClient.postFile(baseUrl + actionUrl + "?" + query.toString(), filePath);
			if (Util.isNotEmpty(response)){
				JSONObject ret = new JSONObject(Util.jsonClean(response));
				return ret;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private  boolean uniqueCheck(String field, String value){
		if (Util.isNotEmpty(field) && Util.isNotEmpty(value)){
			try {
				JSONObject req = new JSONObject();
				req.put("field", field);
				req.put("value", value);
				JSONObject res = httpGet("/api/user/unique_check.php", req);
				return (res != null ) && res.getInt("status") == RESPONSE_OK;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean uniqueCheckEmail(String email){
		return uniqueCheck("email", email);
	}
	
	public boolean uniqueCheckName(String name){
		return uniqueCheck("name", name);
	}
	
	public int register(String email, String name, String password, String mobile){
		if (Util.isNotEmpty(email) && Util.isNotEmpty(name) && Util.isNotEmpty(password)){
			JSONObject req = new JSONObject();
			try {
				req.put("email", email);
				req.put("name", name);
				req.put("password", Util.md5(password));
				if (Util.isNotEmpty(mobile)) req.put("mobile", mobile);
				JSONObject res = httpGet("/api/user/register.php", req);
				if ( res != null  && res.getInt("status") == RESPONSE_OK ){
					return res.getJSONObject("body").getInt("user_id"); 
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	private int auth(String type, String user, String password){
		if (Util.isNotEmpty(type) && Util.isNotEmpty(user) && Util.isNotEmpty(password)){
			try {
				JSONObject req = new JSONObject();
				req.put("user", user);
				req.put("type", type);
				req.put("password", Util.md5(password));
				JSONObject res = httpGet("/api/user/auth.php", req);
				if ( res != null  && res.getInt("status") == RESPONSE_OK ){
					return res.getJSONObject("body").getInt("user_id"); 
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public int authByEmail(String email, String password){
		return auth("email", email, password);
	}

	public int authByName(String name, String password){
		return auth("name", name, password);
	}
	
	public boolean changePassword(int userId, String newPassword, String oldPassword){
		if (userId > 0 && Util.isNotEmpty(newPassword) && Util.isNotEmpty(oldPassword)){
			JSONObject req = new JSONObject();
			try {
				req.put("user_id", userId);
				req.put("new_password", newPassword);
				req.put("old_password", oldPassword);
				JSONObject res = httpGet("/api/user/change_password.php", req);
				return (res != null ) && res.getInt("status") == RESPONSE_OK;			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean newEvent(Event event){
		if (
				event.getUserId() > 0 && 
				Util.isNotEmpty(event.getRid()) &&
				Util.isNotEmpty(event.getType()) &&
				event.getEventTime() != null 
			){
			JSONObject req = new JSONObject();
			try {
				req.put("user_id", event.getUserId());
				req.put("rid", event.getRid());
				req.put("type", event.getType());
				req.put("event_time", Util.date2string(event.getEventTime()));
				req.put("status", event.getStatus());
				if (event.getLat() > 0) req.put("lat", event.getLat());
				if (event.getLng() > 0) req.put("lng", event.getLng());
				if (Util.isNotEmpty(event.getPlace())) req.put("place", event.getPlace());
				req.put("is_new_palce", (event.isNewPlace()) ? "Y":"N");
				if (Util.isNotEmpty(event.getCompanion())) req.put("companion", event.getCompanion());
				if (Util.isNotEmpty(event.getPicture())) req.put("picture", event.getPicture());
				if (Util.isNotEmpty(event.getTitle())) req.put("title", event.getTitle());
				if ( event.getPrice() > 0) req.put("price", event.getPrice());
				if (Util.isNotEmpty(event.getDesc())) req.put("desc", event.getDesc());
				if (Util.isNotEmpty(event.getProperties())) req.put("title", event.getProperties());
				if ( event.getStatus() > 0) req.put("status", event.getStatus());
				JSONObject res = httpGet("/api/event/new.php", req);			
				return (res != null ) && res.getInt("status") == RESPONSE_OK;				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean deleteEvent(int userId, String rid){
		if (userId > 0 && Util.isNotEmpty(rid)){
			JSONObject req = new JSONObject();
			try {
				req.put("user_id", userId);
				req.put("rid", rid);
				JSONObject res = httpGet("/api/event/delete.php", req);
				return (res != null ) && res.getInt("status") == RESPONSE_OK;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private ArrayList<Event> searchEvent(Map<String, Object> params, int pos, int limit, String fields, String orderby){
		ArrayList<Event> ret = new ArrayList<Event>();
		if (params != null && ! params.isEmpty())
		{
			try {
				JSONObject req = new JSONObject();
				if (params.containsKey("user_id"))	req.put("user_id", (Integer) params.get("user_id"));
				if (params.containsKey("rid")) req.put("rid", (String)params.get("rid"));
				if (params.containsKey("type")) req.put("type", (String)params.get("type"));
				if (params.containsKey("place")) req.put("place", (String)params.get("place"));
				if (params.containsKey("companion")) req.put("companion", (String)params.get("companion"));
				if (params.containsKey("max_event_time")) req.put("max_event_time", (String)params.get("max_event_time"));
				if (params.containsKey("min_event_time")) req.put("min_event_time", (String)params.get("min_event_time"));
				if (params.containsKey("max_lat")) req.put("max_lat", (Double)params.get("max_lat"));
				if (params.containsKey("min_lat")) req.put("min_lat", (Double)params.get("min_lat"));
				if (params.containsKey("max_lng")) req.put("max_lng", (Double)params.get("max_lng"));
				if (params.containsKey("min_lng")) req.put("min_lng", (Double)params.get("min_lng"));
				if (params.containsKey("is_new_place")) req.put("is_new_place", (String)params.get("is_new_place"));
				
				req.put("_pos", (pos < 0) ? 0 : pos);
				req.put("_limit",(limit <= 0) ? 1 : limit);
				req.put("_fields", (Util.isEmpty(fields)) ? "*" : fields);
				if (Util.isNotEmpty(orderby)) req.put("_orderby", orderby);
				JSONObject res = httpGet("/api/event/search.php", req);
				if (res != null && res.getInt("status") == RESPONSE_OK)
				{
					JSONArray list = res.getJSONArray("body");
					if (list != null)
					{
						for(int i = 0; i < list.length(); i++)
						{
							JSONObject row = list.getJSONObject(i);
							Event event = new Event();
							event.setUserId(row.getInt("user_id"));
							event.setRid(row.getString("rid"));
							if (row.has("type")) event.setType(row.getString("type"));
							if (row.has("event_time")) event.setEventTime(Util.string2date(row.getString("event_time")));
							if (row.has("lat")) event.setLat(row.getDouble("lat"));
							if (row.has("lng")) event.setLng(row.getDouble("lng"));
							if (row.has("place")) event.setPlace(row.getString("place"));
							if (row.has("is_new_place")) event.setNewPlace(row.getString("is_new_place") == "Y");
							if (row.has("companion")) event.setCompanion(row.getString("companion"));
							if (row.has("picture")) event.setPicture(row.getString("picture"));
							if (row.has("title")) event.setTitle(row.getString("title"));
							if (row.has("price")) event.setPrice(row.getDouble("price"));
							if (row.has("desc")) event.setDesc(row.getString("desc"));
							if (row.has("properties")) event.setProperties(row.getString("properties"));
							if (row.has("status")) event.setStatus(row.getInt("status"));
							ret.add(event);
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	
	public ArrayList<String> getNearPalce(Double lng, Double lat){
		ArrayList<String> ret = new ArrayList<String>();
		if (lat > 0 && lng > 0){
			double[] range = Util.getPlaceRange(lng, lat, 500);
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("max_lat", range[Util.MAX_LAT_INDEX]);
			params.put("min_lat", range[Util.MIN_LAT_INDEX]);
			params.put("max_lng", range[Util.MAX_LNG_INDEX]);
			params.put("min_lng", range[Util.MIN_LNG_INDEX]);
			//?
			params.put("is_new_place", "Y");
			ArrayList<Event> list = searchEvent(params, 0, 20, "place", "event_time desc");
			if (list.size() > 0){
				for(int i = 0; i < list.size(); i++)
				{
					if (Util.isNotEmpty(list.get(i).getPlace())) ret.add(list.get(i).getPlace());
				}
			}
		}
		return ret;
	}
	

	public ArrayList<Event> getAllByUserId(int userId)
	{
		ArrayList<Event> ret = new ArrayList<Event>();
		if (userId > 0){
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("user_id", userId);
			return searchEvent(params, 0, 1000, "*", "event_time desc");
		}
		return ret;
	}
	
	public boolean uploadPicture(int userId,String fileName, String pictureFilePath)
	{
		if (userId > 0 && Util.isNotEmpty(fileName)	&& Util.isNotEmpty(pictureFilePath)) {
			try {
				JSONObject req = new JSONObject();
				req.put("user_id", userId);
				req.put("file_name", fileName);
				JSONObject res = httpPostFile("/api/file/upload_picture.php", req,pictureFilePath);
				return (res != null && res.getInt("status") == RESPONSE_OK);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		API service = new API("http://localhost/diaoyumi");
		//System.out.println(service.authByEmail("changgb@hotmail.com", "abcd"));
		//System.out.println(service.getNearPalce(120.000, 30.000));
		File directory = new File("");//设定为当前文件夹
		try{
		    System.out.println(directory.getCanonicalPath());//获取标准的路径
		    System.out.println(directory.getAbsolutePath());//获取绝对路径
		}catch(Exception e){}

		System.out.println(service.uploadPicture(30, "p-abcdefghijklmnopqrstuvwxyz.png", "./test.png"));

	}

}
