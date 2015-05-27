/**
 * 注册页面(填写名字)
 */
package me.shuotao.activity;

import me.shuotao.etc.Constant;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class RegisterStep1Activity extends BaseActivity {

	// 帐号文本
	private EditText usernameTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*
		 * 已登录则跳转
		 */
		if (this.isLogged()) {
			goToAnyActivity(IndexActivity.class);
		}

		this.callCustomTitleBar(R.layout.register_step_1, R.layout.titlebar);
		super.onCreate(savedInstanceState);

		// 控件初始设置
		init();
		// 设置监听事件
		setOnClickListener();
	}

	/**
	 * 
	 */
	private void init() {

		// 获取文本控件
		usernameTxt = (EditText) findViewById(R.id.registerUsernameEditText);
		// 获取按钮控件
		initTitlebarControl();

		// 设置按钮标题
		leftBtn.setText(Constant.BTN_BACK);
		centerTV.setText(Constant.TITLE_REGISTER);
		rightBtn.setText(Constant.BTN_CONTINUE);
	}

	/**
	 * 
	 */
	private void setOnClickListener() {

		// 左按钮监听
		leftBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用左按钮点击逻辑
				leftBtnOnClick();
			}
		});

		// 右按钮监听
		rightBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用右按钮点击逻辑
				rightBtnOnClick();
			}
		});
	}

	/**
	 * 左按钮点击逻辑
	 */
	private void leftBtnOnClick() {

		goToAnyActivity(ChooseActivity.class);
	}

	/**
	 * 右按钮点击逻辑
	 */
	private void rightBtnOnClick() {

		// 获取输入内容
		String username = usernameTxt.getText().toString();

		// 校验变量格式
		if (null == username || 0 == username.length()) {
			usernameTxt.setHint(Constant.TIPS_USERNAME);
			return;
		}

		/*
		 * 输入正确处理:
		 */
		Bundle bundle = new Bundle();
		bundle.putString(Constant.KEY_USERNAME, username);
		Intent intent = new Intent();
		intent.putExtras(bundle);

		// 输入正确跳转
		goToAnyActivity(intent, RegisterStep2Activity.class);
	}

}
