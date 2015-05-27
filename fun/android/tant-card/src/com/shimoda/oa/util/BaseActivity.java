/**
 * 
 */
package com.shimoda.oa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shimoda.oa.R;
import com.shimoda.oa.activity.MainMenu;

/**
 * @author yumin
 * 
 */
public class BaseActivity extends Activity {

	protected int heightPixels;
	protected int widthPixels;
	protected int densityDpi;
	protected float density;
	
	protected ProgressDialog progressDlg;

	protected void getSize() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		heightPixels = dm.heightPixels;
		widthPixels = dm.widthPixels;
		densityDpi = dm.densityDpi;
		density = dm.density;
	}
	
	protected void showProgressDialog(){
		progressDlg = new ProgressDialog(this);  
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
        progressDlg.setMessage(this.getString(R.string.tips_loading));  
        progressDlg.setCancelable(false);  
          
        progressDlg.show();  
	}
	
	protected void hideProgressDialog(){
		if(progressDlg!=null){
			progressDlg.dismiss();  
		}
	}

	/**
	 * 自定义标题栏 (必须在onCreate内调用,不能隐藏系统标题栏)
	 * 
	 * @param frameLayoutID
	 *            主框架布局编号
	 * @param customLayoutID
	 *            自主标题栏布局
	 */
	protected void callTopBar(int frameLayoutID, int customLayoutID) {

		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(frameLayoutID);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				customLayoutID);
	}

	/**
	 * 是否退出对话
	 * 
	 * @return
	 */
	protected void callWhetherToExitDialog() {

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(getString(R.string.tip));
		builder.setMessage(getString(R.string.whether_to_exit));
		builder.setPositiveButton(getString(R.string.exit),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						exit();
					}
				});
		builder.setNegativeButton(getString(R.string.dlg_btn_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	/**
	 * 设置文本内容
	 * 
	 * @param resId
	 *            文本资源ID
	 * @param text
	 *            内容
	 */
	protected void setTextViewText(int resId, String text) {
		TextView textView = (TextView) this.findViewById(resId);
		textView.setText(text);
	}

	/**
	 * 设置文本内容
	 * 
	 * @param view
	 * @param resId
	 * @param text
	 */
	protected void setTextViewText(View view, int resId, String text) {
		TextView textView = (TextView) view.findViewById(resId);
		textView.setText(text);
	}

	protected void removeView(int resId) {
		this.findViewById(resId).setVisibility(View.GONE);
	}

	protected void hiddenView(int resId) {
		this.findViewById(resId).setVisibility(View.INVISIBLE);
	}

	protected void showView(int resId) {
		this.findViewById(resId).setVisibility(View.VISIBLE);
	}

	/**
	 * 杀掉进程退出
	 */
	protected void exit() {
		hideProgressDialog();
		
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
	}

	/**
	 * 获取存储路径
	 * 
	 * @return
	 */
	protected String getDbDir() {

		String dir = null;
		if (EnvironmentUtil.hasSdCard()) {
			dir = Constants.SDCARD_DIR;
		} else {
			dir = Constants.NO_SDCARD_DIR;
		}

		return dir;
	}

	/**
	 * 跳转至任意页 (不带参数)
	 * 
	 * @param cls
	 * @param isFinish
	 */
	protected void goToAnyActivity(Class<?> cls, boolean isFinish) {

		// 跳转任意页面
		Intent intent = new Intent(this, cls);
		this.startActivity(intent);
		if (isFinish) {
			this.finish();
		}
	}

	/**
	 * 跳转至任意页 (可带参数)
	 * 
	 * @param cls
	 * @param intent
	 * @param isFinish
	 */
	protected void goToAnyActivity(Class<?> cls, Intent intent, boolean isFinish) {

		// 跳转任意页面
		intent.setClass(this, cls);
		this.startActivity(intent);
		if (isFinish) {
			this.finish();
		}
	}

	/**
	 * 跳转回菜单界面
	 * 
	 * @param isFinish
	 */
	protected void goToMenuActivity(boolean isFinish) {
		ActivityManager manager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = manager.getRunningTasks(1);
		if (list != null && !list.isEmpty()) {
			for (RunningTaskInfo task : list) {
				if(list.get(0).topActivity.getClassName().indexOf("com.shimoda.oa")>=0){
					if(task.numActivities>1){
						finish();
						return;
					}
				}
			}
		}
		goToAnyActivity(MainMenu.class, isFinish);
	}

	/**
	 * 是否加载SDCard
	 * 
	 * @return
	 */
	protected boolean isMountedExternal() {

		boolean result = false;

		result = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

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
	 * 显示提示信息 (在当前页面浮层方式)
	 * 
	 * @param text
	 */
	protected void showToast(String text) {

		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void alert(int resId) {
		new AlertDialog.Builder(this).setMessage(getString(resId))
				.setPositiveButton(getString(R.string.dlg_btn_confirm), null)
				.show();
	}

	/**
	 * 存文件至手机
	 * 
	 * @param fileName
	 *            文件名
	 * @param dataMap
	 *            数据集
	 * @return
	 */
	protected boolean storeLocalFile(String fileName,
			Map<String, String> dataMap) {

		boolean result = false;

		try {
			FileOutputStream stream = this.openFileOutput(fileName,
					Context.MODE_WORLD_WRITEABLE);
			Properties properties = new Properties();
			if (null != dataMap && 0 < dataMap.size()) {
				Iterator<Entry<String, String>> it = dataMap.entrySet()
						.iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					String key = entry.getKey();
					String value = entry.getValue();
					properties.put(key, value);
				}
				properties.store(stream, Constants.APP_NAME_EN);
			}
			// 无异常则成功
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	@Override
	protected void onDestroy(){
		hideProgressDialog();
		
		super.onDestroy();
	}
}
