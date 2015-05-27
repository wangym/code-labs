/**
 * 
 */
package me.yumin.android.bodyshow.etc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yumin
 * 
 */
public class Constant {

	// 软件标识
	public static final String APP_NAME_EN = "bodyshow";
	public static final String APP_NAME_ZH = "身材秀";
	public static final String VERSION = "1.0";
	public static final String ENV = "dev";

	// 延迟载入(秒)
	public static final int DELAY_LOADING = 1000;
	// 签名参数
	public static final String SIGN_PREFIX = "_body_show_";
	public static final String SIGN_SUFFIX = "_v1.0";

	// 文件存储
	public static final String LOGIN_FILE_NAME = "bodyshow_login";
	public static final String LOCAL_FILE_PATH = "/data/data/me.yumin.android.bodyshow.activity/files/";
	public static final String UPLOAD_FILE_NAME = "bodyshow_upload";
	public static final String SDCARD_FILE_PATH = "/sdcard/bodyshow/files/";

	//
	public static final String KEY_LOGIN_SIGN = "_sign";
	public static final String KEY_LOGIN_TIME = "_time";
	public static final String KEY_LOGIN_MOBILE = "mobile";
	public static final String KEY_LOGIN_PASSWORD = "password";
	public static final String KEY_LOGIN_SEX = "sex";
	public static final String KEY_LOGIN_LOG = "log";

	/**
	 * 
	 */
	public static final int VAL_SEX_UNKOWN = 0;
	public static final int VAL_SEX_MALE = 1;
	public static final int VAL_SEX_FEMALE = 2;
	public static final int VAL_REQUEST_CODE = 0;

	/**
	 * 
	 */
	public static final String TIP = "提示";
	public static final String TIP_SELECT = "请选择";
	public static final String TIP_UNKOWN_ERROR = "操作失败稍后再试";
	public static final String TIP_NO_SDCARD = "未检测到SD卡无法继续使用";
	public static final String TIP_WHETHER_TO_EXIT = "确定退出身材秀吗?";
	public static final String TIP_CAMERA_SHOOTING = "相机拍摄";
	public static final String TIP_PHONE_ALBUM = "手机相册";
	public static final String TIP_PROGRESS_REGISTER = "注册中,请稍后...";
	public static final String TIP_PROGRESS_LOGIN = "登录中,请稍后...";
	public static final String TIP_TITLE_LOGIN = "身材秀登录";
	public static final String TIP_TITLE_REGISTER = "新用户注册";
	public static final String TIP_TITLE_SHOW_ME = "我要秀";
	public static final String TIP_TITLE_SHOW_TA = "TA人秀";
	public static final String TIP_EMPTY_MOBILE = "手机号码不能为空";
	public static final String TIP_EMPTY_PASSWORD = "登录密码不能为空";
	public static final String TIP_EMPTY_SEX = "用户性别不能为空";
	public static final String TIP_FORMAT_MOBILE = "手机号码格式错误";
	public static final String TIP_FORMAT_PASSWORD = "密码必需6位以上";
	public static final String TIP_BUTTON_EXIT = "退出";
	public static final String TIP_BUTTON_CONFIRM = "确定";
	public static final String TIP_BUTTON_CANCEL = "取消";

	/**
	 * 开发环境
	 */
	public final static Map<String, String> API_DEV = new HashMap<String, String>();
	static {
		API_DEV.put("BODY_SHOW_REGISTER", "http://192.168.1.114/body-show/api/register.php");
		API_DEV.put("BODY_SHOW_LOGIN", "http://192.168.1.114/body-show/api/login.php");
	}

	/**
	 * 生产环境
	 */
	public final static Map<String, String> API_PROD = new HashMap<String, String>();
	static {
		API_PROD.put("BODY_SHOW_REGISTER", "http://bodyshow.yumin.me/api/register.php");
		API_PROD.put("BODY_SHOW_LOGIN", "http://bodyshow.yumin.me/api/login.php");
	}
}
