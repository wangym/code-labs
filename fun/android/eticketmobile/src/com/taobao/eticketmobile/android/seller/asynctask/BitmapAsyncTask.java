/**
 * 
 */
package com.taobao.eticketmobile.android.seller.asynctask;

import me.yumin.android.common.etc.BitmapUtil;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * @author yumin
 * 
 */
public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {

	/**
	 * 
	 */
	private Handler handler;

	/**
	 * 
	 */
	public BitmapAsyncTask(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... params) {

		Bitmap bitmap = BitmapUtil.getBitmap(params[0]);

		return bitmap;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {

		super.onPostExecute(bitmap);
		int what = (null != bitmap ? Constant.BITMAP_LOAD_SUCCESS : Constant.BITMAP_LOAD_FAIL);
		// 生成消息
		Message message = handler.obtainMessage(what, bitmap);
		// 发送通信
		handler.sendMessage(message);
		handler = null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
