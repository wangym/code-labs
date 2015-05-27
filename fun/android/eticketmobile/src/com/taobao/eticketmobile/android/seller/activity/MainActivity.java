/**
 * 
 */
package com.taobao.eticketmobile.android.seller.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import com.taobao.eticketmobile.android.seller.R;
import com.taobao.eticketmobile.android.seller.service.UserService;

/**
 * @author yumin
 * 
 */
public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (!UserService.isLogged()) {
			startLoginAndFinishActivity();
			return;
		}
		setContentView(R.layout.activity_main);
		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			callWhetherToExitDialog();
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void initActivity() {

	}

	/**
	 * 
	 */
	private void initData() {

	}
}
