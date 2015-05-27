/**
 * 
 */
package com.taobao.eticketmobile.android.seller.activity;

import me.yumin.android.common.etc.CommonUtil;
import me.yumin.android.common.etc.ViewUtil;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.taobao.eticketmobile.android.common.api.domain.api.result.BeforeConsumeApiResult;
import com.taobao.eticketmobile.android.common.api.domain.api.result.ConsumeApiResult;
import com.taobao.eticketmobile.android.seller.R;
import com.taobao.eticketmobile.android.seller.asynctask.BitmapAsyncTask;
import com.taobao.eticketmobile.android.seller.asynctask.ConsumeExecuteAsyncTask;
import com.taobao.eticketmobile.android.seller.domain.inputobject.ConsumeExecuteInput;
import com.taobao.eticketmobile.android.seller.domain.viewobject.ConsumeExecuteView;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.etc.GlobalVar;

/**
 * @author yumin
 * 
 */
public class ConsumeExecuteActivity extends BaseActivity implements Handler.Callback {

	/**
     *
     */
	private ProgressDialog progressDialog;
	private Handler handler;
	private BitmapAsyncTask bitmapAsyncTask;
	private ConsumeExecuteAsyncTask consumeExecuteAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (!GlobalVar.logged) {
			startLoginAndFinishActivity();
			return;
		}
		setContentView(R.layout.activity_consume_execute);
		initActivity();
		initData();
	}

	@Override
	protected void onDestroy() {

		if (null != handler) {
			handler = null;
		}
		if (null != bitmapAsyncTask) {
			if (!bitmapAsyncTask.isCancelled()) {
				bitmapAsyncTask.cancel(true);
			}
			bitmapAsyncTask = null;
		}
		if (null != consumeExecuteAsyncTask) {
			if (!consumeExecuteAsyncTask.isCancelled()) {
				consumeExecuteAsyncTask.cancel(true);
			}
			consumeExecuteAsyncTask = null;
		}
		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {

		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (null != msg) {
			// 消息处理
			switch (msg.what) {
			case Constant.EXCEPTION:
				ViewUtil.showToast(getToastException());
				break;
			case Constant.CONSUME_EXECUTE_SUCCESS:
				// 对象转换
				doConsumeExecuteSuccess((ConsumeApiResult) msg.obj);
				break;
			case Constant.CONSUME_EXECUTE_FAIL:
				// 对象转换
				ViewUtil.showToast(getToastFail((ConsumeApiResult) msg.obj));
				break;
			case Constant.NETWORK_UNAVAILABLE:
				ViewUtil.showToast(getToastNetworkUnavailable());
				break;
			case Constant.BITMAP_LOAD_SUCCESS:
				// 显示图片
				ImageView ivItemPic = (ImageView) findViewById(R.id.iv_consume_execute_item_pic);
				ivItemPic.setImageBitmap((Bitmap) msg.obj);
				break;
			case Constant.BITMAP_LOAD_FAIL:
				break;
			default:
				return false;
			}
		}

		return true;
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void initActivity() {

		handler = new Handler(this);
		// 设置标题
		TextView tvTopTitle = (TextView) findViewById(R.id.tv_include_top_title);
		tvTopTitle.setText(R.string.title_consume_execute);
		// 初始控件
		Button btnConsumeExecute = (Button) findViewById(R.id.btn_consume_execute);
		btnConsumeExecute.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				doConsumeExecute();
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

		// 电子凭证
		BeforeConsumeApiResult apiResult = (BeforeConsumeApiResult) getIntent().getExtras().getSerializable(Constant.K_CONSUME);
		if (null != apiResult && apiResult.isSuccess()) {
			ConsumeExecuteView consumeExecuteView = new ConsumeExecuteView(this, apiResult);
			String phrase = (null != consumeExecuteView ? consumeExecuteView.validate() : null);
			if (null == consumeExecuteView || CommonUtil.isNotEmpty(phrase)) {
				// 弹出提示
				Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(getString(R.string.prompt));
				builder.setMessage(getColonETicketUnavailable(phrase));
				builder.setPositiveButton(getString(R.string.rescan),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startCaptureActivity();
							}
						});
				builder.show();
			}
			/* 异步加载区域 */
			// 加载商品图片
			bitmapAsyncTask = new BitmapAsyncTask(handler);
			bitmapAsyncTask.execute(apiResult.getItemPicUrl());
		}
	}

	/**
	 * 
	 */
	private void doConsumeExecute() {

		BeforeConsumeApiResult beforeConsumeApiResult = (BeforeConsumeApiResult) getIntent().getExtras().getSerializable(Constant.K_CONSUME);
		if (null != beforeConsumeApiResult && beforeConsumeApiResult.isSuccess()) {
			ConsumeExecuteInput input = new ConsumeExecuteInput(this, beforeConsumeApiResult);
			String toast = input.validate();
			if (null != toast) {
				ViewUtil.showToast(toast);
				return;
			}
			// 执行核销
			progressDialog = ProgressDialog.show(this, "", getString(R.string.phrase_consume_execute_ing), true, false);
			consumeExecuteAsyncTask = new ConsumeExecuteAsyncTask(handler);
			consumeExecuteAsyncTask.execute(input);
		}
	}

	/**
	 * 
	 * @param apiResult
	 */
	private void doConsumeExecuteSuccess(ConsumeApiResult apiResult) {

		if (null != apiResult && apiResult.isSuccess()) {
			// 弹出提示
			Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle(getString(R.string.prompt));
			builder.setMessage(getString(R.string.phrase_consume_execute_success));
			builder.setPositiveButton(getString(R.string.confirm),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startCaptureActivity();
						}
					});
			builder.show();
		}
	}
}
