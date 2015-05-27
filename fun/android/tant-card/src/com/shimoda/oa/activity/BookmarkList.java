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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.shimoda.oa.R;
import com.shimoda.oa.model.BookmarkVO;
import com.shimoda.oa.service.BookmarkService;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.Constants;
import com.shimoda.oa.util.LoadDataAsyncTask;

public class BookmarkList extends BaseActivity {
	private BookmarkService bookmarkService;
	private List<BookmarkVO> bookmarkList;
	private ListView listView;
	private BookmarkAdapter adapter;
	private Button ibBack;
	private TextView txtTitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 自定义标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bookmark_list);

		initWidget();
		initListener();
	}

	@Override
	protected void onResume() {
		super.onResume();

		LoadBookmarkAsyncTask task =  new LoadBookmarkAsyncTask();
		task.execute(null);
	}

	private void initWidget() {
		ibBack = (Button) findViewById(R.id.top_v1_ib_left);
		ibBack.setBackgroundResource(R.drawable.top_back);
		ibBack.setText(R.string.btn_title_back);
		ibBack.setVisibility(View.VISIBLE);
		txtTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		txtTitle.setText(R.string.title_bookmark);
		txtTitle.setVisibility(View.VISIBLE);

		listView = (ListView) findViewById(R.id.bookmark_list_lv_data);
		adapter = new BookmarkAdapter();
		listView.setAdapter(adapter);
	}

	private void initListener() {
		// 返回按钮
		ibBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(MainMenu.class, true);
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// 跳详情带参数
				Bundle bundle = new Bundle();
				bundle.putString(Constants.TANT_CARD_ID,
						bookmarkList.get(pos).getTantcardId());
				bundle.putString(Constants.FROM_KEY,
						Constants.FROM_VAL_BOOKMARK);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				goToAnyActivity(ContactDetail.class, intent, false);
			}
		});
	}

	/**
	 * 初始化列表数据
	 */
	private void initBookmarkList() {
		this.bookmarkService = new BookmarkService(BookmarkList.this);
		this.bookmarkList = this.bookmarkService.list();
	}

	class ViewHolder {
		TextView nameText;
		TextView companyText;
	}

	class BookmarkAdapter extends BaseAdapter {
		LayoutInflater inflater = null;

		@Override
		public int getCount() {
			if (bookmarkList == null) {
				return 0;
			}
			return bookmarkList.size();
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
					inflater = (LayoutInflater) BookmarkList.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(
						R.layout.bookmark_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.nameText = (TextView) convertView
						.findViewById(R.id.contact_name);
				viewHolder.companyText = (TextView) convertView
						.findViewById(R.id.contact_company);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.nameText.setText(bookmarkList.get(position).getFullName());
			viewHolder.companyText.setText(bookmarkList.get(position).getCompany());
			
			return convertView;
		}
	}
	
	private class LoadBookmarkAsyncTask extends LoadDataAsyncTask{
		public LoadBookmarkAsyncTask(){
		}
		
		@Override
		protected void onPreExecute(){
			showProgressDialog();
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			initBookmarkList();
			return null;
		}
		
		@Override  
        protected void onPostExecute(Object result) {
			hideProgressDialog();
			adapter.notifyDataSetChanged();
        }  
	}
}