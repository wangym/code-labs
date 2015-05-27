/**
 * 
 */
package com.taobao.eticketmobile.android.seller.domain.viewobject;

import java.io.Serializable;
import me.yumin.android.common.etc.CommonUtil;
import com.taobao.eticketmobile.android.common.api.domain.api.result.BeforeConsumeApiResult;
import com.taobao.eticketmobile.android.seller.R;
import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author yumin
 * 
 */
public class ConsumeExecuteView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6306000023960685997L;

	/**
	 * 
	 */
	private Activity activity;

	/**
	 * 
	 */
	private String itemPicUrl;
	private String itemTitle;
	private String sellerNick;
	private String leftNum;

	/**
	 * 
	 * @param activity
	 * @param apiResult
	 * @return
	 */
	public ConsumeExecuteView(Activity activity, BeforeConsumeApiResult apiResult) {

		if (null != activity) {
			// set attribute
			this.activity = activity;
			// set parameters
			itemTitle = apiResult.getItemTitle();
			sellerNick = apiResult.getSellerNick();
			leftNum = apiResult.getCodeLeftNum();
			// 初始控件
			TextView tvItemIntro = (TextView) activity.findViewById(R.id.tv_consume_execute_item_intro);
			tvItemIntro.setText(getItemIntroSpannable());
			TextView tvLeftNum = (TextView) activity.findViewById(R.id.tv_consume_execute_left_num);
			tvLeftNum.setText(getLeftNumSpannable());
			EditText etLeftNum = (EditText) activity.findViewById(R.id.et_consume_execute_consume_num);
			etLeftNum.setText("1");
		}
	}

	/**
	 * 校验显示对象
	 * 
	 * @return null=校验正确|not=提示失败
	 */
	public String validate() {

		String phrase = null;

		if (!CommonUtil.isIntegerStr(leftNum)) {
			phrase = activity.getString(R.string.phrase_left_num_error);
		}

		return phrase;
	}

	/**
	 * 
	 */
	public String getItemPicUrl() {
		return itemPicUrl;
	}

	public void setItemPicUrl(String itemPicUrl) {
		this.itemPicUrl = itemPicUrl;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public String getSellerNick() {
		return sellerNick;
	}

	public void setSellerNick(String sellerNick) {
		this.sellerNick = sellerNick;
	}

	public String getLeftNum() {
		return leftNum;
	}

	public void setLeftNum(String leftNum) {
		this.leftNum = leftNum;
	}

	/**
	 * 
	 * @return
	 */
	private SpannableString getItemIntroSpannable() {

		SpannableString spannable = null;

		String leftSquareBracket = activity.getString(R.string.left_square_bracket);
		String rightSquareBracket = activity.getString(R.string.right_square_bracket);
		String itemIntroText = new StringBuilder(itemTitle).append(leftSquareBracket)
				.append(activity.getString(R.string.seller))
				.append(activity.getString(R.string.colon)).append(sellerNick)
				.append(rightSquareBracket).toString();
		int start = itemIntroText.indexOf(leftSquareBracket);
		int end = itemIntroText.indexOf(rightSquareBracket) + 1;
		System.out.println("getItemIntroSpannable:" + start + "~" + end);
		spannable = new SpannableString(itemIntroText);
		spannable.setSpan(new ForegroundColorSpan(Color.GRAY), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannable;
	}

	/**
	 * 
	 * @return
	 */
	private SpannableString getLeftNumSpannable() {

		SpannableString spannable = null;

		String leftNumText = new StringBuilder(
				activity.getString(R.string.left_num_head)).append(leftNum)
				.append(activity.getString(R.string.left_num_foot)).toString();
		int start = leftNumText.indexOf(leftNum);
		int end = leftNumText.indexOf(leftNum) + leftNum.length();
		spannable = new SpannableString(leftNumText);
		spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannable;
	}
}
