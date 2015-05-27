/**
 * 
 */
package com.taobao.eticketmobile.android.seller.activity;

import me.yumin.android.common.etc.ViewUtil;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.taobao.eticketmobile.android.common.api.domain.api.result.LoginV2ApiResult;
import com.taobao.eticketmobile.android.seller.R;
import com.taobao.eticketmobile.android.seller.asynctask.LoginAsyncTask;
import com.taobao.eticketmobile.android.seller.domain.inputobject.LoginInput;
import com.taobao.eticketmobile.android.seller.etc.Constant;

/**
 * @author yumin
 * 
 */
public class LoginActivity extends BaseActivity implements Handler.Callback {

	/**
     *
     */
	private ProgressDialog progressDialog;
	private Handler handler;
	private LoginAsyncTask loginAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initActivity();
		initData();
	}

	@Override
	protected void onDestroy() {

		if (null != handler) {
			handler = null;
		}
		if (null != loginAsyncTask) {
			if (!loginAsyncTask.isCancelled()) {
				loginAsyncTask.cancel(true);
			}
			loginAsyncTask = null;
		}
		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		super.onDestroy();
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

	@Override
	public boolean handleMessage(Message msg) {

		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (null != msg) {
			// 对象转换
			LoginV2ApiResult apiResult = (LoginV2ApiResult) msg.obj;
			// 消息处理
			switch (msg.what) {
			case Constant.EXCEPTION:
				ViewUtil.showToast(getToastException());
				break;
			case Constant.LOGIN_SUCCESS:
				startMainAndFinishActivity();
				break;
			case Constant.LOGIN_FAIL:
				ViewUtil.showToast(getToastFail(apiResult));
				break;
			case Constant.NETWORK_UNAVAILABLE:
				ViewUtil.showToast(getToastNetworkUnavailable());
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
		// 设置标题
		TextView tvTopTitle = (TextView) findViewById(R.id.tv_include_top_title);
		tvTopTitle.setText(R.string.title_login);
		// 初始控件
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				doLogin();
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

	}

	/**
	 * 
	 */
	private void doLogin() {

		// 校验参数
		LoginInput input = new LoginInput(this);
		String toast = input.validate();
		if (null != toast) {
			ViewUtil.showToast(toast);
			return;
		}
		// 执行登录
		progressDialog = ProgressDialog.show(this, "", getString(R.string.phrase_login_ing), true, false);
		loginAsyncTask = new LoginAsyncTask(handler);
		loginAsyncTask.execute(input);
	}
}
