/**
 * 
 */
package me.yumin.android.yaya.activity;

import me.yumin.android.common.etc.SystemUtil;
import me.yumin.android.yaya.R;
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
	 * @return
	 */
	protected String getToastException() {

		String prompt = getString(R.string.toast_exception);

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
	protected void startLoginAndFinishActivity() {

		startAndFinishActivity(LoginActivity.class);
	}

	/**
	 * 
	 */
	protected void startMainAndFinishActivity() {

		startAndFinishActivity(MainActivity.class);
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
