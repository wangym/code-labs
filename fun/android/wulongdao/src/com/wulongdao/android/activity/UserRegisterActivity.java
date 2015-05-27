/**
 * 注册新用户
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
public class UserRegisterActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnBack;
	private ImageButton btnRegister;
	private EditText etNickname;
	private EditText etEmail;
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
		setContentView(R.layout.user_register);

		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			doBack();
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
		tvTitle.setText(getString(R.string.title_user_register));
		// 初始控件
		btnBack = (ImageButton) findViewById(R.id.top_v1_ib_left);
		btnBack.setVisibility(Button.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.back_btn);
		btnRegister = (ImageButton) findViewById(R.id.user_register_ib_register);
		etNickname = (EditText) findViewById(R.id.user_register_et_nickname);
		etEmail = (EditText) findViewById(R.id.user_register_et_email);
		etPassword = (EditText) findViewById(R.id.user_register_et_password);

		// 返回按钮监听
		btnBack.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用返回逻辑
				doBack();
			}
		});
		// 注册按钮监听
		btnRegister.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用注册逻辑
				doRegister();
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

	}

	/**
	 * 返回逻辑
	 */
	private void doBack() {

		goToActivity(UserLoginActivity.class);
	}

	/**
	 * 注册逻辑
	 */
	private void doRegister() {

		// 隐藏虚拟键盘
		hideSoftInputFromWindow(etNickname.getWindowToken(), etEmail.getWindowToken(), etPassword.getWindowToken());
		// 获取控件内容
		final String nickname = etNickname.getText().toString();
		final String email = etEmail.getText().toString();
		final String password = etPassword.getText().toString();
		// 校验注册参数
		String prompt = validator.forUserRegister(nickname, email, password);
		if (CommonUtil.isNotEmpty(prompt)) {
			showToast(prompt);
			return;
		}
		/* 注册逻辑开始 */
		progressDialog = ProgressDialog.show(this, "", getString(R.string.prompt_progress_register), true, false);
		new Thread() {
			@Override
			public void run() {
				UserResult result = userService.register(nickname, email, password);
				if (result.isSuccess()) {
					/* 注册成功逻辑 */
					// 保存免登文件
					userDO = result.getUserDO();
					storeCookie(userDO);
					// 跳至应用主页
					goToIndex();
				} else {
					/* 注册失败逻辑 */
					progressMsg = result.getMsg();
				}
				progressHandler.sendEmptyMessage(0);
			}
		}.start();
	}

}
