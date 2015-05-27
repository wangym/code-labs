/**
 * 
 */
package cn.androidcloud.tao.activity;

import cn.androidcloud.tao.util.CommonUtil;
import cn.androidcloud.tao.util.TaobaoUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author WANG Yumin
 * 
 */
public class MainActivity extends Activity {

	/**
	 * 帐户名
	 */
	private EditText etUsername;

	/**
	 * 授权码
	 */
	private EditText etAuthCode;

	/**
	 * 登录
	 */
	private Button btnLogin;

	/**
	 * 重填
	 */
	private Button btnReset;

	/**
	 * 获取授权码
	 */
	private TextView tvGetAuthCodeTips;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		prepareWidget();
	}

	/**
	 * 
	 */
	private void prepareWidget() {

		etUsername = (EditText) findViewById(R.id.etUsername);
		etAuthCode = (EditText) findViewById(R.id.etAuthCode);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(btnLoginOnClick);

		btnReset = (Button) findViewById(R.id.btnReset);
		btnReset.setOnClickListener(btnResetOnClick);

		tvGetAuthCodeTips = (TextView) findViewById(R.id.tvGetAuthCodeTips);
		tvGetAuthCodeTips.setOnClickListener(tvGetAuthCodeTipsOnClick);
	}

	/**
	 * 
	 */
	private Button.OnClickListener btnLoginOnClick = new Button.OnClickListener() {

		public void onClick(View v) {

			// 表单控件数据获取
			String username = etUsername.getText().toString();
			String authCode = etAuthCode.getText().toString();

			if (CommonUtil.isNotEmpty(username)
					&& CommonUtil.isNotEmpty(authCode)) {

				Bundle bundle = new Bundle();
				bundle.putString("username", username);
				bundle.putString("sessionKey", TaobaoUtil
						.getSessionKey(authCode));

				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(MainActivity.this, MySoldActivity.class);
				startActivity(intent);
			}
		}
	};

	/**
	 * 
	 */
	private Button.OnClickListener btnResetOnClick = new Button.OnClickListener() {

		public void onClick(View v) {

			// 表单控件数据清空
			etUsername.getText().clear();
			etAuthCode.getText().clear();
		}
	};

	/**
	 * 
	 */
	private Button.OnClickListener tvGetAuthCodeTipsOnClick = new Button.OnClickListener() {

		public void onClick(View v) {

			// 打开文本标签地址
			tvGetAuthCodeTips.setMovementMethod(LinkMovementMethod
					.getInstance());
		}
	};
}
