/**
 * 
 */
package com.taobao.eticketmobile.android.seller.domain.inputobject;

import java.io.Serializable;
import me.yumin.android.common.etc.CommonUtil;
import com.taobao.eticketmobile.android.seller.R;
import android.app.Activity;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class LoginInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5419195151510241576L;

	/**
	 * 
	 */
	private Activity activity;

	/**
	 * 
	 */
	private String password;
	private String username;

	/**
	 * 初始输入对象
	 * 
	 * @param activity
	 */
	public LoginInput(Activity activity) {

		if (null != activity) {
			// set attribute
			this.activity = activity;
			// set parameters
			EditText etPassword = (EditText) activity.findViewById(R.id.et_login_password);
			password = (null != etPassword ? etPassword.getText().toString(): null);
			EditText etUsername = (EditText) activity.findViewById(R.id.et_login_username);
			username = (null != etUsername ? etUsername.getText().toString(): null);
		}
	}

	/**
	 * 校验输入对象
	 * 
	 * @return null=校验正确|not=提示失败
	 */
	public String validate() {

		String toast = null;

		if (!CommonUtil.isNotEmpty(password)) {
			toast = activity.getString(R.string.toast_login_input_password);
		} else if (!CommonUtil.isNotEmpty(username)) {
			toast = activity.getString(R.string.toast_login_input_username);
		}

		return toast;
	}

	/**
	 * 
	 */
	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
}
