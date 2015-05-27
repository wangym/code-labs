/**
 * 
 */
package com.taobao.eticketmobile.android.seller.activity;

import me.yumin.android.common.etc.CommonUtil;
import me.yumin.android.common.etc.ViewUtil;
import me.yumin.android.zxing.etc.ZXingConstant;
import me.yumin.android.zxing.etc.ZXingOutput;
import com.taobao.eticketmobile.android.common.api.domain.api.result.BeforeConsumeApiResult;
import com.taobao.eticketmobile.android.seller.R;
import com.taobao.eticketmobile.android.seller.asynctask.ConsumeQueryAsyncTask;
import com.taobao.eticketmobile.android.seller.domain.inputobject.ConsumeQueryInput;
import com.taobao.eticketmobile.android.seller.domain.viewobject.ConsumeQueryView;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import com.taobao.eticketmobile.android.seller.etc.GlobalVar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author yumin
 * 
 */
public class ConsumeQueryActivity extends BaseActivity implements Handler.Callback {

	/**
     *
     */
	private ProgressDialog progressDialog;
	private Handler handler;
	private ConsumeQueryAsyncTask consumeQueryAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (!GlobalVar.logged) {
			startLoginAndFinishActivity();
			return;
		}
		setContentView(R.layout.activity_consume_query);
		initActivity();
		initData();
	}

	@Override
	protected void onDestroy() {

		if (null != handler) {
			handler = null;
		}
		if (null != consumeQueryAsyncTask) {
			if (!consumeQueryAsyncTask.isCancelled()) {
				consumeQueryAsyncTask.cancel(true);
			}
			consumeQueryAsyncTask = null;
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
			// 对象转换
			BeforeConsumeApiResult apiResult = (BeforeConsumeApiResult) msg.obj;
			// 消息处理
			switch (msg.what) {
			case Constant.EXCEPTION:
				ViewUtil.showToast(getToastException());
				break;
			case Constant.CONSUME_QUERY_SUCCESS:
				doConsumeQuerySuccess(apiResult);
				break;
			case Constant.CONSUME_QUERY_FAIL:
				ViewUtil.showToast(getToastFail(apiResult));
				break;
			case Constant.NETWORK_UNAVAILABLE:
				ViewUtil.showToast(getToastNetworkUnavailable());
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
		tvTopTitle.setText(R.string.title_consume_query);
		// 初始控件
		Button btnConsumeQuery = (Button) findViewById(R.id.btn_consume_query);
		btnConsumeQuery.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				doConsumeQuery();
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

		// 电子凭证
		ZXingOutput output = (ZXingOutput) getIntent().getExtras().getSerializable(ZXingConstant.K_OUTPUT);
		if (null != output) {
			// 二维模式
			String qrCode = output.getText();
			ConsumeQueryView consumeQueryView = new ConsumeQueryView(this, qrCode);
			String phrase = (null != consumeQueryView ? consumeQueryView.validate() : null);
			if (null == consumeQueryView || CommonUtil.isNotEmpty(phrase)) {
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
				builder.setNegativeButton(getString(R.string.manual_input),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.show();
			}
		}
	}

	/**
	 * 
	 */
	private void doConsumeQuery() {

		ConsumeQueryInput input = new ConsumeQueryInput(this);
		String toast = input.validate();
		if (null != toast) {
			ViewUtil.showToast(toast);
			return;
		}
		// 执行登录
		progressDialog = ProgressDialog.show(this, "", getString(R.string.phrase_consume_query_ing), true, false);
		consumeQueryAsyncTask = new ConsumeQueryAsyncTask(handler);
		consumeQueryAsyncTask.execute(input);
	}

	/**
	 * 
	 * @param apiResult
	 */
	private void doConsumeQuerySuccess(BeforeConsumeApiResult apiResult) {

		Bundle extras = new Bundle();
		extras.putSerializable(Constant.K_CONSUME, apiResult);
		startAndFinishActivity(ConsumeExecuteActivity.class, extras);
	}
}
