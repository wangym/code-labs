package com.shimoda.oa.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.shimoda.oa.R;
import com.shimoda.oa.model.CallLogVO;
import com.shimoda.oa.service.CallLogService;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.Constants;
import com.shimoda.oa.util.LoadDataAsyncTask;
import com.shimoda.oa.util.StringUtil;

public class CallLogList extends BaseActivity {
	private CallLogService callLogService;
	private List<CallLogVO> callLogList;
	private ListView listView;
	private CallLogAdapter adapter;
	private Button ibBack;
	private Button ibOrder;
	private TextView txtTitle;
	
	/*
	 * 0:非编辑 1:编辑
	 */
	private Integer op  = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 自定义标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call_log_list);

		initWidget();
		initListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		LoadCallLogAsyncTask task = new LoadCallLogAsyncTask();
		task.execute(null);
	}
	
	private void initWidget() {
		ibBack = (Button) findViewById(R.id.top_v1_ib_left);
		ibBack.setBackgroundResource(R.drawable.top_back);
		ibBack.setText(R.string.btn_title_back);
		ibBack.setVisibility(View.VISIBLE);
		ibOrder = (Button) findViewById(R.id.top_v1_ib_right);
		ibOrder.setBackgroundResource(R.drawable.top_order);
		ibOrder.setText(R.string.btn_title_delete);
		ibOrder.setVisibility(View.VISIBLE);
		ibOrder.setFocusable(true);
		ibOrder.setFocusableInTouchMode(true);
		txtTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		txtTitle.setText(R.string.title_calllog);
		txtTitle.setVisibility(View.VISIBLE);

		listView = (ListView) findViewById(R.id.calllog_list_lv_data);
		adapter = new CallLogAdapter();
		listView.setAdapter(adapter);
	}

	private void initListener() {
		// 返回按钮
		ibBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToMenuActivity(true);
			}
		});

		// 操作按钮
		ibOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(op==0){
					op = 1;
					ibOrder.setText(R.string.btn_title_finish);
				}else{
					op = 0;
					ibOrder.setText(R.string.btn_title_delete);
				}
				adapter.notifyDataSetChanged();
			}
		});

	
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(op==0){
					// 跳详情带参数
					Bundle bundle = new Bundle();
					bundle.putString(Constants.TANT_CARD_ID,
							callLogList.get(position).getTantcardId());
					bundle.putString(Constants.FROM_KEY,
							Constants.FROM_VAL_CALLLOG);
					Intent intent = new Intent();
					intent.putExtras(bundle);
					goToAnyActivity(ContactDetail.class, intent, false);
					return;
				}
				if (position >= callLogList.size()) {
					return;
				}
				if (callLogList.get(position).getOpStatus()==0) {
					callLogList.get(position).setOpStatus(1);
				} else{
					callLogList.get(position).setOpStatus(0);
				}

				adapter.notifyDataSetChanged();
			}
		});
	}



	/**
	 * 初始化联系人列表
	 */
	private void initCallLogList() {
		this.callLogService = new CallLogService(CallLogList.this);
		this.callLogList = this.callLogService.list();
	}

	class ViewHolder {
		TextView nameText;
		TextView callDateText;
		ImageView selectedImage;
		Button deleteButton;
	}

	class CallLogAdapter extends BaseAdapter {
		LayoutInflater inflater = null;

		@Override
		public int getCount() {
			if (callLogList == null) {
				return 0;
			}
			return callLogList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				if (inflater == null) {
					inflater = (LayoutInflater) CallLogList.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(
						R.layout.call_log_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.nameText = (TextView) convertView
						.findViewById(R.id.contact_name);
				viewHolder.callDateText = (TextView) convertView
						.findViewById(R.id.call_date);
				viewHolder.selectedImage = (ImageView) convertView
						.findViewById(R.id.del_op);
				viewHolder.deleteButton = (Button) convertView
				.findViewById(R.id.del_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.nameText.setText(callLogList.get(position).getFullName());
			String callDate = StringUtil.formatDate(callLogList.get(position).getCallDate(), "yyyy-MM-dd HH:mm:ss", "MM/dd HH:mm:ss");
			viewHolder.callDateText.setText(callDate+"\n"+callLogList.get(position).getTel());
			if(op==1){
				if(callLogList.get(position).getOpStatus()==0){
					viewHolder.selectedImage.setImageResource(R.drawable.del_op_1);
					viewHolder.selectedImage.setVisibility(View.VISIBLE);
					viewHolder.deleteButton.setVisibility(View.INVISIBLE);
				}else{
					viewHolder.selectedImage.setImageResource(R.drawable.del_op_2);
					viewHolder.selectedImage.setVisibility(View.VISIBLE);
					viewHolder.deleteButton.setText(R.string.btn_title_del_calllog);
					viewHolder.deleteButton.setVisibility(View.VISIBLE);
				}
				//删除按钮点击事件
				final int pos = position;
				viewHolder.deleteButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						//从数据库删除记录
						Integer id = callLogList.get(pos).getId();
						callLogService.deleteCallLogById(id);
						//从列表中移除
						callLogList.remove(pos);
						
						adapter.notifyDataSetChanged();
					}
				});
			}else{
				viewHolder.selectedImage.setVisibility(View.GONE);
				viewHolder.deleteButton.setVisibility(View.GONE);
			}
			return convertView;
		}
	}
	
	private class LoadCallLogAsyncTask extends LoadDataAsyncTask{
		public LoadCallLogAsyncTask(){
		}
		
		@Override
		protected void onPreExecute(){
			showProgressDialog();
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			initCallLogList();
			return null;
		}
		
		@Override  
        protected void onPostExecute(Object result) {
			hideProgressDialog();
			adapter.notifyDataSetChanged();
        }  
	}
}