/**
 * 选择去向
 */
package me.shuotao.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author yumin
 * 
 */
public class ChooseActivity extends BaseActivity {

	// 登录按钮
	private Button loginBtn;
	// 注册按钮
	private Button registerBtn;
	// 退出按钮
	private Button exitBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*
		 * 已登录则跳转
		 */
		if (this.isLogged()) {
			goToAnyActivity(IndexActivity.class);
		}

		this.hiddenSystemTitleBar();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose);

		// 设置监听事件
		setOnClickListener();
	}

	/**
	 * 
	 */
	private void setOnClickListener() {

		// 获取按钮控件
		loginBtn = (Button) findViewById(R.id.loadingLoginButton);
		registerBtn = (Button) findViewById(R.id.loadingRegisterButton);
		exitBtn = (Button) findViewById(R.id.loadingExitButton);

		// 登录按钮监听
		loginBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用登录逻辑
				loginBtnOnClick();
			}
		});

		// 注册按钮监听
		registerBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用注册逻辑
				registerBtnOnClick();
			}
		});

		// 退出按钮监听
		exitBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用退出逻辑
				exitBtnOnClick();
			}
		});
	}

	/**
	 * 登录按钮逻辑
	 */
	private void loginBtnOnClick() {

		this.goToAnyActivity(LoginActivity.class);
	}

	/**
	 * 注册按钮逻辑
	 */
	private void registerBtnOnClick() {

		this.goToAnyActivity(RegisterStep1Activity.class);
	}

	/**
	 * 退出按钮逻辑
	 */
	private void exitBtnOnClick() {

		this.exit();
	}

}
