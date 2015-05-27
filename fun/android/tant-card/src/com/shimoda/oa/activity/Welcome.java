package com.shimoda.oa.activity;

import java.io.File;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;
import com.shimoda.oa.R;
import com.shimoda.oa.util.Constants;
import com.shimoda.oa.util.EnvironmentUtil;

public class Welcome extends Activity {
	Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.welcome);
		// 启动，检查目录是否存在，检查内存情况，显示3秒后加载menu
		start();
	}

	private void start() {
		new Thread() {
			@Override
			public void run() {
				// 检查内存情况
				ActivityManager activityManager = (ActivityManager) Welcome.this
						.getSystemService(Context.ACTIVITY_SERVICE);
				ActivityManager.MemoryInfo minfo = new ActivityManager.MemoryInfo();
				activityManager.getMemoryInfo(minfo);
				long mem = minfo.availMem;
				if(mem<1024*1024*Constants.MIN_NEEDED_MEM){
					showMemToast();
				}

				// 检查目录是否存在
				String dir = null;
				if (EnvironmentUtil.hasSdCard()) {
					dir = Constants.SDCARD_DIR;
				} else {
					dir = Constants.NO_SDCARD_DIR;
				}
				File destDir = new File(dir);
				if (!destDir.exists()) {
					if (!destDir.mkdirs()) {
						// 目录创建失败，提示
						showDirCreateToast();
					}
				}

				// 延迟3秒
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.setClass(Welcome.this, MainMenu.class);
				startActivity(intent);
				finish();
			}
		}.start();
	}
	
	private void showMemToast() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(Welcome.this
						.getApplicationContext(), R.string.welcome_lack_of_mem,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}

	private void showDirCreateToast() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(Welcome.this
						.getApplicationContext(), R.string.welcome_create_dir_fail,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}
}