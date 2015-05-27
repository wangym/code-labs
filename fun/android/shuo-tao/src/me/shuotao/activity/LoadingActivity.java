/**
 * 欢迎页面
 */
package me.shuotao.activity;

import me.shuotao.etc.Constant;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author yumin
 * 
 */
public class LoadingActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO DEBUG
		this.delFile(Constant.LOGIN_FILE_NAME);

		this.hiddenSystemStatusBar();
		this.hiddenSystemTitleBar();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		// 延迟加载
		loading();
	}

	/**
	 * 
	 */
	private void loading() {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				// 页面跳转
				goToAnyActivity(ChooseActivity.class);
			}

		}, Constant.DELAY_LOADING); // 延迟秒数
	}

}
