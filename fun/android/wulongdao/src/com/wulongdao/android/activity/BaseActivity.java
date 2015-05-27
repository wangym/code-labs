/**
 * 
 */
package com.wulongdao.android.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.wulongdao.etc.CommonUtil;
import com.wulongdao.android.domain.dataobject.UserDO;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Validator;
import com.wulongdao.android.service.UserService;

/**
 * @author yumin
 * 
 */
public abstract class BaseActivity extends Activity {

	/**
	 * 
	 */
	protected static final int MENU_EXIT = Menu.FIRST;
	protected static final int MENU_LOGOFF = Menu.FIRST + 1;
	protected static final int MENU_USER_MODIFY_PASSWORD = Menu.FIRST + 2;
	protected static final int MENU_USER_UPLOAD_AVATAR = Menu.FIRST + 3;

	/**
	 * 
	 */
	protected static UserDO userDO;
	protected static UserService userService;
	protected static Validator validator;

	/**
	 * 
	 */
	protected ProgressDialog progressDialog;
	protected String progressMsg;
	protected Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			showToast(progressMsg);
			progressDialog.dismiss();
		}
	};

	/**
	 * 必需退出对话
	 * 
	 * @param message
	 */
	protected void callMustExitDialog(String message) {

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(getString(R.string.prompt));
		builder.setMessage(message);
		builder.setPositiveButton(getString(R.string.exit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						exit();
					}
				});
		builder.show();
	}

	/**
	 * 自定义标题栏
	 * (必须在onCreate内调用,不能隐藏系统标题栏)
	 * 
	 * @param frameLayoutID 主框架布局编号
	 * @param customLayoutID 自主标题栏布局
	 */
	protected void callTopBar(int frameLayoutID, int customLayoutID) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(frameLayoutID);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, customLayoutID);
	}

	/**
	 * 是否退出对话
	 */
	protected void callWhetherToExitDialog() {

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(getString(R.string.prompt));
		builder.setMessage(getString(R.string.prompt_whether_to_exit));
		builder.setPositiveButton(getString(R.string.exit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						exit();
					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	/**
	 * 是否注销对话
	 */
	protected void callWhetherToLogoffDialog() {

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(getString(R.string.prompt));
		builder.setMessage(getString(R.string.prompt_whether_to_logoff));
		builder.setPositiveButton(getString(R.string.logoff),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						logoff();
					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	/**
	 * 
	 * @param file
	 */
	protected void chooseImage(final File file) {

		final CharSequence[] items = {getString(R.string.prompt_hint_camera_shooting), getString(R.string.prompt_hint_phone_album) };
		final String select = getString(R.string.prompt_select);

		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(select);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case Constant.V_CAMERA_SHOOTING:
					// 相机拍摄
					Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intentCamera.putExtra(Constant.K_OUTPUT, Uri.fromFile(file));
					intentCamera.putExtra(Constant.K_OUTPUT_FORMAT, Bitmap.CompressFormat.JPEG.name());
					startActivityForResult(intentCamera, item);
					break;
				case Constant.V_PHONE_ALBUM:
					// 手机相册
					Intent intentAlbum = new Intent(Intent.ACTION_GET_CONTENT);
					intentAlbum.setType("image/*");
					intentAlbum.putExtra(Constant.K_OUTPUT_FORMAT, Bitmap.CompressFormat.JPEG.name());
					startActivityForResult(Intent.createChooser(intentAlbum, select), item);
					break;
				default:
					break;
				}
			}
		});
		builder.show();
	}

	/**
	 * 
	 */
	protected void destroy() {

		userDO = null;
		userService = null;
		validator = null;
	}

	/**
	 * 杀掉进程退出
	 */
	protected void exit() {

		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
	}

	/**
	 * 注销重要登录
	 */
	protected void logoff() {

		if (deleteFile(Constant.FILE_COOKIE)) {
			destroy();
			goToActivity(UserLoginActivity.class);
		}
	}

	/**
	 * 
	 * @return
	 */
	protected String getAvatar() {

		String avatar = null;

		if (null != userDO) {
			avatar = userDO.getAvatar();
		}

		return avatar;
	}

	/**
	 * 
	 * @return
	 */
	protected String getEmail() {

		String email = null;

		if (null != userDO) {
			email = userDO.getEmail();
		}

		return email;
	}

	/**
	 * 
	 * @return
	 */
	protected String getLastUpdated() {

		return getString(R.string.pull_to_refresh_last_updated) + new SimpleDateFormat("MM-dd HH:mm").format(new Date());
	}

	/**
	 * 
	 * @return
	 */
	protected String getNickname() {

		String nickname = null;

		if (null != userDO) {
			nickname = userDO.getNickname();
		}

		return nickname;
	}

	/**
	 * 
	 * @return
	 */
	protected String getPassword() {

		String password = null;

		if (null != userDO) {
			password = userDO.getPassword();
		}

		return password;
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	protected String getPathFromUri(Uri uri) {

		String path = null;

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (null != cursor) {
			int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			path = cursor.getString(index);
		}

		return path;
	}

	/**
	 * 
	 * @return
	 */
	protected int getUserId() {

		int userId = 0;

		if (null != userDO) {
			userId = userDO.getUserId();
		}

		return userId;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	protected String getType(File file) {

		String type = "";

		String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
		if (end.equalsIgnoreCase("aac") || end.equalsIgnoreCase("amr") || end.equalsIgnoreCase("mp3") || end.equalsIgnoreCase("mp4") || end.equalsIgnoreCase("mpeg")) {
			type = "audio";
		} else if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("jepg") || end.equalsIgnoreCase("gif") || end.equalsIgnoreCase("png")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";

		return type;
	}

	/**
	 * 跳转至任意页
	 * (默认关闭)
	 * 
	 * @param cls
	 */
	protected void goToActivity(Class<?> cls) {

		goToActivity(cls, true);
	}

	/**
	 * 跳转至任意页
	 * (选择关闭)
	 * 
	 * @param cls
	 * @param isFinish
	 */
	protected void goToActivity(Class<?> cls, boolean isFinish) {

		Intent intent = new Intent(this, cls);
		startActivity(intent);
		if (isFinish) {
			finish();
		}
	}

	/**
	 * 跳转至任意页
	 * (可带参数,默认关闭)
	 * 
	 * @param cls
	 * @param intent
	 */
	protected void goToActivity(Class<?> cls, Intent intent) {

		goToActivity(cls, intent, true);
	}

	/**
	 * 跳转至任意页
	 * (可带参数,选择关闭)
	 * 
	 * @param cls
	 * @param intent
	 * @param isFinish
	 */
	protected void goToActivity(Class<?> cls, Intent intent, boolean isFinish) {

		intent.setClass(this, cls);
		startActivity(intent);
		if (isFinish) {
			finish();
		}
	}

	/**
	 * 跳至应用首页
	 */
	protected void goToIndex() {

		goToActivity(QuestionListActivity.class);
	}

	/**
	 * 跳至问题详情
	 */
	protected void goToQuestionDetailActivity(HashMap<String, Object> questionDetail) {

		Intent intent = new Intent();
		intent.putExtra(Constant.K_QUESTION_DETAIL, questionDetail);
		goToActivity(QuestionDetailActivity.class, intent);
	}

	/**
	 * 隐藏虚拟键盘
	 * 
	 * @param token
	 * @param tokens
	 */
	protected void hideSoftInputFromWindow(IBinder token, IBinder... tokens) {

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(token, 0);
		if (null != tokens && 0 < tokens.length) {
			for (IBinder iBinder : tokens) {
				inputMethodManager.hideSoftInputFromWindow(iBinder, 0);
			}
		}
	}

	/**
	 * 隐藏系统状态
	 */
	protected void hideSystemStatusBar() {

		int flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		int mask = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(flags, mask);
	}

	/**
	 * 隐藏系统标题
	 */
	protected void hideSystemTitleBar() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_PROGRESS);
	}

	/**
	 * 是否已经联网
	 * 
	 * @return
	 */
	protected boolean isActiveNetwork() {

		boolean result = false;

		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != conn) {
			NetworkInfo info = conn.getActiveNetworkInfo();
			if (null != info && info.isAvailable()) {
				return true;
			}
		}

		return result;
	}

	/**
	 * 是否已加载SD
	 * 
	 * @return
	 */
	protected boolean isMounted() {

		boolean result = false;

		String storageState = Environment.getExternalStorageState();
		if (CommonUtil.isNotEmpty(storageState)) {
			result = storageState.equals(android.os.Environment.MEDIA_MOUNTED);
		}

		return result;
	}

	/**
	 * 判断是否登录
	 * 
	 * @param recheck 是否需要联网复检(会更安全但有开销)
	 * @return
	 */
	private boolean isLogged(boolean recheck) {

		boolean result = false;

		if (null == userDO) {
			Properties login = loadProperties(Constant.PATH_LOCAL, Constant.FILE_COOKIE);
			if (null != login && 0 < login.size()) {
				userDO = UserDO.fromJSONString(login.getProperty(Constant.K_USER));
			}
		}
		if (null == userService) {
			userService = new UserService();
		}
		if (null == validator) {
			validator = new Validator(getApplicationContext());
		}

		// 读取文件
		if (null != userDO && userDO.verifyToken()) {
			// 通过初检
			if (recheck && isActiveNetwork()) {
				/* 联网复检 */
				String email = userDO.getEmail();
				String password = userDO.getPassword();
				result = userService.isLogged(email, password);
			} else {
				/* 无需复检 */
				result = true;
			}
		}

		return result;
	}

	/**
	 * 是否安全已登
	 * (联网复检)
	 * 
	 * @return
	 */
	protected boolean isLoggedNetwork() {

		return isLogged(true);
	}

	/**
	 * 是否简单登录
	 * (仅本地检)
	 * 
	 * @return
	 */
	protected boolean isLoggedLocal() {

		return isLogged(false);
	}

	/**
	 * 读取任意文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	protected Properties loadProperties(String filePath, String fileName) {

		Properties properties = null;

		try {
			File file = new File(filePath + fileName);
			if (file.exists()) {
				FileInputStream stream = openFileInput(fileName);
				properties = new Properties();
				properties.load(stream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return properties;
	}

	/**
	 * 
	 * @param file
	 */
	protected void openFile(File file) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		startActivity(intent);
	}

	/**
	 * 显示提示信息
	 * (浮层方式)
	 * 
	 * @param text
	 */
	protected void showToast(String text) {

		if (CommonUtil.isNotEmpty(text)) {
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 保存免登文件
	 * 
	 * @param userDO
	 * @return
	 */
	protected boolean storeCookie(UserDO userDO) {

		return storeFile(Constant.FILE_COOKIE, userService.getJsonMap(userDO));
	}

	/**
	 * 存文件至手机
	 * 
	 * @param fileName 文件名
	 * @param dataMap 数据集
	 * @return
	 */
	protected boolean storeFile(String fileName, Map<String, String> dataMap) {

		boolean result = false;

		try {
			if (null != dataMap && 0 < dataMap.size()) {
				Properties properties = new Properties();
				FileOutputStream stream = openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
				Iterator<Entry<String, String>> it = dataMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					properties.put(entry.getKey(), entry.getValue());
				}
				properties.store(stream, Constant.KEY);
			}
			// 无异常即成功
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
