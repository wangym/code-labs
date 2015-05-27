/**
 * 注册
 */
package me.yumin.android.bodyshow.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import me.yumin.android.bodyshow.etc.Constant;
import me.yumin.android.bodyshow.etc.MD5;
import me.yumin.android.bodyshow.etc.Util;
import me.yumin.android.bodyshow.etc.Validator;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author yumin
 * 
 */
public class RegisterActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnBack;
	private EditText etMobile;
	private EditText etPassword;
	private RadioButton rbMale;
	private RadioButton rbFemale;
	private ImageButton btnRegister;

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (this.isLogged()) {
			this.goToAnyActivity(ShowActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		initWidget();
		initListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			this.goToAnyActivity(LoginActivity.class);
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
	private void initWidget() {

		// 头部标题
		TextView tvTitle = (TextView) findViewById(R.id.topV1TvCenter);
		tvTitle.setText(Constant.TIP_TITLE_REGISTER);
		// 左侧返回
		btnBack = (ImageButton) findViewById(R.id.topV1ImgBtnLeft);
		btnBack.setVisibility(Button.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.back_btn);
		// 手机输入
		etMobile = (EditText) findViewById(R.id.registerEtMobile);
		etMobile.setInputType(InputType.TYPE_CLASS_PHONE);
		// 密码输入
		etPassword = (EditText) findViewById(R.id.registerEtPassword);
		// 性别选择
		rbMale = (RadioButton) findViewById(R.id.registerRbMale);
		rbFemale = (RadioButton) findViewById(R.id.registerRbFemale);
		// 注册按钮
		btnRegister = (ImageButton) findViewById(R.id.registerImgBtnRegister);
	}

	/**
	 * 
	 */
	private void initListener() {

		// 返回按钮监听
		btnBack.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用返回逻辑
				btnBackOnClick();
			}
		});
		// 注册按钮监听
		btnRegister.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用注册逻辑
				btnRegisterOnClick();
			}
		});
	}

	/**
	 * 返回按钮逻辑
	 */
	private void btnBackOnClick() {

		this.goToAnyActivity(LoginActivity.class);
	}

	/**
	 * 注册按钮逻辑
	 */
	private void btnRegisterOnClick() {

		// 获取控件内容
		String mobile = etMobile.getText().toString();
		String password = etPassword.getText().toString();
		// 验证内容输入
		String message = Validator.register(mobile, password, rbMale, rbFemale);
		if (Util.isNotNullAndEmpty(message)) {
			showToast(message);
			return;
		}
		// 参数最终整理
		password = MD5.hash(password);
		int sex = (rbMale.isChecked() ? Constant.VAL_SEX_MALE : rbFemale.isChecked() ? Constant.VAL_SEX_FEMALE : Constant.VAL_SEX_UNKOWN);

		// 隐藏虚拟键盘
		this.hideSoftInputFromWindow(etMobile.getWindowToken());
		this.hideSoftInputFromWindow(etPassword.getWindowToken());

		// 最终参数组合
		final Map<String, String> params = new HashMap<String, String>();
		params.put(Constant.KEY_LOGIN_SIGN, Util.getSign(mobile + password + sex));
		params.put(Constant.KEY_LOGIN_TIME, Util.getCurrentTimestamp() + "");
		params.put(Constant.KEY_LOGIN_MOBILE, mobile);
		params.put(Constant.KEY_LOGIN_PASSWORD, password);
		params.put(Constant.KEY_LOGIN_SEX, sex + "");
		params.put(Constant.KEY_LOGIN_LOG, Util.getLoginLog());

		//
		progressDialog = ProgressDialog.show(this, "", Constant.TIP_PROGRESS_REGISTER, true, false);
		new Thread() {
			@Override
			public void run() {
				// 调用前初始化
				int code = 0;
				progressTips = Constant.TIP_UNKOWN_ERROR;
				// 调用接口注册
				try {
					/**
					 * 执行注册命令
					 */
					String url = Util.getAPIUrl(Constant.ENV, "BODY_SHOW_REGISTER");
					String result = Util.httpPost(url, params);
					/**
					 * 获取注册结果
					 */
					// 有且是JSON
					if (null != result && result.startsWith("{")) {
						JSONObject json = new JSONObject(result);
						if (null != json) {
							code = (json.isNull("code") ? 0 : json.getInt("code"));
							progressTips = (json.isNull("message") ? "" : json.getString("message"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				/**
				 * 注册完成逻辑
				 */
				if (200 == code) {
					// 保存注册信息
					params.remove(Constant.KEY_LOGIN_SEX);
					params.remove(Constant.KEY_LOGIN_LOG);
					params.put(Constant.KEY_LOGIN_SIGN, Util.getLoginSign(params));
					storeLocalFile(Constant.LOGIN_FILE_NAME, params);
					// 跳转至首页面
					goToAnyActivity(ShowActivity.class);
				} else {
					//
					progressHandler.sendEmptyMessage(0);
				}
			}
		}.start();
	}
}
