/**
 * 
 */
package com.taobao.eticketmobile.android.seller.domain.inputobject;

import java.io.Serializable;
import me.yumin.android.common.etc.CommonUtil;
import com.taobao.eticketmobile.android.seller.R;
import com.taobao.eticketmobile.android.seller.etc.Util;
import android.app.Activity;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class ConsumeQueryInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5419195151510241576L;

	/**
	 * 
	 */
	private Activity activity;

	/**
	 * 
	 */
	private String code;
	private String mobile;

	/**
	 * 
	 * @param activity
	 */
	public ConsumeQueryInput(Activity activity) {

		if (null != activity) {
			// set attribute
			this.activity = activity;
			// set parameters
			EditText etCode = (EditText) activity.findViewById(R.id.et_consume_query_code);
			code = (null != etCode ? etCode.getText().toString() : null);
			EditText etMobile = (EditText) activity.findViewById(R.id.et_consume_query_mobile);
			mobile = (null != etMobile ? etMobile.getText().toString() : null);
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
}
