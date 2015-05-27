package com.shimoda.oa.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.shimoda.oa.R;
import com.shimoda.oa.model.ContactListVO;
import com.shimoda.oa.service.ContactService;
import com.shimoda.oa.service.SystemContactService;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.LoadDataAsyncTask;
import com.shimoda.oa.util.exporter.DataExporter;
import com.shimoda.oa.util.exporter.IDataExportResponse;

public class DataExportStep1 extends BaseActivity {
	private static final int UPDATE_LISTVIEW = 1101;
	private static final int FINISH = 1102;
	private static final int ERROR = 1103;
	
	private EditText etKeyword;

	private ContactService contactService;
	private List<ContactListVO> contactList;
	private ListView listView;
	private ContactAdapter adapter;

	private AlertDialog menuDlg;

	private DataExporter exporter;
	private int current = 0;
	private int total = 0;
	private int exported = 0;
	private boolean stop = false;
	private boolean exporting = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 自定义标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.data_export_step_1);
		//setTextViewText(R.id.top_v1_tv_center, getString(R.string.menu_data_export));
		this.contactService = new ContactService(DataExportStep1.this);

		initWidget();
		initListener();
	}

	@Override
	protected void onResume() {
		super.onResume();

		LoadContactsAsyncTask task = new LoadContactsAsyncTask();
		task.execute(null);
	}

	private void initWidget() {
		Button btnBack = (Button) this.findViewById(R.id.top_v1_ib_left);
		btnBack.setText(R.string.btn_title_back);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setBackgroundResource(R.drawable.top_back);
		Button btnMenu = (Button) this.findViewById(R.id.top_v1_ib_right);
		btnMenu.setText(R.string.btn_title_menu);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setBackgroundResource(R.drawable.top_order);
		this.findViewById(R.id.top_v1_tv_center).setFocusableInTouchMode(true);

		// 隐藏导出状态
		removeView(R.id.exporting);

		listView = (ListView) findViewById(R.id.export_list_lv_data);
		adapter = new ContactAdapter();
		listView.setAdapter(adapter);

		// keyword
		etKeyword = (EditText) findViewById(R.id.export_list_et_keyword);
	}

	private void initListener() {
		// 返回按钮
		Button btnBack = (Button) this.findViewById(R.id.top_v1_ib_left);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToMenuActivity(true);
			}
		});

		// 菜单按钮
		Button btnMenu = (Button) this.findViewById(R.id.top_v1_ib_right);
		btnMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (menuDlg != null && menuDlg.isShowing()) {
					menuDlg.dismiss();
				} else {
					showMenu();
				}
			}
		});

		// 搜索文本监听
		etKeyword.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				reloadContactList();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//
			}
		});
		
		//点击停止导出按钮
		Button btnStop = (Button)this.findViewById(R.id.exporting_btn_stop);
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stop = true;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(exporting){
					return;
				}
				if (position >= contactList.size()) {
					return;
				}
				if (contactList.get(position).getIsSelected()) {
					contactList.get(position).setIsSelected(false);
				} else {
					contactList.get(position).setIsSelected(true);
				}

				adapter.notifyDataSetChanged();
			}
		});
	}

	private void showMenu() {
		LayoutInflater factory = LayoutInflater.from(DataExportStep1.this);
		View dialogView = factory.inflate(R.layout.import_menu, null);
		// 设置标题
		setTextViewText(dialogView, R.id.menu_title,
				getString(R.string.export_menu_title));
		// 绑定数据

		ListView listView = (ListView) dialogView.findViewById(R.id.menu_list);
		ArrayList<Map<String, String>> listData = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		map = new HashMap<String, String>();
		map.put("text", getString(R.string.export_menu_select_all));
		listData.add(map);
		map = new HashMap<String, String>();
		map.put("text", getString(R.string.export_menu_unselect_all));
		listData.add(map);
		map = new HashMap<String, String>();
		map.put("text", getString(R.string.export_menu_export));
		listData.add(map);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				menuDlg.dismiss();
				switch (position) {
				case 0:
					// 导入该条记录
					selectAll();
					break;
				case 1:
					// 跳过该条记录
					unselectAll();
					break;
				case 2:
					// 导出
					doExport();
					break;
				}
			}
		});

		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
				listData, R.layout.import_menu_item, new String[] { "text" },
				new int[] { R.id.import_menu_item_btn });
		listView.setAdapter(adapter);

		// 设置取消按钮的事件
		Button btn = (Button) dialogView
				.findViewById(R.id.import_menu_btn_cancel);
		btn.setText(R.string.export_menu_cancel);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				menuDlg.dismiss();
			}
		});

		menuDlg = new AlertDialog.Builder(DataExportStep1.this).create();
		Window window = menuDlg.getWindow();
		window.setGravity(Gravity.BOTTOM);
		menuDlg.show();

		menuDlg.setContentView(dialogView);
	}

	private void selectAll() {
		if (this.contactList != null && !this.contactList.isEmpty()) {
			for (ContactListVO vo : this.contactList) {
				vo.setIsSelected(true);
			}
		}

		adapter.notifyDataSetChanged();

		menuDlg.dismiss();
	}

	private void unselectAll() {
		if (this.contactList != null && !this.contactList.isEmpty()) {
			for (ContactListVO vo : this.contactList) {
				vo.setIsSelected(false);
			}
		}

		adapter.notifyDataSetChanged();

		menuDlg.dismiss();
	}

	private void doExport() {
		exporting = true;
		stop = false;
		this.total = 0;
		if (this.contactList != null && !this.contactList.isEmpty()) {
			for (ContactListVO vo : this.contactList) {
				if (vo.getIsSelected()) {
					this.total++;
				}
			}
		}
		if (this.total == 0) {
			exporting = false;
			// 弹出对话框
			new AlertDialog.Builder(this).setMessage(
					getString(R.string.tips_need_to_select_export_contact)).setPositiveButton(
					getString(R.string.dlg_btn_confirm),null).show();
			return;
		}
		
		// 隐藏标题和搜索框
		removeView(R.id.search_ll);
		showView(R.id.exporting);
		showView(R.id.exporting_name);
		showView(R.id.exporting_progressbar);
		showView(R.id.exporting_btn_stop);
		
		exporter = new DataExporter(new SystemContactService(DataExportStep1.this), contactService, this.contactList, exportResponse);

		exporting(0, this.total);
	}

	private void showExportResult(int exported) {
		// 显示导出结果
		setTextViewText(R.id.exporting_msg,
				getString(R.string.tips_export_finish));
		setTextViewText(R.id.exporting_num, exported + " / " + this.total);
		hiddenView(R.id.exporting_name);
		hiddenView(R.id.exporting_progressbar);
		hiddenView(R.id.exporting_btn_stop);

		// 弹出对话框
		new AlertDialog.Builder(this).setMessage(
				getString(R.string.dlg_msg_export_finish)).setPositiveButton(
				getString(R.string.dlg_btn_confirm),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						//按钮事件
						showView(R.id.search_ll);
						removeView(R.id.exporting);
				        
						exporting = false;
					}
				}).show();
	}

	private void exporting(Integer index, Integer count) {
		// 显示正在导入第index个联系人
		setTextViewText(R.id.exporting_msg, getString(R.string.tips_exporting));
		setTextViewText(R.id.exporting_num, (index + 1) + " / " + count);
		setTextViewText(R.id.exporting_name, contactList.get(index)
				.getFullName());
		
		adapter.notifyDataSetChanged();
		
		if(!stop){
			exporter.processExport();
		}else{
			//发送完成消息
			exportResponse.onExportDataFinish(exported);
		}
	}

	private void reloadContactList(){
		initContactList();
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 初始化联系人列表
	 */
	private void initContactList() {
		String keyword = etKeyword.getText().toString();
		this.contactList = this.contactService.queryContactList(keyword,
				ContactService.ORDERBY_NAME);
	}

	class ViewHolder {
		TextView nameText;
		TextView companyText;
		ImageView selectedImage;
	}

	class ContactAdapter extends BaseAdapter {
		LayoutInflater inflater = null;

		@Override
		public int getCount() {
			if (contactList == null) {
				return 0;
			}
			return contactList.size();
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
					inflater = (LayoutInflater) DataExportStep1.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(
						R.layout.data_export_step1_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.nameText = (TextView) convertView
						.findViewById(R.id.contact_name);
				viewHolder.companyText = (TextView) convertView
						.findViewById(R.id.company_name);
				viewHolder.selectedImage = (ImageView) convertView
						.findViewById(R.id.selected);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.nameText.setText(contactList.get(position).getLastName()
					+ " " + contactList.get(position).getFirstName());
			viewHolder.companyText.setText(contactList.get(position)
					.getCompany());
			if (contactList.get(position).getIsSelected()) {
				// 选中状态
				viewHolder.selectedImage.setImageResource(R.drawable.checked);
			} else {
				// 非选中状态
				viewHolder.selectedImage.setImageResource(R.drawable.unchecked);
			}
			
			//字体颜色
			if (contactList.get(position).getIsExported()) {
				//字体设置为灰色
				viewHolder.nameText.setTextColor(android.graphics.Color.GRAY);
				viewHolder.companyText.setTextColor(android.graphics.Color.GRAY);
			} else {
				//字体设置为白色
				viewHolder.nameText.setTextColor(android.graphics.Color.WHITE);
				viewHolder.companyText.setTextColor(android.graphics.Color.WHITE);
			}

			return convertView;
		}
	}
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case UPDATE_LISTVIEW:
				//导出一个联系人完毕，继续下一个
				current = (Integer)msg.obj;
				exporting(current, total);
				break;
			case FINISH:
				//导出完毕
				exported = (Integer)msg.obj;
				showExportResult(exported);
				break;
			case ERROR:
				showToast((String)msg.obj);
				showExportResult(exported);
				break;
			}
		}
	};
	
	IDataExportResponse exportResponse = new IDataExportResponse(){
		@Override
		public void onExportDataComplete(int exportedNum, int current) {
			Message msg = mHandler.obtainMessage();
			msg.what = UPDATE_LISTVIEW;
			msg.obj = current;
			msg.sendToTarget();
			
			exported = exportedNum;
		}
		
		@Override
		public void onExportDataFinish(int exported) {
			Message msg = mHandler.obtainMessage();
			msg.what = FINISH;
			msg.obj = exported;
			msg.sendToTarget();
		}

		@Override
		public void onExportDataError(String errorMsg) {
			Message msg = mHandler.obtainMessage();
			msg.what = ERROR;
			msg.obj = errorMsg;
			msg.sendToTarget();
		}
	};
	
	private class LoadContactsAsyncTask extends LoadDataAsyncTask{
		public LoadContactsAsyncTask(){
		}
		
		@Override
		protected void onPreExecute(){
			showProgressDialog();
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			initContactList();
			return null;
		}
		
		@Override  
        protected void onPostExecute(Object result) {
			hideProgressDialog();
			adapter.notifyDataSetChanged();
        }  
	}
}