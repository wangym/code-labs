/**
 * 
 */
package com.diaoyumi.android.etc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.diaoyumi.android.activity.R;
import com.diaoyumi.android.activity.R.string;
import com.diaoyumi.android.etc.Listener.OnSelectPhoto;

/**
 * @author yumin
 * 
 */
public abstract class AbstractActivity extends Activity {
	/**
	 * 
	 */
	protected ProgressDialog progressDialog;
	protected String progressPrompt;
	protected Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			showToast(progressPrompt);
			progressDialog.dismiss();
		}
	};
	private final int  CAMERA_REQUEST_CODE 	= 10000;
	private final int  PICKED_PHOTO_CODE 	= 10001;
	protected OnSelectPhoto onSelectPhoto;
	

	/**
	 * 必需退出对话
	 * 
	 * @param message
	 * @return
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
	 * 自定义标题栏(必须在onCreate内调用,不能隐藏系统标题栏)
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
	 * 
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
	 * 杀掉进程退出
	 */
	protected void exit() {

		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
	}

	/**
	 * 跳转至任意页(默认关闭)
	 * 
	 * @param cls
	 */
	protected void goToActivity(Class<?> cls) {

		goToActivity(cls, true);
	}

	/**
	 * 跳转至任意页(选择关闭)
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
	 * 跳转至任意页(可带参数,默认关闭)
	 * 
	 * @param cls
	 * @param intent
	 */
	protected void goToActivity(Class<?> cls, Intent intent) {

		goToActivity(cls, intent, true);
	}

	/**
	 * 跳转至任意页(可带参数,选择关闭)
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
	 * 隐藏虚拟键盘
	 * 
	 * @param windowToken
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
		if (Util.isNotEmpty(storageState)) {
			result = storageState.equals(android.os.Environment.MEDIA_MOUNTED);
		}

		return result;
	}

	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	protected boolean isLoggedIn() {

		boolean result = false;

		// TODO

		return result;
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
	 * 显示提示信息(浮层方式)
	 * 
	 * @param text
	 */
	protected void showToast(String text) {

		if (Util.isNotEmpty(text)) {
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
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
				properties.store(stream, Constant.ENAME);
			}
			// 无异常即成功
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	
	protected void getCameraPhoto(Listener.OnSelectPhoto onSelectPhoto){
		final String[] items = {"拍照","从相册里选取"};		
		this.onSelectPhoto = onSelectPhoto;
		if (this.onSelectPhoto == null) return;
		
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("你从哪里获取照片?");
		builder.setNegativeButton("取消", null);
		builder.setItems(items, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0: //拍照
						Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						Uri uri = Uri.fromFile(new File(Constant.CAMERA_TEMP_FILE));
						intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
						startActivityForResult(intentCamera, CAMERA_REQUEST_CODE);
						break;
					case 1: //相册选取
						Intent intentPicked = new Intent(Intent.ACTION_PICK);
						intentPicked.setType("image/*"); 
						intentPicked.setAction("android.intent.action.GET_CONTENT");
						startActivityForResult(Intent.createChooser(intentPicked, "选择图片"), PICKED_PHOTO_CODE);
						break;
					default:
						break;
				}
				
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
			this.onSelectPhoto.onSelect(Constant.CAMERA_TEMP_FILE);
		}
		else if (requestCode == PICKED_PHOTO_CODE && resultCode == RESULT_OK){
			this.onSelectPhoto.onSelect(Constant.CAMERA_TEMP_FILE);
		}
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		Diaoyumi.setCurActivity(this);
	}
	
	
	
	

}
