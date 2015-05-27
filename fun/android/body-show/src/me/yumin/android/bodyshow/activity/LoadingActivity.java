/**
 * 
 */
package me.yumin.android.bodyshow.activity;

import me.yumin.android.bodyshow.etc.Constant;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

/**
 * @author yumin
 * 
 */
public class LoadingActivity extends BaseActivity {

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		// 需有扩展存储
		if (this.isMountedExternal()) {
			// 载入应用逻辑
			loading();
		} else {
			// 弹退出对话框
			this.callExitDialog(Constant.TIP_NO_SDCARD);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return true;
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void loading() {

		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 创建扩展目录
				createExternalFolder(Constant.SDCARD_FILE_PATH);
				// 跳转下一页面
				goToAnyActivity(ShowActivity.class);
			}
		}, Constant.DELAY_LOADING);
	}
}
