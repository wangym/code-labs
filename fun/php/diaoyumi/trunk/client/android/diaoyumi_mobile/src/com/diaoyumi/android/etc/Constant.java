/**
 * 
 */
package com.diaoyumi.android.etc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yumin
 * 
 */
public class Constant {

	// 应用标识
	public static final String CNAME = "钓鱼迷";
	public static final String ENAME = "diaoyumi";
	public static final String VERSION = "1.0";
	public static final String ENV = "prod";
	public static final String KEY = "diaoyumi_a78102-";
	// 延迟载入(秒)
	public static final int DELAY_LOADING = 2000;
	// 文件存储
	public static final String FILE_LOGIN = "login";
	public static final String FILE_EXCEPTION = "exception";
	public static final String LOCAL_PATH_ROOT = "/data/data/com.diaoyumi.android.activity/files/";
	public static final String SDCARD_PATH_ROOT = "/sdcard/diaoyumi/";
	// keys
	public static final String K_REGISTER = "register";
	// values
	// public static final String V_ = "";
	// 开发环境
	public final static Map<String, String> API_DEV = new HashMap<String, String>();
	static {
		API_DEV.put(K_REGISTER, "http://");
	}
	// 生产环境
	public final static Map<String, String> API_PROD = new HashMap<String, String>();
	static {
		API_PROD.put(K_REGISTER, "http://?/api/user/register.php");
	}
	
	public final static String PATH_ROOT = "/sdcard/diaoyumi";
	public final static String PATH_IMAGE = PATH_ROOT + "/image";
	
	public final static String CAMERA_TEMP_FILE = "/sdcard/diaoyumi/camera_temp.jpg";
	public final static String NEW_JPEG_FILE = "/sdcard/diaoyumi/new_camera.jpg";
	
	public final static int POST_TYPE_LAND_SPACE = 1;
	public final static int POST_TYPE_BIG_FISH = 2;
	public final static int POST_TYPE_MULTI_FISH = 3; 

}
