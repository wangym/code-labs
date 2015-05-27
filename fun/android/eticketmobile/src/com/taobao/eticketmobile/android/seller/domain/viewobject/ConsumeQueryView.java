/**
 * 
 */
package com.taobao.eticketmobile.android.seller.domain.viewobject;

import java.io.Serializable;
import me.yumin.android.common.etc.CommonUtil;
import com.taobao.eticketmobile.android.common.api.etc.ApiConstant;
import com.taobao.eticketmobile.android.seller.R;
import com.taobao.eticketmobile.android.seller.etc.Constant;
import android.app.Activity;
import android.widget.EditText;

/**
 * @author yumin
 * 
 */
public class ConsumeQueryView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1621894309316877969L;

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
	 * @param qrCode
	 * @return
	 */
	public ConsumeQueryView(Activity activity, String qrCode) {

		if (null != activity) {
			// set attribute
			this.activity = activity;
			// set parameters
			if (CommonUtil.isNotEmpty(qrCode) && qrCode.contains(ApiConstant.SEPARATE_DOUBLE_COLON)) {
				String[] resultArray = qrCode.split(ApiConstant.SEPARATE_DOUBLE_COLON);
				if (null != resultArray && 0 < resultArray.length) {
					String et = resultArray[0];
					if (CommonUtil.isNotEmpty(et) && Constant.K_ET.equals(et)) {
						code = resultArray[1];
						mobile = resultArray[2];
						// 初始控件
						EditText etConsumeQueryCode = (EditText) activity.findViewById(R.id.et_consume_query_code);
						etConsumeQueryCode.setText(code);
						EditText etConsumeQueryMobile = (EditText) activity.findViewById(R.id.et_consume_query_mobile);
						etConsumeQueryMobile.setText(mobile);
					}
				}
			}
		}
	}

	/**
	 * 校验显示对象
	 * 
	 * @return null=校验正确|not=提示失败
	 */
	public String validate() {

		String phrase = null;

		if (!CommonUtil.isNotEmpty(code) || !CommonUtil.isIntegerStr(mobile)) {
			phrase = activity.getString(R.string.phrase_not_qrcode_format);
		}

		return phrase;
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
