package com.diaoyumi.android.etc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.diaoyumi.android.database.DBAdapter;

import dalvik.system.VMRuntime;

public class Diaoyumi{
	private final static int HEAP_SIZE = 6 * 1024 * 1024;
	private static Application application = null;
	private static Activity curActivity = null;
	private static DBAdapter dbAdapter = null;
	private static LocationManager locationManager = null;
	private static BMapManager bMapManager = null;
	private static String bMapKey = "3A40B1377B9F245A266C91A2EB9037A6A1C40D36";
	private static HashMap<String, Object> newEvent = new HashMap<String, Object>();
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static float screenDpi = 0;


	public final static void startup(Application app){
		if (application == null){
			application = app;
			dbAdapter = DBAdapter.getInstance(app.getApplicationContext());
			locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
			bMapManager = new BMapManager(app.getApplicationContext());
			bMapManager.init(bMapKey, new MyGeneralListener());
			bMapManager.getLocationManager().enableProvider((int)MKLocationManager.MK_NETWORK_PROVIDER);
			bMapManager.getLocationManager().enableProvider((int)MKLocationManager.MK_GPS_PROVIDER);
			bMapManager.getLocationManager().setNotifyInternal(10, 5);
			Util.createFolder(Constant.PATH_ROOT);
			Util.createFolder(Constant.PATH_IMAGE);
			//VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE);
		}
		return;
	}

	public final static void shutdown(){
		if (dbAdapter != null){
			dbAdapter.close();
			dbAdapter = null;
		}
		if (bMapManager != null){
			bMapManager.destroy();
			bMapManager = null;
		}
	}

	public final static void setScreenSize(int w, int h, int dpi){
		screenWidth = w;
		screenHeight = h;
		screenDpi = dpi;
	}
	
	public final static int getScreenWidth(){
		return screenWidth;
	}
	
	public final static int getScreenHeight(){
		return screenHeight;
	}
	
	
	public final static Resources getResources(){
		if (application != null) return application.getResources();
		return null;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
			Toast.makeText(getContext(), "您的网络出错啦！",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(getContext(), 
						"请在BMapApiDemoApp.java文件输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public final static BMapManager getBaiduMapManager(){
		return bMapManager;
	}
	
	
    public final static Location getHereLocation(){
    	Location ret = null;
    	String lcp;
    	try {
    		Criteria cr = new Criteria();
    		cr.setAccuracy(Criteria.ACCURACY_FINE);
    		cr.setAltitudeRequired(false);
    		cr.setBearingRequired(false);
    		cr.setCostAllowed(true);
    		cr.setPowerRequirement(Criteria.POWER_LOW);
    		lcp = locationManager.getBestProvider(cr, true);
    		ret = locationManager.getLastKnownLocation(lcp);
    		if (ret == null && "gps".equals(lcp) ){
    			ret = locationManager.getLastKnownLocation("network");
    		}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return ret;
    }
    
    

	public final static void info(Activity parent, String message){
		AlertDialog.Builder builder = new Builder(parent);
		builder.setMessage(message);
		builder.setNegativeButton("确定", null);
		builder.create().show();
	}
	
	public final static void confirm(Activity parent, String message,  DialogInterface.OnClickListener okListener){
		AlertDialog.Builder builder = new Builder(parent);
		builder.setMessage(message);
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", okListener);
		builder.create().show();
	}
	
	public final static Object getExtras(Activity act, String key){
		if (act != null && Util.isNotEmpty(key)){
			Bundle bundle = act.getIntent().getExtras();
			if (bundle != null) return bundle.get(key);
		}
		return null;
	}
	
	public final static String getExtrasAsString(Activity act, String key){
		Object ret = getExtras(act, key);
		return (ret == null) ? null : (String) ret;
	}
	
	public final static int getExtrasAsInt(Activity act, String key){
		Object ret = getExtras(act, key);
		return (ret == null) ? 0 : (Integer) ret;
	}
	
	public final static double getExtrasAsDouble(Activity act, String key){
		Object ret = getExtras(act, key);
		return (ret == null) ? 0 : (Double) ret;
	}

	
	public final static void go(Activity from, Class<?> toClass) {
		go(from, toClass, null);
	}
	
	public final static void go(Activity from, Class<?> toClass, int enterAnim, int exitAnim) {
		go(from, toClass, null, enterAnim, exitAnim);
	}
	

	public final static void go(Activity from, Class<?> toClass, String key1, Object val1){
		go(from, toClass, key1, val1, null, null, null,null);
	}

	public final static void go(Activity from, Class<?> toClass, String key1, Object val1, String key2, Object val2){
		go(from, toClass, key1, val1, key2, val2, null,null);
	}
	
	public final static void go(Activity from, Class<?> toClass, String key1, Object val1, String key2, Object val2, String key3, Object val3){
		HashMap<String, Object> params = new HashMap<String, Object>();
		if (key1 != null && val1 != null ) params.put(key1, val1);
		if (key2 != null && val2 != null ) params.put(key2, val2);
		if (key3 != null && val3 != null ) params.put(key3, val3);
		go(from, toClass, params);
	}
	
	public final static void go(Activity from, Class<?> toClass, Map<String,Object> params) {
		go(from, toClass, params, 0, 0);
	}
	
	public final static void go(Activity from, Class<?> toClass, Map<String,Object> params , int enterAnim, int exitAnim) {
		Intent intent = new Intent(from, toClass);
		if (params != null && params.size() >0){
			Iterator<Entry<String, Object>> it = params.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Object> entry = (Entry<String, Object>) it.next();
				Object val = entry.getValue();
				if (val != null){
					if (val instanceof Integer){
						intent.putExtra(entry.getKey(), (Integer) val);
					}else if (val instanceof Double) {
						intent.putExtra(entry.getKey(), (Double) val);
					}else if (val instanceof Boolean){
						intent.putExtra(entry.getKey(), (Boolean) val);
					}else{
						intent.putExtra(entry.getKey(), val.toString());
					}
				}
			}
		}
		from.startActivity(intent);
		if (enterAnim > 0 && exitAnim > 0){
			from.overridePendingTransition(enterAnim, exitAnim);
		}
	}

	
	public final static Application getApplication(){
		return application;
	}
	
	public final static Context getContext(){
		if (application != null)
			return application.getApplicationContext();
		return null;
	}
	
	public final static DBAdapter getDBAdapter(){
		return dbAdapter;
	}
	
	public final static void setCurActivity(Activity activity){
		curActivity = activity;
	}
	
	public final static Activity getCurActivity(){
		return curActivity;
	}
	

	
	public final static boolean saveAlbumPhotoToJpegFile(Intent data, String jpegFileName){
		if (data != null){
			Uri selectUri = data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};
			Cursor cursor = application.getContentResolver().query(selectUri, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return savePhotoToJpegFile(filePath, jpegFileName);
		}
		return false;
	}
	
	//该方法目的时管理bitmap务必释放之
	public final static boolean savePhotoToJpegFile(String photoFilePath, String jpegFileName){
		Bitmap bmp = null;
		try{
			bmp = BitmapFactory.decodeFile(photoFilePath);
			return saveBitmapToJpegFile(bmp, jpegFileName);
		}finally{
			if (bmp != null){
				bmp.recycle();
				bmp = null;
			}
		}
	}
	
	public final static boolean saveBitmapToJpegFile(Bitmap bmp,
			String jpegFileName) {
		if (bmp != null && Util.isNotEmpty(jpegFileName)) {
			FileOutputStream out = null;
			try {
				Util.deleteFile(jpegFileName);
				out = new FileOutputStream(jpegFileName, false);
				bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if (out != null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
	
	public final static void initNew(){
		newEvent.clear();
	}
	
	public final static void initNew(String k, Object v){
		initNew();
		putNew(k, v);
	}
	
	public final static void putNew(String k, Object v){
		newEvent.put(k, v);
	}
	
	public final static Object getNew(String k){
		return newEvent.get(k);
	}
	
	
}
