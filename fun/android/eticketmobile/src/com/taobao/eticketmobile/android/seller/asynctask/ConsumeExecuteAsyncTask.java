/**
 * 
 */
package com.taobao.eticketmobile.android.seller.asynctask;

import me.yumin.android.common.etc.NetworkUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.ConsumeApiResult;
import com.taobao.eticketmobile.android.seller.domain.inputobject.ConsumeExecuteInput;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.service.ConsumeService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * @author yumin
 * 
 */
public class ConsumeExecuteAsyncTask extends AsyncTask<ConsumeExecuteInput, Void, ConsumeApiResult> {

	/**
	 * 
	 */
	private Handler handler;

	/**
	 * 
	 */
	public ConsumeExecuteAsyncTask(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ConsumeApiResult doInBackground(ConsumeExecuteInput... params) {

		// 核销执行
		ConsumeApiResult consumeApiResult = ConsumeService.consume(params[0]);

		return consumeApiResult;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(ConsumeApiResult consumeApiResult) {

		super.onPostExecute(consumeApiResult);
		int what = Constant.EXCEPTION;
		// 判断网络
		if (NetworkUtil.isNetworkConnected()) {
			if (null != consumeApiResult) {
				what = consumeApiResult.isSuccess() ? Constant.CONSUME_EXECUTE_SUCCESS : Constant.CONSUME_EXECUTE_FAIL;
			}
		} else {
			what = Constant.NETWORK_UNAVAILABLE;
		}
		// 生成消息
		Message message = handler.obtainMessage(what, consumeApiResult);
		// 发送通信
		handler.sendMessage(message);
		handler = null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
