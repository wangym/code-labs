package com.diaoyumi.android.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dianoyumi.vo.Event;
import com.dianoyumi.vo.Location;
import com.diaoyumi.android.etc.ExtandCursor;
import com.diaoyumi.android.etc.Util;

public class DBAdapter {
	public static int MAX_PAGE_SIZE = 99999;
	public static DBAdapter instance = null;
	private static final String TABLE_CONF = "conf";
	private static final String TABLE_EVENT = "event";
	private static final String TABLE_LOCATION = "event_location";
	private Context context;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private HashMap<String,String> conf = new HashMap<String, String>();
	private final static String TABLE_CONF_KEY_FIELD = "key";
	private final static String TABLE_CONF_VAL_FIELD = "value";
	public final static String CONF_USER_EMAIL = "user_email";
	public final static String CONF_USER_NICK = "user_nick";
	public final static String CONF_USER_PHOTO = "user_photo";
	public final static String CONF_USER_PASSWORD = "user_password";
	public final static String CONF_USER_IS_LOGIN = "user_is_login";
	public final static String CONF_USER_ID = "user_id";
	private final static String TABLE_LOCATION_LAT_FIELD = "lat";
	private final static String TABLE_LOCATION_LNG_FIELD = "lng";
	private final static String TABLE_LOCATION_PLACE_FIELD = "place";
	private final static String TABLE_LOCATION_USER_ID_FIELD = "user_id";
	private final static String TABLE_LOCATION_VISITS_FIELD = "visits";
	private final static String TABLE_LOCATION_CREATED_FIELD = "created";
	private final static String TABLE_LOCATION_MODIFIED_FIELD = "modified";
	
	private final static String TABLE_EVENT_ID_FIELD = "id";
	private final static String TABLE_EVENT_USER_ID_FIELD = "user_id";
	private final static String TABLE_EVENT_RID_FIELD = "rid";
	private final static String TABLE_EVENT_TYPE_FIELD = "type";
	private final static String TABLE_EVENT_EVENT_TIME_FILED = "event_time";
	private final static String TABLE_EVENT_LAT_FIELD = "lat";
	private final static String TABLE_EVENT_LNG_FIELD = "lng";
	private final static String TABLE_EVENT_PLACE_FIELD = "place";
	private final static String TABLE_EVENT_IS_NEW_PLACE_FIELD = "is_new_place";
	private final static String TABLE_EVENT_COMPANION_FIELD = "companion";
	private final static String TABLE_EVENT_PICTURE_FIELD = "picture";
	private final static String TABLE_EVENT_TITLE_FIELD = "title";
	private final static String TABLE_EVENT_PRICE_FIELD = "price";
	private final static String TABLE_EVENT_INTRO_FIELD = "intro";
	private final static String TABLE_EVENT_PROPERTIES_FIELD = "properties";
	private final static String TABLE_EVENT_STATUS_FIELD = "status";
	private final static String TABLE_EVENT_CREATED_FIELD = "created";
	private final static String TABLE_EVENT_MODIFIED_FIELD = "modified";
	
	public final static DBAdapter getInstance(Context context){
		if (instance == null){
			instance = new DBAdapter();
			instance.context = context;
			instance.open();
		}
		return instance;
	}

	
	public void open(){
		dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
		loadConf();
	}
	
	public void close(){
		dbHelper.close();
	}

	private void loadConf(){
		Cursor cur = db.query(TABLE_CONF, null, null, null, null, null, null);
		if (cur == null) return;
		conf.clear();
		while(cur.moveToNext()){
			String key = cur.getString(cur.getColumnIndex(TABLE_CONF_KEY_FIELD));
			String val = cur.getString(cur.getColumnIndex(TABLE_CONF_VAL_FIELD));
			conf.put(key, val);
		}
		cur.close();
	}
	
	private boolean updateConf(String key, String val){
		ContentValues values =  new ContentValues();
		values.put(TABLE_CONF_KEY_FIELD, key);
		values.put(TABLE_CONF_VAL_FIELD, val);
		boolean ret = db.update(TABLE_CONF, values, TABLE_CONF_KEY_FIELD + " = ?", new String[] {key}) > 0;
		if (ret) conf.put(key, val);
		return ret;
	}
	
	private boolean deleteConf(String key){
		boolean ret = db.delete(TABLE_CONF, TABLE_CONF_KEY_FIELD + " = ?", new String[]{key}) >= 0;
		if (ret) conf.remove(key);
		return ret;
	}
	
	private boolean insertConf(String key, String val){
		ContentValues values =  new ContentValues();
		values.put(TABLE_CONF_KEY_FIELD, key);
		values.put(TABLE_CONF_VAL_FIELD, val);
		boolean ret =  db.insert(TABLE_CONF, null, values) > -1;
		if (ret) conf.put(key, val);
		return ret;
	}
	
	
	public String getConf(String name){
		//@TODO
		if (name == CONF_USER_ID) return "3";
		if (conf != null) return conf.get(name);
		return null;
	}
	
	
	public boolean register(String email, String nick, String password) {
		if (Util.isNotEmpty(email) && Util.isNotEmpty(nick)
				&& Util.isNotEmpty(password)) {
			db.delete(TABLE_CONF, null, null);
	
			return insertConf(CONF_USER_EMAIL, email)
					&& insertConf(CONF_USER_NICK, nick)
					&& insertConf(CONF_USER_PASSWORD, password)
					&& insertConf(CONF_USER_IS_LOGIN, "Y");
		}
		return false;
	}	
	
	public boolean logout(){
		if (conf != null) return updateConf(CONF_USER_IS_LOGIN, "N");
		return false;
	}
	
	public boolean isLogin(){
		if (conf != null) return "Y".equals(conf.get(CONF_USER_IS_LOGIN));
		return false;
	}
	
	public boolean login(String email, String password){
		boolean ret = false;
		if (conf != null && Util.isNotEmpty(email) && Util.isNotEmpty(password)){
			ret =  password.equals(conf.get(CONF_USER_PASSWORD)) && email.equals(conf.get(CONF_USER_EMAIL));
			if (ret) updateConf(CONF_USER_IS_LOGIN, "Y");
		}
		return ret;
	}
	
	public boolean changePassword(String oldPassword, String newPassword){
		if (conf != null && Util.isNotEmpty(oldPassword) && Util.isNotEmpty(newPassword)){
			return updateConf(CONF_USER_PASSWORD, newPassword);
		}
		return false;
	}
	
	
	public boolean changeUserPhoto(String newPhotoFilePath){
		if (conf != null && Util.isNotEmpty(newPhotoFilePath)){
			return updateConf(CONF_USER_PHOTO, newPhotoFilePath);
		}
		return false;
	}
	

	private boolean insertLocation(Location location){
		ContentValues values = new ContentValues();
		values.put(TABLE_LOCATION_USER_ID_FIELD, Integer.parseInt(getConf(CONF_USER_ID)));
		values.put(TABLE_LOCATION_LAT_FIELD, location.getLat());
		values.put(TABLE_LOCATION_LNG_FIELD, location.getLng());
		values.put(TABLE_LOCATION_PLACE_FIELD, location.getName());
		String now = Util.date2string(new Date());
		values.put(TABLE_LOCATION_CREATED_FIELD, now);
		values.put(TABLE_LOCATION_MODIFIED_FIELD, now);
		boolean ret =  db.insert(TABLE_LOCATION, null, values) > -1;	
		return ret;
	}
	
	
	public List<Location> getMyAllPlace(){
		
		String orderby = TABLE_LOCATION_CREATED_FIELD + " desc";
		Cursor dbCur = db.query(TABLE_LOCATION, null, null, null, null, null, orderby);
		ExtandCursor cur = new ExtandCursor(dbCur);
		ArrayList<Location> ret = new ArrayList<Location>();
		while(cur.moveToNext()){
			Location location = new Location();
			location.setLat(cur.getDoubleByName(TABLE_LOCATION_LAT_FIELD));
			location.setLng(cur.getDoubleByName(TABLE_LOCATION_LNG_FIELD));
			location.setName(cur.getStringByName(TABLE_LOCATION_PLACE_FIELD));
			ret.add(location);
		}
		cur.close();
		return ret;
	}
	
	
	public boolean insertEvent(Event event){
		ContentValues values =  new ContentValues();
		values.put(TABLE_EVENT_USER_ID_FIELD, event.getUserId());
		values.put(TABLE_EVENT_RID_FIELD, event.getRid());
		values.put(TABLE_EVENT_TYPE_FIELD, event.getType());
		values.put(TABLE_EVENT_EVENT_TIME_FILED, Util.date2string(event.getEventTime()));
		values.put(TABLE_EVENT_LAT_FIELD, event.getLat());
		values.put(TABLE_EVENT_LNG_FIELD, event.getLng());
		values.put(TABLE_EVENT_PLACE_FIELD, event.getPlace());
		values.put(TABLE_EVENT_IS_NEW_PLACE_FIELD, (event.isNewPlace()) ? "Y":"N");
		values.put(TABLE_EVENT_COMPANION_FIELD, event.getCompanion());
		values.put(TABLE_EVENT_PICTURE_FIELD, event.getPicture());
		values.put(TABLE_EVENT_TITLE_FIELD, event.getTitle());
		values.put(TABLE_EVENT_PRICE_FIELD, event.getPrice());
		values.put(TABLE_EVENT_INTRO_FIELD, event.getDesc());
		values.put(TABLE_EVENT_PROPERTIES_FIELD, event.getProperties());
		values.put(TABLE_EVENT_STATUS_FIELD, event.getStatus());
		String now = Util.date2string(new Date());
		values.put(TABLE_EVENT_CREATED_FIELD, now);
		values.put(TABLE_EVENT_MODIFIED_FIELD, now);
		boolean ret =  db.insert(TABLE_EVENT, null, values) > -1;	
		if (ret == true && event.isNewPlace()){
			//存入地标位置表
			Location location = new Location();
			location.setLat(event.getLat());
			location.setLng(event.getLng());
			location.setName(event.getPlace());
			insertLocation(location);
		}
		return ret;
	}
	
	
	/**
	 * 返回自己的发布的所有Event
	 * @return
	 */
	public List<Event> getMyAllEvent(int pageSize, int page){
		int pos = (page - 1) * pageSize;
		String limit = Integer.toString(pos) + "," + Integer.toString(pageSize);
		String orderby = TABLE_EVENT_CREATED_FIELD + " desc";
		Cursor dbCur = db.query(false, TABLE_EVENT, null, null, null, null, null, orderby, limit);
		ExtandCursor cur = new ExtandCursor(dbCur);
		ArrayList<Event> ret = new ArrayList<Event>();
		while(cur.moveToNext()){
			Event event = new Event();
			event.setId(cur.getIntByName(TABLE_EVENT_ID_FIELD));
			event.setUserId(cur.getIntByName(TABLE_EVENT_USER_ID_FIELD));
			event.setRid(cur.getStringByName(TABLE_EVENT_RID_FIELD));
			event.setType(cur.getString(cur.getColumnIndex(TABLE_EVENT_TYPE_FIELD)));
			event.setEventTime(cur.getDateByName(TABLE_EVENT_EVENT_TIME_FILED));
			event.setLat(cur.getIntByName(TABLE_EVENT_LAT_FIELD));
			event.setLng(cur.getIntByName(TABLE_EVENT_LNG_FIELD));
			event.setPlace(cur.getStringByName(TABLE_EVENT_PLACE_FIELD));
			event.setNewPlace(("Y".equals(cur.getStringByName(TABLE_EVENT_IS_NEW_PLACE_FIELD)) ? true : false));
			event.setCompanion(cur.getStringByName(TABLE_EVENT_COMPANION_FIELD));
			event.setPicture(cur.getStringByName(TABLE_EVENT_PICTURE_FIELD));
			event.setTitle(cur.getStringByName(TABLE_EVENT_TITLE_FIELD));
			event.setPrice(cur.getDoubleByName(TABLE_EVENT_PRICE_FIELD));
			event.setDesc(cur.getStringByName(TABLE_EVENT_INTRO_FIELD));
			event.setProperties(cur.getStringByName(TABLE_EVENT_PROPERTIES_FIELD));
			event.setStatus(cur.getIntByName(TABLE_EVENT_STATUS_FIELD));
			ret.add(event);
		}
		cur.close();
		return ret;
	}
	

	
	
}
