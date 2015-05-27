/**
 * 
 */
package me.yumin.android.yaya.activity;

import me.yumin.android.common.etc.ViewUtil;
import me.yumin.android.yaya.R;
import me.yumin.android.yaya.asynctask.WelcomeAsyncTask;
import me.yumin.android.yaya.etc.Constant;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

/**
 * @author yumin
 * 
 */
public class WelcomeActivity extends BaseActivity implements Handler.Callback {

	/**
     *
     */
	private Handler handler;
	private WelcomeAsyncTask welcomeAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initActivity();
		initData();
	}

	@Override
	protected void onDestroy() {

		if (null != handler) {
			handler = null;
		}
		if (null != welcomeAsyncTask) {
			if (!welcomeAsyncTask.isCancelled()) {
				welcomeAsyncTask.cancel(true);
			}
			welcomeAsyncTask = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		return true;
	}

	@Override
	public boolean handleMessage(Message msg) {

		if (null != msg) {
			switch (msg.what) {
			case Constant.EXCEPTION:
				startLoginAndFinishActivity();
				break;
			case Constant.WELCOME_TO_MAIN:
				startMainAndFinishActivity();
				break;
			case Constant.WELCOME_TO_LOGIN:
				startLoginAndFinishActivity();
				break;
			case Constant.NETWORK_UNAVAILABLE:
				ViewUtil.showToast(getToastNetworkUnavailable());
				startMainAndFinishActivity();
				break;
			default:
				return false;
			}
		}

		return true;
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void initActivity() {

		handler = new Handler(this);
		welcomeAsyncTask = new WelcomeAsyncTask(handler);
		welcomeAsyncTask.execute();
	}

	/**
	 * 
	 */
	private void initData() {

	}
}
