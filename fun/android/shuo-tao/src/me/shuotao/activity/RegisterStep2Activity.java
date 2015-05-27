/**
 * 注册页面(填手机号)
 */
package me.shuotao.activity;

import me.shuotao.etc.Constant;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class RegisterStep2Activity extends BaseActivity {

	// 手机文本
	private EditText mobileTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*
		 * 已登录则跳转
		 */
		if (this.isLogged()) {
			goToAnyActivity(IndexActivity.class);
		}

		this.callCustomTitleBar(R.layout.register_step_2, R.layout.titlebar);
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
		mobileTxt = (EditText) findViewById(R.id.registerMobileEditText);
		// 获取按钮控件
		initTitlebarControl();

		// 手机号码键盘
		mobileTxt.setInputType(InputType.TYPE_CLASS_PHONE);
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

		goToAnyActivity(RegisterStep1Activity.class);
	}

	/**
	 * 右按钮点击逻辑
	 */
	private void rightBtnOnClick() {

		// 默认变量定义
		String mobile = mobileTxt.getText().toString();

		// 校验变量格式
		if (null == mobile || 0 == mobile.length()) {
			mobileTxt.setHint(Constant.TIPS_MOBILE);
			return;
		}

		// TODO:改调注册接口
		boolean result = (11 == mobile.length() ? true : false);
		String password = mobile;

		/*
		 * 登录失败处理:
		 */
		if (!result) {

			// 清密码框内容
			mobileTxt.getText().clear();
			// 失败提示信息
			showToast(Constant.TIP_REGISTER_FAILED);
			return;
		}

		/*
		 * 登录成功处理:
		 */
		Bundle bundle = getIntent().getExtras();
		String username = bundle.getString(Constant.KEY_USERNAME);
		bundle.putString(Constant.KEY_USERNAME, username);
		bundle.putString(Constant.KEY_MOBILE, mobile);
		bundle.putString(Constant.KEY_PASSWORD, password);
		Intent intent = new Intent();
		intent.putExtras(bundle);

		// 跳转登录成功
		goToAnyActivity(intent, RegisterStep3Activity.class);
	}

}
