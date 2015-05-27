/**
 * 
 */
package com.taobao.eticketmobile.android.seller.service;

import me.yumin.android.common.thirdparty.RSAUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.AutoLoginV2ApiResult;
import com.taobao.eticketmobile.android.common.api.domain.api.result.GetAppTokenApiResult;
import com.taobao.eticketmobile.android.common.api.domain.api.result.LoginV2ApiResult;
import com.taobao.eticketmobile.android.common.api.domain.param.AutoLoginV2ApiParam;
import com.taobao.eticketmobile.android.common.api.domain.param.GetAppTokenApiParam;
import com.taobao.eticketmobile.android.common.api.domain.param.LoginV2ApiParam;
import com.taobao.eticketmobile.android.common.api.top.AutoLoginV2Api;
import com.taobao.eticketmobile.android.common.api.top.GetAppTokenApi;
import com.taobao.eticketmobile.android.common.api.top.LoginV2Api;
import com.taobao.eticketmobile.android.seller.dao.UserDAO;
import com.taobao.eticketmobile.android.seller.domain.inputobject.LoginInput;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.etc.GlobalVar;

/**
 * @author yumin
 * 
 */
public class UserService {

	/**
	 * 账户登录
	 * 
	 * @param input
	 */
	public static LoginV2ApiResult login(LoginInput input) {

		LoginV2ApiResult loginV2ApiResult = null;

		if (null != input) {
			try {
				// 登录前的操作
				String username = input.getUsername();
				GetAppTokenApiParam getAppTokenApiParam = new GetAppTokenApiParam();
				getAppTokenApiParam.setUsername(username);
				GetAppTokenApiResult getAppTokenApiResult = GetAppTokenApi.request(getAppTokenApiParam);
				if (null != getAppTokenApiResult && getAppTokenApiResult.isSuccess()) {
					RSAUtil.pubKey = RSAUtil.generateRSAPublicKey(getAppTokenApiResult.getPubKey());
					String rsaPassword = new String(RSAUtil.encrypt(input.getPassword()));
					// 执行登录操作
					LoginV2ApiParam loginV2ApiParam = new LoginV2ApiParam();
					loginV2ApiParam.setAppKey(Constant.APP_KEY);
					loginV2ApiParam.setAppSecret(Constant.APP_SECRET);
					loginV2ApiParam.setRsaPassword(rsaPassword);
					loginV2ApiParam.setToken(getAppTokenApiResult.getToken());
					loginV2ApiParam.setTtid(Constant.TTID);
					loginV2ApiParam.setUsername(username);
					loginV2ApiResult = LoginV2Api.request(loginV2ApiParam);
					if (null != loginV2ApiResult && loginV2ApiResult.isSuccess()) {
						// 登录成功后续
						UserDAO.saveLogin(loginV2ApiResult);
						GlobalVar.logged = true;
						GlobalVar.sid = loginV2ApiResult.getSid();
						GlobalVar.userId = loginV2ApiResult.getUserId();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return loginV2ApiResult;
	}

	/**
	 * 自动登录
	 * 
	 * @param loginV2ApiResult
	 */
	public static AutoLoginV2ApiResult autoLogin(LoginV2ApiResult loginV2ApiResult) {

		AutoLoginV2ApiResult autoLoginV2ApiResult = null;

		if (null != loginV2ApiResult) {
			try {
				AutoLoginV2ApiParam autoLoginV2ApiParam = new AutoLoginV2ApiParam();
				autoLoginV2ApiParam.setAppKey(Constant.APP_KEY);
				autoLoginV2ApiParam.setAppSecret(Constant.APP_SECRET);
				autoLoginV2ApiParam.setToken(loginV2ApiResult.getToken());
				autoLoginV2ApiParam.setTtid(Constant.TTID);
				autoLoginV2ApiParam.setUsername(loginV2ApiResult.getUsername());
				autoLoginV2ApiResult = AutoLoginV2Api.request(autoLoginV2ApiParam);
				if (null != autoLoginV2ApiResult && autoLoginV2ApiResult.isSuccess()) {
					// 登录成功后续
					loginV2ApiResult.setEcode(autoLoginV2ApiResult.getEcode());
					loginV2ApiResult.setLogintime(autoLoginV2ApiResult.getLogintime());
					loginV2ApiResult.setSid(autoLoginV2ApiResult.getSid());
					UserDAO.saveLogin(loginV2ApiResult);
					GlobalVar.logged = true;
					GlobalVar.sid = autoLoginV2ApiResult.getSid();
					GlobalVar.userId = autoLoginV2ApiResult.getUserId();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return autoLoginV2ApiResult;
	}

	/**
	 * 销毁所有
	 * 
	 * @return
	 */
	public static boolean destroy() {

		boolean result = false;

		// 全局变量复原
		GlobalVar.logged = false;
		GlobalVar.sid = "";
		GlobalVar.userId = "";
		// 已存文件清空
		result = UserDAO.clearLogin();

		return result;
	}

	/**
	 * 登录结果
	 * 
	 * @return
	 */
	public static LoginV2ApiResult getLoginResult() {

		LoginV2ApiResult result = UserDAO.getLogin();

		return result;
	}

	/**
	 * 是否登录(加强版,应用首页可用此,其它页请用全局变量)
	 * 
	 * @return
	 */
	public static boolean isLogged() {

		boolean result = false;

		LoginV2ApiResult loginV2ApiResult = UserDAO.getLogin();
		if (null != loginV2ApiResult && loginV2ApiResult.isSuccess()) {
			result = true;
			GlobalVar.logged = true;
			GlobalVar.sid = loginV2ApiResult.getSid();
			GlobalVar.userId = loginV2ApiResult.getUserId();
		}

		return result;
	}
}
