/**
 * 
 */
package com.taobao.eticketmobile.android.seller.asynctask;

import me.yumin.android.common.etc.CommonUtil;
import me.yumin.android.common.etc.NetworkUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.AutoLoginV2ApiResult;
import com.taobao.eticketmobile.android.common.api.domain.api.result.LoginV2ApiResult;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.service.UserService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * @author yumin
 * 
 */
public class WelcomeAsyncTask extends AsyncTask<Void, Void, AutoLoginV2ApiResult> {

	/**
	 * 
	 */
	private Handler handler;
	private long startTime;

	/**
	 * 
	 */
	public WelcomeAsyncTask(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();
		startTime = CommonUtil.getTimestamp();
	}

	@Override
	protected AutoLoginV2ApiResult doInBackground(Void... params) {

		// 获取登录
		LoginV2ApiResult loginV2ApiResult = UserService.getLoginResult();
		// 自动登录
		AutoLoginV2ApiResult autoLoginV2ApiResult = UserService.autoLogin(loginV2ApiResult);

		return autoLoginV2ApiResult;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(AutoLoginV2ApiResult autoLoginV2ApiResult) {

		super.onPostExecute(autoLoginV2ApiResult);
		int what = Constant.EXCEPTION;
		// 判断网络
		if (NetworkUtil.isNetworkConnected()) {
			if (null != autoLoginV2ApiResult) {
				what = autoLoginV2ApiResult.isSuccess() ? Constant.WELCOME_TO_MAIN : Constant.WELCOME_TO_LOGIN;
			}
		} else {
			what = Constant.NETWORK_UNAVAILABLE;
		}
		// 生成消息
		Message message = handler.obtainMessage(what, autoLoginV2ApiResult);
		// 发送通信
		if (1 >= CommonUtil.getTimestamp() - startTime) { // 快则停顿
			handler.sendMessageDelayed(message, Constant.DELAYED);
		} else {
			handler.sendMessage(message);
		}
		handler = null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
