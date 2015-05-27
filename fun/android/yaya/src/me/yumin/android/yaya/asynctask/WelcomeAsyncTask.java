/**
 * 
 */
package me.yumin.android.yaya.asynctask;

import me.yumin.android.common.etc.CommonUtil;
import me.yumin.android.common.etc.NetworkUtil;
import me.yumin.android.yaya.etc.Constant;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * @author yumin
 * 
 */
public class WelcomeAsyncTask extends AsyncTask<Void, Void, Boolean> {

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
	protected Boolean doInBackground(Void... params) {

		return true; // TODO
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Boolean result) {

		super.onPostExecute(result);
		int what = Constant.EXCEPTION;
		// 判断网络
		if (NetworkUtil.isNetworkConnected()) {
			if (null != result) {
				what = result.booleanValue() ? Constant.WELCOME_TO_MAIN : Constant.WELCOME_TO_LOGIN;
			}
		} else {
			what = Constant.NETWORK_UNAVAILABLE;
		}
		// 生成消息
		Message message = handler.obtainMessage(what, result);
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
