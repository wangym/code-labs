/**
 * 注册成功
 */
package me.shuotao.activity;

import java.util.HashMap;
import java.util.Map;
import me.shuotao.etc.Constant;
import me.shuotao.etc.Util;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author yumin
 * 
 */
public class RegisterStep3Activity extends BaseActivity {

	// 手机内容
	private TextView mobileValTV;
	// 密码内容
	private TextView passwordValTV;
	// 修改密码
	private Button modifyPasswordBtn;

	/**
	 * 
	 */
	private Bundle bundle;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/*
		 * 已登录则跳转
		 */
		if (this.isLogged()) {
			goToAnyActivity(IndexActivity.class);
		}

		this.callCustomTitleBar(R.layout.register_step_3, R.layout.titlebar);
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

		// 获取各类控件
		mobileValTV = (TextView) findViewById(R.id.registerMobileValTextView);
		passwordValTV = (TextView) findViewById(R.id.registerPasswordValTextView);
		modifyPasswordBtn = (Button) findViewById(R.id.registerModifyPasswordButton);
		// 获取按钮控件
		initTitlebarControl();

		// 接收传递参数
		bundle = getIntent().getExtras();
		String username = bundle.getString(Constant.KEY_USERNAME);
		String mobile = bundle.getString(Constant.KEY_MOBILE);
		String password = bundle.getString(Constant.KEY_PASSWORD);

		// 设置控件内容
		mobileValTV.setText(mobile);
		passwordValTV.setText(password);
		leftBtn.setVisibility(Button.INVISIBLE);
		centerTV.setText(Constant.TITLE_REGISTER_SUCCESS);
		rightBtn.setText(Constant.BTN_CONTINUE);

		// 保存注册信息
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.KEY_USERNAME, username);
		map.put(Constant.KEY_LOGIN_MOBILE, mobile);
		map.put(Constant.KEY_LOGIN_PASSWORD, password); // TODO encrypt
		map.put(Constant.KEY_LOGIN_TIME, Util.getUnixTimestamp() + "");
		// 存成本地文件
		if (!this.storeFile(Constant.LOGIN_FILE_NAME, map)) {
			// 文件保存失败
			showToast(Constant.TIP_STORE_FAILED);
		}

		bundle = new Bundle();
		bundle.putString(Constant.KEY_TYPE, "register");
		intent = new Intent();
		intent.putExtras(bundle);
	}

	/**
	 * 
	 */
	private void setOnClickListener() {

		// 完成按钮监听
		rightBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用完成逻辑
				rightBtnOnClick();
			}
		});

		// 修改密码监听
		modifyPasswordBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 修改密码逻辑
				modifyPasswordBtnOnClick();
			}
		});
	}

	/**
	 * 完成按钮逻辑
	 */
	private void rightBtnOnClick() {

		goToAnyActivity(intent, PhoneContactsActivity.class);
	}

	/**
	 * 修改密码逻辑
	 */
	private void modifyPasswordBtnOnClick() {

		goToAnyActivity(intent, ModifyPasswordActivity.class);
	}

}
