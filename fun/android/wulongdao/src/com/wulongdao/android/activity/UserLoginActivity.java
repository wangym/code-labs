/**
 * 登录乌龙岛
 */
package com.wulongdao.android.activity;

import com.wulongdao.android.domain.resultobject.UserResult;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.wulongdao.etc.CommonUtil;

/**
 * @author yumin
 * 
 */
public class UserLoginActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnRegister;
	private ImageButton btnLogin;
	private EditText etName;
	private EditText etPassword;

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (isLoggedLocal()) {
			goToIndex();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);

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

		// 顶部标题
		TextView tvTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		tvTitle.setText(getString(R.string.title_user_login));
		// 初始控件
		btnRegister = (ImageButton) findViewById(R.id.top_v1_ib_left);
		btnRegister.setVisibility(Button.VISIBLE);
		btnRegister.setBackgroundResource(R.drawable.common_button_bg);
		btnRegister.setImageResource(R.drawable.back_btn_icon);
		btnLogin = (ImageButton) findViewById(R.id.top_v1_ib_right);
		btnLogin.setVisibility(Button.VISIBLE);
		btnLogin.setBackgroundResource(R.drawable.common_button_bg);
		btnLogin.setImageResource(R.drawable.ok_btn_icon);
		etName = (EditText) findViewById(R.id.user_login_et_name);
		etPassword = (EditText) findViewById(R.id.user_login_et_password);

		// 注册按钮监听
		btnRegister.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用注册逻辑
				doRegister();
			}
		});
		// 登录按钮监听
		btnLogin.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用登录逻辑
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
	 * 注册逻辑
	 */
	private void doRegister() {

		goToActivity(UserRegisterActivity.class);
	}

	/**
	 * 登录逻辑
	 */
	private void doLogin() {

		// 隐藏虚拟键盘
		hideSoftInputFromWindow(etName.getWindowToken(), etPassword.getWindowToken());
		// 获取控件内容
		final String name = etName.getText().toString();
		final String password = etPassword.getText().toString();
		// 校验登录参数
		String prompt = validator.forUserLogin(name, password);
		if (CommonUtil.isNotEmpty(prompt)) {
			showToast(prompt);
			return;
		}
		//
		progressDialog = ProgressDialog.show(this, "", getString(R.string.prompt_progress_login), true, false);
		new Thread() {
			@Override
			public void run() {
				UserResult result = userService.login(name, CommonUtil.MD5(password));
				if (result.isSuccess()) {
					/* 登录成功逻辑 */
					userDO = result.getUserDO();
					// 保存免登文件
					storeCookie(userDO);
					// 转至应用首页
					goToIndex();
				} else {
					/* 登录失败逻辑 */
					progressMsg = result.getMsg();
				}
				progressHandler.sendEmptyMessage(0);
			}
		}.start();
	}

}
