/**
 * 
 */
package com.taobao.eticketmobile.android.seller.asynctask;

import me.yumin.android.common.etc.NetworkUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.LoginV2ApiResult;
import com.taobao.eticketmobile.android.seller.domain.inputobject.LoginInput;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.service.UserService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * @author yumin
 * 
 */
public class LoginAsyncTask extends AsyncTask<LoginInput, Void, LoginV2ApiResult> {

	/**
	 * 
	 */
	private Handler handler;

	/**
	 * 
	 */
	public LoginAsyncTask(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected LoginV2ApiResult doInBackground(LoginInput... params) {

		// 执行登录
		LoginV2ApiResult loginV2ApiResult = UserService.login(params[0]);

		return loginV2ApiResult;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(LoginV2ApiResult loginV2ApiResult) {

		super.onPostExecute(loginV2ApiResult);
		int what = Constant.EXCEPTION;
		// 判断网络
		if (NetworkUtil.isNetworkConnected()) {
			if (null != loginV2ApiResult) {
				what = loginV2ApiResult.isSuccess() ? Constant.LOGIN_SUCCESS : Constant.LOGIN_FAIL;
			}
		} else {
			what = Constant.NETWORK_UNAVAILABLE;
		}
		// 生成消息
		Message message = handler.obtainMessage(what, loginV2ApiResult);
		// 发送通信
		handler.sendMessage(message);
		handler = null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
