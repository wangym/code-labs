/**
 * 载入启动页
 */
package com.wulongdao.android.activity;

import com.wulongdao.android.etc.Constant;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.wulongdao.etc.CommonUtil;

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

		// 判断扩展存储
		if (isMounted()) {
			loading();
		} else {
			callMustExitDialog(getString(R.string.prompt_no_sdcard));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 屏蔽按键功能
		return true;
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void loading() {

		/**
		 * 
		 */
		new Handler().postDelayed(new Runnable() {

			/**
			 * 
			 */
			public void run() {

				// 是否已经联网
				if (!isActiveNetwork()) {
					showToast(getString(R.string.prompt_no_network));
				}
				// 创建扩展目录
				CommonUtil.createFolders(Constant.PATH_SDCARD);
				// 是否可以免登
				if (isLoggedNetwork()) {
					// 可以免登
					if (CommonUtil.isNotEmpty(getAvatar())) {
						goToIndex();
					} else {
						goToActivity(UserUploadAvatarActivity.class);
					}
				} else {
					// 不能免登
					goToActivity(UserLoginActivity.class);
				}
			}

		}, Constant.DELAY_LOADING);
	}

}
