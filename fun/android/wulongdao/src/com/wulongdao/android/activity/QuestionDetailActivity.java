/**
 * 求助正文页
 */
package com.wulongdao.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wulongdao.android.adapter.CommentListAdapter;
import com.wulongdao.android.domain.enumtype.APIEnum;
import com.wulongdao.android.domain.resultobject.QuestionResult;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.wulongdao.etc.BitmapUtil;
import android.wulongdao.etc.CommonUtil;
import android.wulongdao.etc.HTTPUtil;

/**
 * @author yumin
 * 
 */
public class QuestionDetailActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnBack;
	private ImageButton btnPost;
	private TextView tvNickname;
	private TextView tvMessage;
	private ImageView ivImage;
	private TextView tvLoadTime;
	private TextView tvFrom;
	private ListView lvCommentList;

	/**
	 * 
	 */
	private HashMap<String, Object> questionDetail; // 问题详情,上一页跳转时传递
	private ArrayList<HashMap<String, Object>> commentList; // 回答列表,本页直接获取生成

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
		setContentView(R.layout.question_detail);

		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goToIndex();
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
		tvTitle.setText(getString(R.string.title_question_detail));
		// 初始控件
		btnPost = (ImageButton) findViewById(R.id.top_v1_ib_left);
		btnPost.setVisibility(Button.VISIBLE);
		btnPost.setBackgroundResource(R.drawable.post_btn);
		btnBack = (ImageButton) findViewById(R.id.top_v1_ib_right);
		btnBack.setVisibility(Button.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.back_btn);
		// 详情区域
		tvNickname = (TextView) findViewById(R.id.question_detail_tv_nickname);
		tvMessage = (TextView) findViewById(R.id.question_detail_tv_message);
		ivImage = (ImageView) findViewById(R.id.question_detail_tv_image);
		tvLoadTime = (TextView) findViewById(R.id.question_detail_tv_load_time);
		tvFrom = (TextView) findViewById(R.id.question_detail_tv_from);
		lvCommentList = (ListView) findViewById(R.id.comment_list_lv_refresh);

		// 回复按钮监听
		btnPost.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用发布逻辑
				doPost();
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

		// 问题页面填充
		tvNickname.setText((String) questionDetail.get(Constant.K_NICKNAME));
		tvMessage.setText((String) questionDetail.get(Constant.K_MESSAGE));
		tvLoadTime.setText(Util.getLoadTime((Long) questionDetail.get(Constant.K_LOAD_TIME)));
		tvFrom.setText(getString(R.string.default_from));
		String image = (String) CommonUtil.getMapValue(questionDetail, Constant.K_IMAGE);
		if (CommonUtil.isNotEmpty(image)) {
			ivImage.setVisibility(View.VISIBLE);
			ivImage.setImageBitmap(BitmapUtil.getBitmap(image + Constant.IMAGE_SIZE_LIST));
			// 图片预览监听
			ivImage.setOnClickListener(new ImageView.OnClickListener() {
				public void onClick(View view) {
					// 查看图片逻辑
					// TODO
				}
			});
		}

		//
		progressDialog = ProgressDialog.show(this, "", getString(R.string.prompt_progress), true, false);
		new Thread() {
			@Override
			public void run() {
				QuestionResult result = new QuestionResult();
				JSONArray array = null;
				try {
					String timestamp = CommonUtil.getTimestamp() + "";
					Map<String, String> map = new HashMap<String, String>();
					map.put(Constant.K_USER_ID, getUserId() + "");
					map.put(Constant.K_QUESTION_ID, questionDetail.get(Constant.K_QUESTION_ID) + "");
					map.put(Constant.K_LONGITUDE, "120.023736"); // TODO:获取坐标
					map.put(Constant.K_LATITUDE, "30.243585");
					map.put(Constant.K_TIMESTAMP, timestamp);
					map.put(Constant.K_SIGN, Util.getSign(getPassword(), getEmail(), timestamp));
					String response = HTTPUtil.postParameters(APIEnum.COMMENT_LIST.URL(), map);
					JSONObject json = CommonUtil.toJSONObject(response);
					if (null != json) {
						result.setCode(json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE));
						result.setMsg(json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG));
						json = json.isNull(Constant.K_RESULT) ? null : json.getJSONObject(Constant.K_RESULT);
						array = (null == json || json.isNull(Constant.K_COMMENT_LIST) ? null : json.getJSONArray(Constant.K_COMMENT_LIST));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (result.isSuccess() && null != array) {
					// 有则转换
					commentList = new ArrayList<HashMap<String, Object>>();
					try {
						for (int i = 0; i < array.length(); i++) {
							JSONObject json = (JSONObject) array.get(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							item.put(Constant.K_QUESTION_ID, json.isNull(Constant.K_QUESTION_ID) ? 0 : json.getInt(Constant.K_QUESTION_ID));
							item.put(Constant.K_AVATAR, json.isNull(Constant.K_AVATAR) ? null : json.getString(Constant.K_AVATAR));
							item.put(Constant.K_NICKNAME, json.isNull(Constant.K_NICKNAME) ? null : json.getString(Constant.K_NICKNAME));
							item.put(Constant.K_MESSAGE, json.isNull(Constant.K_MESSAGE) ? null : json.getString(Constant.K_MESSAGE));
							item.put(Constant.K_LOAD_TIME, json.isNull(Constant.K_LOAD_TIME) ? 0 : json.getLong(Constant.K_LOAD_TIME));
							commentList.add(item);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// 刷新页面
					QuestionDetailActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							CommentListAdapter adapter = new CommentListAdapter(getApplicationContext(), commentList);
							lvCommentList.setAdapter(adapter);
						}
					});
				} else {
					// 无则提示
					progressMsg = result.getMsg();
				}
				progressHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 发布回复
	 */
	private void doPost() {

		Intent intent = new Intent();
		intent.putExtra(Constant.K_QUESTION_DETAIL, questionDetail);
		goToActivity(CommentNewActivity.class, intent);
	}

	/**
	 * 返回逻辑
	 */
	private void doBack() {

		goToIndex();
	}

}
