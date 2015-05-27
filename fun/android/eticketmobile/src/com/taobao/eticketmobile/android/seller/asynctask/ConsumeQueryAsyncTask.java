/**
 * 
 */
package com.taobao.eticketmobile.android.seller.asynctask;

import me.yumin.android.common.etc.NetworkUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.BeforeConsumeApiResult;
import com.taobao.eticketmobile.android.seller.domain.inputobject.ConsumeQueryInput;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.service.ConsumeService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * @author yumin
 * 
 */
public class ConsumeQueryAsyncTask extends AsyncTask<ConsumeQueryInput, Void, BeforeConsumeApiResult> {

	/**
	 * 
	 */
	private Handler handler;

	/**
	 * 
	 */
	public ConsumeQueryAsyncTask(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected BeforeConsumeApiResult doInBackground(ConsumeQueryInput... params) {

		// 核销查询
		BeforeConsumeApiResult beforeConsumeApiResult = ConsumeService.query(params[0]);

		return beforeConsumeApiResult;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(BeforeConsumeApiResult beforeConsumeApiResult) {

		super.onPostExecute(beforeConsumeApiResult);
		int what = Constant.EXCEPTION;
		// 判断网络
		if (NetworkUtil.isNetworkConnected()) {
			if (null != beforeConsumeApiResult) {
				what = beforeConsumeApiResult.isSuccess() ? Constant.CONSUME_QUERY_SUCCESS : Constant.CONSUME_QUERY_FAIL;
			}
		} else {
			what = Constant.NETWORK_UNAVAILABLE;
		}
		// 生成消息
		Message message = handler.obtainMessage(what, beforeConsumeApiResult);
		// 发送通信
		handler.sendMessage(message);
		handler = null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
