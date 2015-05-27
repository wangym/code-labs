/**
 * 
 */
package com.taobao.eticketmobile.android.seller.dao;

import me.yumin.android.common.etc.CommonUtil;
import me.yumin.android.common.etc.StorageUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.LoginV2ApiResult;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author yumin
 * 
 */
public class UserDAO {

	/**
	 * 
	 * @return
	 */
	public static boolean clearLogin() {

		boolean result = false;

		SharedPreferences sharedPreferences = StorageUtil.getSharedPreferences(Constant.K_LOGIN);
		if (null != sharedPreferences) {
			Editor editor = sharedPreferences.edit();
			editor.clear();
			editor.commit();
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public static LoginV2ApiResult getLogin() {

		LoginV2ApiResult result = null;

		try {
			SharedPreferences sharedPreferences = StorageUtil.getSharedPreferences(Constant.K_LOGIN);
			if (null != sharedPreferences) {
				String login = sharedPreferences.getString(Constant.K_LOGIN, null);
				if (CommonUtil.isNotEmpty(login)) {
					byte[] base64Bytes = login.getBytes();
					result = (LoginV2ApiResult) CommonUtil.getBase64InputStream(base64Bytes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param loginV2Result
	 * @return
	 */
	public static boolean saveLogin(LoginV2ApiResult loginV2Result) {

		boolean result = false;

		if (null != loginV2Result) {
			try {
				SharedPreferences sharedPreferences = StorageUtil.getSharedPreferences(Constant.K_LOGIN);
				if (null != sharedPreferences) {
					Editor editor = sharedPreferences.edit();
					editor.clear();
					byte[] base64Bytes = CommonUtil.getBase64OutputStream(loginV2Result);
					editor.putString(Constant.K_LOGIN, new String(base64Bytes));
					result = editor.commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}
