package com.shimoda.oa.util;

import android.os.Environment;

/**
 * 环境工具类
 * 
 * @author youcai.lu
 * 
 */
public class EnvironmentUtil {
	public static boolean hasSdCard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean aboveDonut(){
		if(android.os.Build.VERSION.SDK_INT>android.os.Build.VERSION_CODES.DONUT){
			return true;
		}
		return false;
	}
}
