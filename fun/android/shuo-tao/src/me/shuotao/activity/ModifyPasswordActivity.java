/**
 * 修改密码
 */
package me.shuotao.activity;

import me.shuotao.etc.Constant;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author yumin
 * 
 */
public class ModifyPasswordActivity extends BaseActivity {

	/**
	 * 
	 */
	private Bundle bundle;
	private Intent intent;

	/**
	 * 
	 */
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		this.callCustomTitleBar(R.layout.modify_password, R.layout.titlebar);
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

		// 获取按钮控件
		initTitlebarControl();

		// 设置控件内容
		leftBtn.setVisibility(Button.INVISIBLE);
		centerTV.setText(Constant.TITLE_MODIFY_PASSWORD);
		rightBtn.setText(Constant.BTN_CONTINUE);

		// 接收传递参数
		bundle = getIntent().getExtras();
		type = bundle.getString(Constant.KEY_TYPE);
		intent = new Intent();
		intent.putExtras(bundle);
	}

	/**
	 * 
	 */
	private void setOnClickListener() {

		// 继续按钮监听
		rightBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用继续逻辑
				rightBtnOnClick();
			}
		});
	}

	/**
	 * 继续按钮逻辑
	 */
	private void rightBtnOnClick() {

		if (Constant.KEY_REGISTER.equalsIgnoreCase(type)) {
			goToAnyActivity(intent, PhoneContactsActivity.class);
		}
	}

}
