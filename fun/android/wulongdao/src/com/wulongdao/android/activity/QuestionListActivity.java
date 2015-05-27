/**
 * 信息中心页
 */
package com.wulongdao.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wulongdao.android.adapter.QuestionListAdapter;
import com.wulongdao.android.domain.enumtype.APIEnum;
import com.wulongdao.android.domain.resultobject.QuestionResult;
import com.wulongdao.android.etc.Constant;
import com.wulongdao.android.etc.Util;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.wulongdao.etc.CommonUtil;
import android.wulongdao.etc.HTTPUtil;
import android.wulongdao.thirdparty.plugin.PullToRefreshListView;
import android.wulongdao.thirdparty.plugin.PullToRefreshListView.OnMoreListener;
import android.wulongdao.thirdparty.plugin.PullToRefreshListView.OnRefreshListener;

/**
 * @author yumin
 * 
 */
public class QuestionListActivity extends BaseActivity {

	/**
	 * 
	 */
	private ImageButton btnReload;
	private ImageButton btnPost;
	private PullToRefreshListView lvQuestionList;
	private QuestionListAdapter adapter;

	/**
	 * 
	 */
	private ArrayList<HashMap<String, Object>> questionList;
	private int questionId;

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (!isLoggedLocal()) {
			goToActivity(UserLoginActivity.class);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_list);

		initActivity();
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			callWhetherToExitDialog();
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		int listMenuGroup = 0;
		menu.add(listMenuGroup, MENU_USER_MODIFY_PASSWORD, Menu.NONE, R.string.modify_password);
		menu.add(listMenuGroup, MENU_USER_UPLOAD_AVATAR, Menu.NONE + 1, R.string.upload_avatar);
		menu.add(listMenuGroup, MENU_LOGOFF, Menu.NONE + 2, R.string.logoff);
		menu.add(listMenuGroup, MENU_EXIT, Menu.NONE + 3, R.string.exit);
		menu.setGroupCheckable(listMenuGroup, true, true);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_EXIT:
			callWhetherToExitDialog();
			break;
		case MENU_LOGOFF:
			callWhetherToLogoffDialog();
			break;
		case MENU_USER_MODIFY_PASSWORD:
			goToActivity(UserModifyPasswordActivity.class);
			break;
		case MENU_USER_UPLOAD_AVATAR:
			goToActivity(UserUploadAvatarActivity.class);
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
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
		tvTitle.setText(getNickname());
		// 初始控件
		btnPost = (ImageButton) findViewById(R.id.top_v1_ib_left);
		btnPost.setVisibility(Button.VISIBLE);
		btnPost.setBackgroundResource(R.drawable.post_btn);
		btnReload = (ImageButton) findViewById(R.id.top_v1_ib_right);
		btnReload.setVisibility(Button.VISIBLE);
		btnReload.setBackgroundResource(R.drawable.reload_btn);
		lvQuestionList = (PullToRefreshListView) findViewById(R.id.question_list_lv_refresh);

		// 发布按钮监听
		btnPost.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用发布逻辑
				doPost();
			}
		});
		// 刷新按钮监听
		btnReload.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View view) {
				// 调用刷新逻辑
				doReload();
			}
		});
		// 列表控件监听
		lvQuestionList.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Refresh().execute();
			}
		});
		lvQuestionList.setOnMoreListener(new OnMoreListener() {
			@Override
			public void onMore() {
				new More().execute();
			}
		});
		lvQuestionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
				int index = paramInt - 1;
				if (null != questionList && index >= 0 && index < questionList.size()) {
					Intent intent = new Intent();
					intent.putExtra(Constant.K_QUESTION_DETAIL, questionList.get(index));
					goToActivity(QuestionDetailActivity.class, intent);
				}
			}
		});
	}

	/**
	 * 
	 */
	private void initData() {

		new Refresh().execute();
	}

	/**
	 * 刷新逻辑
	 */
	private void doReload() {

		lvQuestionList.setOnRefreshButtonListener();
	}

	/**
	 * 发布逻辑
	 */
	private void doPost() {

		goToActivity(QuestionNewActivity.class);
	}

	/**
	 * 加载最新
	 * 
	 * @author yumin
	 */
	private class Refresh extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPostExecute(String[] args) {

			QuestionResult result = new QuestionResult();
			JSONArray array = null;
			try {
				String timestamp = CommonUtil.getTimestamp() + "";
				Map<String, String> map = new HashMap<String, String>();
				map.put(Constant.K_USER_ID, getUserId() + "");
				map.put(Constant.K_LONGITUDE, "120.023736"); // TODO:获取坐标
				map.put(Constant.K_LATITUDE, "30.243585");
				map.put(Constant.K_TIMESTAMP, timestamp);
				map.put(Constant.K_SIGN, Util.getSign(getPassword(), getEmail(), timestamp));
				String response = HTTPUtil.postParameters(APIEnum.QUESTION_LIST.URL(), map);
				JSONObject json = CommonUtil.toJSONObject(response);
				if (null != json) {
					result.setCode(json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE));
					result.setMsg(json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG));
					array = json.isNull(Constant.K_RESULT) ? null : json.getJSONArray(Constant.K_RESULT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result.isSuccess() && null != array) {
				// 有则转换
				questionList = new ArrayList<HashMap<String, Object>>();
				try {
					for (int i = 0; i < array.length(); i++) {
						JSONObject json = (JSONObject) array.get(i);
						HashMap<String, Object> item = jsonObjectToQuestionMap(json);
						questionList.add(item);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 刷新页面
				QuestionListActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter = new QuestionListAdapter(getApplicationContext(), questionList);
						lvQuestionList.setAdapter(adapter);
					}
				});
			} else {
				// 无则提示
				showToast(result.getMsg());
			}

			lvQuestionList.onRefreshComplete(getLastUpdated());
			super.onPostExecute(args);
		}

		@Override
		protected String[] doInBackground(Void... params) {

			return null;
		}
	}

	/**
	 * 加载更多
	 * 
	 * @author yumin
	 */
	private class More extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPostExecute(String[] args) {

			QuestionResult result = new QuestionResult();
			JSONArray array = null;
			try {
				String timestamp = CommonUtil.getTimestamp() + "";
				Map<String, String> map = new HashMap<String, String>();
				map.put(Constant.K_USER_ID, getUserId() + "");
				map.put(Constant.K_LONGITUDE, "120.023736"); // TODO:获取坐标
				map.put(Constant.K_LATITUDE, "30.243585");
				map.put(Constant.K_START, questionId + "");
				map.put(Constant.K_TIMESTAMP, timestamp);
				map.put(Constant.K_SIGN, Util.getSign(getPassword(), getEmail(), timestamp));
				String response = HTTPUtil.postParameters(APIEnum.QUESTION_LIST.URL(), map);
				JSONObject json = CommonUtil.toJSONObject(response);
				if (null != json) {
					result.setCode(json.isNull(Constant.K_CODE) ? 0 : json.getInt(Constant.K_CODE));
					result.setMsg(json.isNull(Constant.K_MSG) ? null : json.getString(Constant.K_MSG));
					array = json.isNull(Constant.K_RESULT) ? null : json.getJSONArray(Constant.K_RESULT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result.isSuccess() && null != array) {
				// 有则转换
				try {
					for (int i = 0; i < array.length(); i++) {
						JSONObject json = (JSONObject) array.get(i);
						HashMap<String, Object> item = jsonObjectToQuestionMap(json);
						adapter.addItem(item);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 刷新页面
				QuestionListActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
			} else {
				// 无则提示
				showToast(result.getMsg());
			}

			// 列表尾部展现
			if (null != array && 0 < array.length()) {
				lvQuestionList.onMoreComplete();
			} else {
				lvQuestionList.onMoreNoData();
			}

			super.onPostExecute(args);
		}

		@Override
		protected String[] doInBackground(Void... params) {

			return null;
		}
	}

	/**
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	private HashMap<String, Object> jsonObjectToQuestionMap(JSONObject json) throws JSONException {

		questionId = json.isNull(Constant.K_QUESTION_ID) ? 0 : json.getInt(Constant.K_QUESTION_ID);
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(Constant.K_QUESTION_ID, questionId);
		item.put(Constant.K_AVATAR, json.isNull(Constant.K_AVATAR) ? null : json.getString(Constant.K_AVATAR));
		item.put(Constant.K_NICKNAME, json.isNull(Constant.K_NICKNAME) ? null : json.getString(Constant.K_NICKNAME));
		item.put(Constant.K_MESSAGE, json.isNull(Constant.K_MESSAGE) ? null : json.getString(Constant.K_MESSAGE));
		item.put(Constant.K_LOAD_TIME, json.isNull(Constant.K_LOAD_TIME) ? 0 : json.getLong(Constant.K_LOAD_TIME));
		item.put(Constant.K_IMAGE, json.isNull(Constant.K_IMAGE) ? null : json.getString(Constant.K_IMAGE));
		item.put(Constant.K_AUDIO, json.isNull(Constant.K_AUDIO) ? null : json.getString(Constant.K_AUDIO));

		return item;
	}

}
