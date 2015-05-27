/**
 * 
 */
package com.taobao.eticketmobile.android.seller.activity;

import me.yumin.android.common.etc.CommonUtil;
import me.yumin.android.common.etc.SystemUtil;
import me.yumin.android.zxing.etc.ZXingConstant;
import me.yumin.android.zxing.etc.ZXingInput;
import com.google.zxing.client.android.CaptureActivity;
import com.taobao.eticketmobile.android.common.api.domain.api.result.BaseApiResult;
import com.taobao.eticketmobile.android.seller.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author yumin
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class BaseActivity extends Activity {

	/**
	 * 
	 */
	protected void callWhetherToExitDialog() {

		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(getString(R.string.prompt));
		builder.setMessage(getString(R.string.phrase_whether_to_exit));
		builder.setPositiveButton(getString(R.string.exit),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
						SystemUtil.exit();
					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	/**
	 * 
	 * @param reason
	 * @return
	 */
	protected String getColonETicketUnavailable(String reason) {

		String prompt = getString(R.string.colon_eticket_unavailable);

		if (CommonUtil.isNotEmpty(reason)) {
			prompt += reason;
		}

		return prompt;
	}

	/**
	 * 
	 * @return
	 */
	protected String getToastException() {

		String prompt = getString(R.string.toast_exception);

		return prompt;
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected String getToastFail(Object object) {

		String prompt = getString(R.string.toast_fail);

		if (null != object) {
			BaseApiResult apiResult = (BaseApiResult) object;
			if (null != apiResult) {
				prompt = apiResult.getPrompt();
			}
		}

		return prompt;
	}

	/**
	 * 
	 * @return
	 */
	protected String getToastNetworkUnavailable() {

		String prompt = getString(R.string.toast_network_unavailable);

		return prompt;
	}

	/**
	 * 
	 * @param target
	 */
	protected void startActivity(Class target) {

		startActivity(target, null);
	}

	/**
	 * 
	 * @param target
	 * @param extras
	 */
	protected void startActivity(Class target, Bundle extras) {

		Intent intent = new Intent();
		if (null != extras) {
			intent.putExtras(extras);
		}
		intent.setClass(this, target);
		startActivity(intent);
	}

	/**
	 * 
	 * @param target
	 * @param extras
	 */
	protected void startAndFinishActivity(Class target, Bundle extras) {

		Intent intent = new Intent();
		if (null != extras) {
			intent.putExtras(extras);
		}
		intent.setClass(this, target);
		startActivity(intent);
		finish();
	}

	/**
	 * 
	 */
	protected void startCaptureActivity() {

		Bundle extras = new Bundle();
		extras.putSerializable(ZXingConstant.K_INPUT, new ZXingInput(ConsumeQueryActivity.class));
		startActivity(CaptureActivity.class, extras);
	}

	/**
	 * 
	 */
	protected void startLoginAndFinishActivity() {

		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 
	 */
	protected void startMainAndFinishActivity() {

		startCaptureActivity();
		finish();
	}

	/**
	 * 
	 * @param target
	 */
	protected void startAndFinishActivity(Class target) {

		Intent intent = new Intent();
		intent.setClass(this, target);
		startActivity(intent);
		finish();
	}
}
