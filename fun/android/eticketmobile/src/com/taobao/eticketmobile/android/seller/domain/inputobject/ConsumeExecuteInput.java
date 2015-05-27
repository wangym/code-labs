/**
 * 
 */
package com.taobao.eticketmobile.android.seller.domain.inputobject;

import java.io.Serializable;
import me.yumin.android.common.etc.CommonUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.BeforeConsumeApiResult;
import com.taobao.eticketmobile.android.seller.R;
import com.taobao.eticketmobile.android.seller.etc.Util;
import android.app.Activity;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class ConsumeExecuteInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1301027329492402327L;

	/**
	 * 
	 */
	private Activity activity;

	/**
	 * 
	 */
	private String code;
	private String mobile;
	private String consumeNum;

	/**
	 * 
	 * @param activity
	 * @param beforeConsumeApiResult
	 */
	public ConsumeExecuteInput(Activity activity, BeforeConsumeApiResult beforeConsumeApiResult) {

		if (null != activity && null != beforeConsumeApiResult) {
			// set attribute
			this.activity = activity;
			// set parameters
			code = beforeConsumeApiResult.getCode();
			mobile = beforeConsumeApiResult.getMobile();
			EditText etConsumeNum = (EditText) activity.findViewById(R.id.et_consume_execute_consume_num);
			consumeNum = (null != etConsumeNum ? etConsumeNum.getText().toString() : null);
		}
	}

	/**
	 * 校验输入对象
	 * 
	 * @return null=校验正确|not=提示失败
	 */
	public String validate() {

		String toast = null;

		if (!CommonUtil.isIntegerStr(code)) {
			toast = activity.getString(R.string.toast_consume_query_input_code);
		} else if (!Util.isMobile4(mobile)) {
			toast = activity.getString(R.string.toast_consume_query_input_mobile_4);
		} else if (!CommonUtil.isIntegerStr(consumeNum)) {
			toast = activity.getString(R.string.toast_consume_query_input_consume_num);
		}

		return toast;
	}

	/**
	 * 
	 */
	public String getCode() {
		return code;
	}

	public String getMobile() {
		return mobile;
	}

	public String getConsumeNum() {
		return consumeNum;
	}
}
