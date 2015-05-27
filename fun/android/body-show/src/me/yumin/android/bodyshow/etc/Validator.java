/**
 * 
 */
package me.yumin.android.bodyshow.etc;

import android.widget.RadioButton;

/**
 * @author yumin
 * 
 */
public class Validator {

	/**
	 * 
	 * @param mobile
	 * @param password
	 * @return
	 */
	public static String login(String mobile, String password) {

		String message = null;

		if (!Util.isNotNullAndEmpty(mobile)) {
			message = Constant.TIP_EMPTY_MOBILE;
		} else if (!Util.isMobile(mobile)) {
			message = Constant.TIP_FORMAT_MOBILE;
		} else if (!Util.isNotNullAndEmpty(password)) {
			message = Constant.TIP_EMPTY_PASSWORD;
		} else if (6 > password.length()) {
			message = Constant.TIP_FORMAT_PASSWORD;
		}

		return message;
	}

	/**
	 * 
	 * @param mobile
	 * @param password
	 * @param male
	 * @param female
	 * @return
	 */
	public static String register(String mobile, String password, RadioButton male, RadioButton female) {

		String message = null;

		message = login(mobile, password);
		if (null == message && !male.isChecked() && !female.isChecked()) {
			message = Constant.TIP_EMPTY_SEX;
		}

		return message;
	}
}
