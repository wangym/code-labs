/**
 * 修改密码页
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
public class UserModifyPasswordActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnBack;
	private ImageButton btnModify;
	private EditText etOld;
	private EditText etNew;
	private EditText etRepeat;

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (!isLoggedLocal()) {
			goToActivity(UserLoginActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_modify_password);

		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goToIndex();
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
		tvTitle.setText(getString(R.string.title_user_modify_password));
		// 初始控件
		btnBack = (ImageButton) findViewById(R.id.top_v1_ib_right);
		btnBack.setVisibility(Button.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.back_btn);
		btnModify = (ImageButton) findViewById(R.id.user_modify_password_ib_modify);
		etOld = (EditText) findViewById(R.id.user_modify_password_et_old);
		etNew = (EditText) findViewById(R.id.user_modify_password_et_new);
		etRepeat = (EditText) findViewById(R.id.user_modify_password_et_repeat);

		// 返回按钮监听
		btnBack.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用返回逻辑
				doBack();
			}
		});
		// 修改密码监听
		btnModify.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用修改密码
				doModify();
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

		goToIndex();
	}

	/**
	 * 修改密码
	 */
	private void doModify() {

		// 隐藏虚拟键盘
		hideSoftInputFromWindow(etOld.getWindowToken(), etNew.getWindowToken(), etRepeat.getWindowToken());
		// 获取控件内容
		final String oldPassword = etOld.getText().toString();
		final String newPassword = etNew.getText().toString();
		final String repeatPassword = etRepeat.getText().toString();
		// 校验修改参数
		String prompt = validator.forUserModifyPassword(oldPassword, newPassword, repeatPassword);
		if (CommonUtil.isNotEmpty(prompt)) {
			showToast(prompt);
			return;
		}
		/* 修改逻辑开始 */
		progressDialog = ProgressDialog.show(this, "", getString(R.string.prompt_progress_modify), true, false);
		new Thread() {
			@Override
			public void run() {
				UserResult result = userService.modifyPassword(getUserId(), getEmail(), oldPassword, newPassword);
				if (result.isSuccess()) {
					/* 修改成功逻辑 */
					// 保存免登文件
					userDO = result.getUserDO();
					storeCookie(userDO);
					// 跳至应用主页
					goToIndex();
				} else {
					/* 修改失败逻辑 */
					progressMsg = result.getMsg();
				}
				progressHandler.sendEmptyMessage(0);
			}
		}.start();
	}

}
