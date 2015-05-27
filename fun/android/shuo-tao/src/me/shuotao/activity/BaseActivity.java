/**
 * 
 */
package me.shuotao.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import me.shuotao.etc.Constant;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author yumin
 * 
 */
public class BaseActivity extends Activity {

	/**
	 * 嵌套按钮
	 */
	// 左按钮
	protected Button leftBtn;
	// 中标题
	protected TextView centerTV;
	// 右按钮
	protected Button rightBtn;

	/**
	 * 自主标题栏
	 * 必须在onCreate内调用,不能隐藏系统标题栏
	 * 
	 * @param frameLayoutID 主框架布局编号
	 * @param customLayoutID 自主标题栏布局
	 */
	protected void callCustomTitleBar(int frameLayoutID, int customLayoutID) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(frameLayoutID);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, customLayoutID);
	}

	/**
	 * 在手机上删文件
	 * Path: /data/data/[namespace]/files/
	 * 
	 * @param fileName 文件名
	 * @return
	 */
	protected boolean delFile(String fileName) {

		boolean result = false;

		try {

			// 删除存储文件
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
	 * 显示提示信息
	 * 在当前页面浮层方式
	 * 
	 * @param text
	 */
	protected void showToast(String text) {

		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 跳转至任意页(不带参数)
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
	 * 跳转至任意页(可带参数)
	 * 
	 * @param intent
	 * @param cls
	 */
	protected void goToAnyActivity(Intent intent, Class<?> cls) {

		// 跳转任意页面
		intent.setClass(this, cls);
		this.startActivity(intent);
		this.finish();
	}

	/**
	 * 隐藏状态栏
	 */
	protected void hiddenSystemStatusBar() {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 隐藏标题栏
	 */
	protected void hiddenSystemTitleBar() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_PROGRESS);
	}

	/**
	 * 初始化标题栏控件
	 */
	protected void initTitlebarControl() {

		leftBtn = (Button) findViewById(R.id.leftButton);
		centerTV = (TextView) findViewById(R.id.centerTextView);
		rightBtn = (Button) findViewById(R.id.rightButton);
	}

	/**
	 * 判断是否登录
	 * 据登录时生成的文件
	 * 
	 * @return
	 */
	protected boolean isLogged() {

		boolean result = false;

		Properties properties = loadProperties(Constant.LOGIN_FILE_NAME);
		if (null != properties) {

			String mobile = (String) properties.get(Constant.KEY_LOGIN_MOBILE);
			String password = (String) properties.get(Constant.KEY_LOGIN_PASSWORD);

			// TODO 校验密码是否正确
			if (null != mobile && 0 != mobile.length() && mobile.equalsIgnoreCase(password)) {

				result = true;
			}
		}

		return result;
	}

	/**
	 * 读取文件数据
	 * 
	 * @param fileName
	 * @return
	 */
	protected Properties loadProperties(String fileName) {

		Properties properties = null;

		try {

			File file = new File(Constant.LOGIN_FILE_PATH + fileName);
			if (file.exists()) {

				properties = new Properties();
				FileInputStream stream = this.openFileInput(fileName);
				properties.load(stream);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return properties;
	}

	/**
	 * 存文件至手机 
	 * Path: /data/data/[namespace]/files/
	 * 
	 * @param fileName 文件名
	 * @param dataMap 数据集
	 * @return
	 */
	protected boolean storeFile(String fileName, Map<String, String> dataMap) {

		boolean result = false;

		try {

			// 存储文件初始
			FileOutputStream stream = this.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
			Properties properties = new Properties();

			// 存储字段赋值
			Iterator<Entry<String, String>> it = dataMap.entrySet().iterator();
			while (it.hasNext()) {

				Entry<String, String> entry = it.next();
				String key = entry.getKey();
				String value = entry.getValue();

				properties.put(key, value);
			}

			// 
			properties.store(stream, Constant.SHUOTAO);

			// 无异常则成功
			result = true;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return result;
	}

}
