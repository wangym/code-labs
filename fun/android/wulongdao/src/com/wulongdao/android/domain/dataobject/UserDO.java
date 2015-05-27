/**
 * 
 */
package com.wulongdao.android.domain.dataobject;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;
import android.wulongdao.etc.CommonUtil;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;

/**
 * @author yumin
 * 
 */
public class UserDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3976833111350186540L;

	/**
	 * 
	 */
	private int userId;
	private String avatar;
	private String email;
	private String nickname;
	private String password;
	private String token;

	/**
	 * 
	 * @param object
	 * @return
	 */
	public static UserDO fromJSONObject(JSONObject object) {

		UserDO user = null;

		if (null != object) {
			user = new UserDO();
			try {
				user.setUserId(object.isNull(Constant.K_USER_ID) ? 0 : object.getInt(Constant.K_USER_ID));
				user.setAvatar(object.isNull(Constant.K_AVATAR) ? null : object.getString(Constant.K_AVATAR));
				user.setEmail(object.isNull(Constant.K_EMAIL) ? null : object.getString(Constant.K_EMAIL));
				user.setNickname(object.isNull(Constant.K_NICKNAME) ? null : object.getString(Constant.K_NICKNAME));
				user.setPassword(object.isNull(Constant.K_PASSWORD) ? null : object.getString(Constant.K_PASSWORD));
				user.setToken(object.isNull(Constant.K_TOKEN) ? null : object.getString(Constant.K_TOKEN));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return user;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static UserDO fromJSONString(String string) {

		return fromJSONObject(CommonUtil.toJSONObject(string));
	}

	/**
	 * 
	 * @return
	 */
	public String toString() {

		return toJson();
	}

	/**
	 * 
	 * @return
	 */
	public String toJson() {

		String format = "{\"user_id\":%s,\"avatar\":\"%s\",\"email\":\"%s\",\"nickname\":\"%s\",\"password\":\"%s\",\"token\":\"%s\"}";
		String json = String.format(format, userId, avatar, email, nickname, password, token);

		return json;
	}

	/**
	 * 
	 * @return
	 */
	public boolean verifyToken() {

		boolean result = false;

		if (null != token && token.equalsIgnoreCase(Util.getSign(password, Constant.KEY))) {
			result = true;
		}

		return result;
	}

	/**
	 * 
	 */
	public int getUserId() {
		return userId;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getEmail() {
		return email;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	public String getToken() {
		return token;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
