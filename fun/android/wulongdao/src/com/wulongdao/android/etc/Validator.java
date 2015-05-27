/**
 * 
 */
package com.wulongdao.android.etc;

import com.wulongdao.android.activity.R;
import android.content.Context;
import android.content.res.Resources;
import android.wulongdao.etc.CommonUtil;

/**
 * @author yumin
 * 
 */
public class Validator {

	/**
	 * 
	 */
	private Resources res;

	/**
	 * @param context
	 */
	public Validator(Context context) {
		res = context.getResources();
	}

	// ====================
	// solution methods
	// ====================

	/**
	 * 
	 * @param nickname
	 * @param email
	 * @param password
	 * @return
	 */
	public String forUserRegister(String nickname, String email, String password) {

		String prompt = null;

		// 校验昵称
		if (null != (prompt = forNickname(nickname))) {
			return prompt;
		}
		// 校验邮箱
		if (null != (prompt = forEmail(email))) {
			return prompt;
		}
		// 校验密码
		if (null != (prompt = forPassword(password))) {
			return prompt;
		}

		return prompt;
	}

	/**
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	public String forUserLogin(String name, String password) {

		String prompt = null;

		// 校验帐号
		if (null != (prompt = forName(name))) {
			return prompt;
		}
		// 校验密码
		if (null != (prompt = forPassword(password))) {
			return prompt;
		}

		return prompt;
	}

	/**
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param repeatPassword
	 * @return
	 */
	public String forUserModifyPassword(String oldPassword, String newPassword, String repeatPassword) {

		String prompt = null;

		// 校验密码
		if (null != (prompt = forPassword(oldPassword))) {
			return prompt;
		}
		if (null != (prompt = forPassword(newPassword))) {
			return prompt;
		}
		if (null != (prompt = forPassword(repeatPassword))) {
			return prompt;
		}

		if (oldPassword.equalsIgnoreCase(newPassword)) {
			return res.getString(R.string.prompt_password_old_new);
		}
		if (!newPassword.equalsIgnoreCase(repeatPassword)) {
			return res.getString(R.string.prompt_password_new_repeat);
		}

		return prompt;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public String forQuestionPost(String message) {

		String prompt = null;

		// 校验内容
		if (null != (prompt = forMessage(message))) {
			return prompt;
		}

		return prompt;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public String forCommentPost(String message) {

		String prompt = null;

		// 校验内容
		if (null != (prompt = forMessage(message))) {
			return prompt;
		}

		return prompt;
	}

	// ====================
	// overhaul methods
	// ====================

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String forName(String name) {

		if (!CommonUtil.isNotEmpty(name)) {
			return res.getString(R.string.prompt_name_empty);
		}

		return null;
	}

	/**
	 * 
	 * @param nickname
	 * @return
	 */
	public String forNickname(String nickname) {

		if (!CommonUtil.isNotEmpty(nickname)) {
			return res.getString(R.string.prompt_nickname_empty);
		} else if (!CommonUtil.isBetweenLength(nickname, 4, 10)) {
			return res.getString(R.string.prompt_nickname_format);
		}

		return null;
	}

	/**
	 * 
	 * @param email
	 * @return
	 */
	public String forEmail(String email) {

		if (!CommonUtil.isNotEmpty(email)) {
			return res.getString(R.string.prompt_email_empty);
		} else if (!CommonUtil.isEmail(email)) {
			return res.getString(R.string.prompt_email_format);
		}

		return null;
	}

	/**
	 * 
	 * @param password
	 * @return
	 */
	public String forPassword(String password) {

		if (!CommonUtil.isNotEmpty(password)) {
			return res.getString(R.string.prompt_password_empty);
		} else if (!CommonUtil.isBetweenLength(password, 6, 16)) {
			return res.getString(R.string.prompt_password_format);
		}

		return null;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public String forMessage(String message) {

		if (!CommonUtil.isNotEmpty(message)) {
			return res.getString(R.string.prompt_message_empty);
		} else if (!CommonUtil.isBetweenLength(message, 1, 250)) {
			return res.getString(R.string.prompt_message_format);
		}

		return null;
	}

}
