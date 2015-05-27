/**
 * 
 */
package com.wulongdao.android.service;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.wulongdao.etc.CommonUtil;
import android.wulongdao.etc.HTTPUtil;
import com.wulongdao.android.domain.dataobject.UserDO;
import com.wulongdao.android.domain.enumtype.APIEnum;
import com.wulongdao.android.domain.resultobject.UserResult;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;

/**
 * @author yumin
 * 
 */
public class UserService extends BaseService {

	/**
	 * 
	 * @param userDO
	 * @return
	 */
	public Map<String, String> getJsonMap(UserDO userDO) {

		Map<String, String> map = null;

		if (null != userDO) {
			userDO.setToken(Util.getSign(userDO.getPassword(), Constant.KEY));
			map = new HashMap<String, String>();
			map.put(Constant.K_USER, userDO.toJson());
		}

		return map;
	}

	/**
	 * 
	 * @param name
	 * @param md5Password
	 * @return
	 */
	public boolean isLogged(String name, String md5Password) {

		boolean result = false;

		UserResult login = login(name, md5Password);
		if (null != login && login.isSuccess()) {
			result = true;
		}

		return result;
	}

	/**
	 * 
	 * @param name
	 * @param md5Password
	 * @return
	 */
	public UserResult login(String name, String md5Password) {

		UserResult result = new UserResult();

		try {
			String timestamp = CommonUtil.getTimestamp() + "";
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.K_NAME, name);
			map.put(Constant.K_PASSWORD, md5Password);
			map.put(Constant.K_TIMESTAMP, timestamp);
			map.put(Constant.K_SIGN, Util.getSign(md5Password, name, timestamp));
			String response = HTTPUtil.postParameters(APIEnum.USER_LOGIN.URL(), map);
			result = toUserResult(CommonUtil.toJSONObject(response));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param userId
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public UserResult modifyPassword(int userId, String email, String oldPassword, String newPassword) {

		UserResult result = new UserResult();

		try {
			String md5OldPassword = CommonUtil.MD5(oldPassword);
			String md5NewPassword = CommonUtil.MD5(newPassword);
			String timestamp = CommonUtil.getTimestamp() + "";
			Map<String, String> params = new HashMap<String, String>();
			params.put(Constant.K_USER_ID, userId + "");
			params.put(Constant.K_OLD_PASSWORD, md5OldPassword);
			params.put(Constant.K_NEW_PASSWORD, md5NewPassword);
			params.put(Constant.K_TIMESTAMP, timestamp);
			params.put(Constant.K_SIGN, Util.getSign(md5OldPassword, email, timestamp));
			String response = HTTPUtil.postParameters(APIEnum.USER_MODIFY_PASSWORD.URL(), params);
			result = toUserResult(CommonUtil.toJSONObject(response));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param nickname
	 * @param email
	 * @param password
	 * @return
	 */
	public UserResult register(String nickname, String email, String password) {

		UserResult result = new UserResult();

		try {
			String md5Password = CommonUtil.MD5(password);
			String timestamp = CommonUtil.getTimestamp() + "";
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put(Constant.K_NICKNAME, nickname);
			parameters.put(Constant.K_EMAIL, email);
			parameters.put(Constant.K_PASSWORD, md5Password);
			parameters.put(Constant.K_TIMESTAMP, timestamp);
			parameters.put(Constant.K_SIGN, Util.getSign(md5Password, email, timestamp));
			String response = HTTPUtil.postParameters(APIEnum.USER_REGISTER.URL(), parameters);
			result = toUserResult(CommonUtil.toJSONObject(response));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	private UserResult toUserResult(JSONObject json) throws JSONException {

		UserResult result = new UserResult();

		if (null != json) {
			result.setCode(json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE));
			result.setMsg(json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG));
			result.setUserDO(json.isNull(Constant.K_RESULT) ? null : UserDO.fromJSONObject(json.getJSONObject(Constant.K_RESULT)));
		}

		return result;
	}

}
