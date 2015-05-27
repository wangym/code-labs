/**
 * 登录页面
 */
package me.shuotao.activity;

import java.util.HashMap;
import java.util.Map;
import me.shuotao.etc.Constant;
import me.shuotao.etc.Util;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class LoginActivity extends BaseActivity {

	// 手机文本
	private EditText mobileTxt;
	// 密码文本
	private EditText passwordTxt;
	// 登录按钮
	private Button loginBtn;
	// 注册按钮
	private Button registerBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*
		 * 已登录则跳转
		 */
		if (this.isLogged()) {
			goToAnyActivity(IndexActivity.class);
		}

		this.callCustomTitleBar(R.layout.login, R.layout.titlebar);
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
		mobileTxt = (EditText) findViewById(R.id.loginMobileEditText);
		passwordTxt = (EditText) findViewById(R.id.loginPasswordEditText);
		// 获取按钮控件
		loginBtn = (Button) findViewById(R.id.loginLoginButton);
		registerBtn = (Button) findViewById(R.id.loginRegisterButton);
		initTitlebarControl();

		// 手机号码键盘
		mobileTxt.setInputType(InputType.TYPE_CLASS_PHONE);
		// 设置按钮标题
		leftBtn.setText(Constant.BTN_BACK);
		centerTV.setText(Constant.TITLE_LOGIN);
		rightBtn.setVisibility(Button.INVISIBLE);
	}

	/**
	 * 
	 */
	private void setOnClickListener() {

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

		// 左按钮监听
		leftBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用左按钮点击逻辑
				leftBtnOnClick();
			}
		});
	}

	/**
	 * 登录按钮逻辑
	 */
	private void loginBtnOnClick() {

		// 默认变量定义
		String mobile = mobileTxt.getText().toString();
		String password = passwordTxt.getText().toString();

		// 校验变量格式
		if (null == mobile || 0 == mobile.length()) {
			mobileTxt.setHint(Constant.TIPS_MOBILE);
			return;
		}
		if (null == password || 0 == password.length()) {
			passwordTxt.setHint(Constant.TIPS_PASSWORD);
			return;
		}

		// TODO 改调会员验证
		boolean result = (mobile.equalsIgnoreCase(password) ? true : false);

		/*
		 * 登录失败处理:
		 */
		if (!result) {

			// 清密码框内容
			passwordTxt.getText().clear();
			// 失败提示信息
			showToast(Constant.TIP_LOGIN_FAILED);
			return;
		}

		/*
		 * 登录成功处理:
		 */
		// 保存登录信息
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.KEY_LOGIN_MOBILE, mobile);
		map.put(Constant.KEY_LOGIN_PASSWORD, password); // TODO encrypt
		map.put(Constant.KEY_LOGIN_TIME, Util.getUnixTimestamp() + "");
		// 存成本地文件
		if (!this.storeFile(Constant.LOGIN_FILE_NAME, map)) {

			// 文件保存失败
			showToast(Constant.TIP_STORE_FAILED);
			return;
		}

		// 跳转个人首页
		goToAnyActivity(IndexActivity.class);
	}

	/**
	 * 注册按钮逻辑
	 */
	private void registerBtnOnClick() {

		goToAnyActivity(RegisterStep1Activity.class);
	}

	/**
	 * 左按钮点击逻辑
	 */
	private void leftBtnOnClick() {

		goToAnyActivity(ChooseActivity.class);
	}

}
