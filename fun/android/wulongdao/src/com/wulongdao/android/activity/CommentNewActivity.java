/**
 * 发布新回复
 */
package com.wulongdao.android.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import com.wulongdao.android.domain.enumtype.APIEnum;
import com.wulongdao.android.domain.resultobject.CommentResult;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.wulongdao.etc.CommonUtil;
import android.wulongdao.etc.HTTPUtil;

/**
 * @author yumin
 * 
 */
public class CommentNewActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnSubmit;
	private ImageButton btnBack;
	private EditText etMessage;

	/**
	 * 
	 */
	private HashMap<String, Object> questionDetail; // 问题详情,上一页跳转时传递

	// ====================
	// override methods
	// ====================

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		/* 初始数据 */
		questionDetail = (HashMap<String, Object>) getIntent().getSerializableExtra(Constant.K_QUESTION_DETAIL);
		if (!CommonUtil.isNotEmpty(questionDetail)) {
			goToIndex();
		}

		if (!isLoggedLocal()) {
			goToActivity(UserLoginActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_new);

		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goToQuestionDetailActivity(questionDetail);
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void initActivity() {

		// 顶部标题
		TextView tvTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		tvTitle.setText(getString(R.string.title_comment_post));
		// 初始控件
		btnSubmit = (ImageButton) findViewById(R.id.top_v1_ib_left);
		btnSubmit.setVisibility(Button.VISIBLE);
		btnSubmit.setBackgroundResource(R.drawable.send_btn);
		btnBack = (ImageButton) findViewById(R.id.top_v1_ib_right);
		btnBack.setVisibility(Button.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.back_btn);
		etMessage = (EditText) findViewById(R.id.comment_post_et_message);

		// 提交按钮监听
		btnSubmit.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用提交逻辑
				doSubmit();
			}
		});
		// 返回按钮监听
		btnBack.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用返回逻辑
				doBack();
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

	}

	/**
	 * 提交逻辑
	 */
	private void doSubmit() {

		// 获取控件内容
		final String message = etMessage.getText().toString();
		// 校验发布参数
		String prompt = validator.forCommentPost(message);
		if (CommonUtil.isNotEmpty(prompt)) {
			showToast(prompt);
			return;
		}
		// 隐藏虚拟键盘
		hideSoftInputFromWindow(etMessage.getWindowToken());
		//
		progressDialog = ProgressDialog.show(this, "", getString(R.string.prompt_progress_post), true, false);
		new Thread() {
			@Override
			public void run() {
				CommentResult result = new CommentResult();
				try {
					String timestamp = CommonUtil.getTimestamp() + "";
					Map<String, String> map = new HashMap<String, String>();
					map.put(Constant.K_MESSAGE, message);
					map.put(Constant.K_USER_ID, getUserId() + "");
					map.put(Constant.K_QUESTION_ID, questionDetail.get(Constant.K_QUESTION_ID) + "");
					map.put(Constant.K_LONGITUDE, "120.023736"); // TODO:获取坐标
					map.put(Constant.K_LATITUDE, "30.243585");
					map.put(Constant.K_TIMESTAMP, timestamp);
					map.put(Constant.K_SIGN, Util.getSign(getPassword(), getEmail(), timestamp));
					String response = HTTPUtil.postParameters(APIEnum.COMMENT_NEW.URL(), map);
					JSONObject json = CommonUtil.toJSONObject(response);
					if (null != json) {
						result.setCode(json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE));
						result.setMsg(json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (result.isSuccess()) {
					/* 发布成功逻辑 */
					// 跳至问题详情
					goToQuestionDetailActivity(questionDetail);
				} else {
					/* 发布失败逻辑 */
					progressMsg = result.getMsg();
				}
				progressHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 返回逻辑
	 */
	private void doBack() {

		goToQuestionDetailActivity(questionDetail);
	}

}
