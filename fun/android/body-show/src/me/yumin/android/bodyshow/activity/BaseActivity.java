/**
 * 
 */
package me.yumin.android.bodyshow.activity;

import me.yumin.android.bodyshow.etc.Constant;
import me.yumin.android.bodyshow.etc.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * @author yumin
 * 
 */
public class BaseActivity extends Activity {

	/**
	 * 
	 */
	protected ProgressDialog progressDialog;
	protected String progressTips;
	protected Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			showToast(progressTips);
		}
	};

	/**
	 * 肯定退出对话
	 * 
	 * @return
	 */
	protected void callExitDialog(String message) {

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(Constant.TIP);
		builder.setMessage(message);
		builder.setPositiveButton(Constant.TIP_BUTTON_EXIT,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						exit();
					}
				});
		builder.show();
	}

	/**
	 * 是否退出对话
	 * 
	 * @return
	 */
	protected void callWhetherToExitDialog() {

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(Constant.TIP);
		builder.setMessage(Constant.TIP_WHETHER_TO_EXIT);
		builder.setPositiveButton(Constant.TIP_BUTTON_EXIT,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						exit();
					}
				});
		builder.setNegativeButton(Constant.TIP_BUTTON_CANCEL,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
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

		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(frameLayoutID);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, customLayoutID);
	}

	/**
	 * 在SD上建文件夹
	 * 
	 * @param path
	 * @return
	 */
	protected boolean createExternalFolder(String path) {

		boolean result = false;

		File file = new File(path);
		if (!file.exists()) {
			result = file.mkdirs();
		}

		return result;
	}

	/**
	 * 手机上删文件
	 * 
	 * @param fileName 文件名
	 * @return
	 */
	protected boolean deleteLocalFile(String fileName) {

		boolean result = false;

		try {
			result = this.deleteFile(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 杀掉进程退出
	 */
	protected void exit() {

		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
	}

	/**
	 * 跳转至任意页
	 * (不带参数)
	 * 
	 * @param cls
	 */
	protected void goToAnyActivity(Class<?> cls) {

		// 跳转任意页面
		Intent intent = new Intent(this, cls);
		this.startActivity(intent);
		this.finish();
	}

	/**
	 * 跳转至任意页
	 * (可带参数)
	 * 
	 * @param cls
	 * @param intent
	 */
	protected void goToAnyActivity(Class<?> cls, Intent intent) {

		// 跳转任意页面
		intent.setClass(this, cls);
		this.startActivity(intent);
		this.finish();
	}

	/**
	 * 判断是否登录 (据登录时生成的文件)
	 * 
	 * @return
	 */
	protected boolean isLogged() {

		boolean result = false;

		Properties properties = loadLocalProperties(Constant.LOCAL_FILE_PATH, Constant.LOGIN_FILE_NAME);
		if (null != properties) {
			String sign = (String) properties.get(Constant.KEY_LOGIN_SIGN);
			String mobile = (String) properties.get(Constant.KEY_LOGIN_MOBILE);
			String password = (String) properties.get(Constant.KEY_LOGIN_PASSWORD);
			if (Util.isNotNullAndEmpty(sign) && Util.isNotNullAndEmpty(mobile) && Util.isNotNullAndEmpty(password)) {
				if (sign.equalsIgnoreCase(Util.getSign(mobile + password))) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * 是否加载SDCard
	 * 
	 * @return
	 */
	protected boolean isMountedExternal() {

		boolean result = false;

		result = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		return result;
	}

	/**
	 * 隐藏虚拟键盘
	 */
	protected void hideSoftInputFromWindow(IBinder windowToken) {

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
	}

	/**
	 * 隐藏系统状态
	 */
	protected void hideSystemStatusBar() {

		int flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		int mask = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		this.getWindow().setFlags(flags, mask);
	}

	/**
	 * 隐藏系统标题
	 */
	protected void hideSystemTitleBar() {

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_PROGRESS);
	}

	/**
	 * 读取文件数据
	 * 
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	protected Properties loadLocalProperties(String filePath, String fileName) {

		Properties properties = null;

		try {
			File file = new File(filePath + fileName);
			if (file.exists()) {
				FileInputStream stream = this.openFileInput(fileName);
				properties = new Properties();
				properties.load(stream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return properties;
	}

	/**
	 * 显示提示信息
	 * (在当前页面浮层方式)
	 * 
	 * @param text
	 */
	protected void showToast(String text) {

		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 存文件至手机
	 * 
	 * @param fileName 文件名
	 * @param dataMap 数据集
	 * @return
	 */
	protected boolean storeLocalFile(String fileName, Map<String, String> dataMap) {

		boolean result = false;

		try {
			FileOutputStream stream = this.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
			Properties properties = new Properties();
			if (null != dataMap && 0 < dataMap.size()) {
				Iterator<Entry<String, String>> it = dataMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					String key = entry.getKey();
					String value = entry.getValue();
					properties.put(key, value);
				}
				properties.store(stream, Constant.APP_NAME_EN);
			}
			// 无异常则成功
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
