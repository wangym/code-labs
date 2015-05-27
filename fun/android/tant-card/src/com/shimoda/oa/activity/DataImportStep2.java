package com.shimoda.oa.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.shimoda.oa.R;
import com.shimoda.oa.model.SourceContactListVO;
import com.shimoda.oa.service.ContactService;
import com.shimoda.oa.service.SourceContactService;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.importer.DataImporter;
import com.shimoda.oa.util.importer.IDataImportResponse;

public class DataImportStep2 extends BaseActivity {
	private static final int UPDATE_LISTVIEW = 1001;
	private static final int FINISH = 1002;
	private static final int ERROR = 1003;
	
	/**
	 * 菜单对话框
	 */
	private AlertDialog menuDlg;
	
	private DataImporter importer;
	
	private ListView listView;
	private ContactAdapter adapter;
	
	private int curIndex = 0;
	private int total = 0;
	private int imported =0;
	private boolean stop = false;
	private boolean ignoreExists = false;
	private List<SourceContactListVO> contactList;
	private ContactService contactService;
	private File dbFile ;
	
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case UPDATE_LISTVIEW:
				//更新进度
				curIndex = (Integer)msg.obj;
				showImporting(curIndex, total);
				break;
			case FINISH:
				//更新进度
				imported = (Integer)msg.obj;
				showFinish(imported);
				if(!stop){
					//非用户取消的，执行完毕删除db文件
					try {
						dbFile.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case ERROR:
				Toast.makeText(DataImportStep2.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	IDataImportResponse importResponse = new IDataImportResponse(){
		@Override
		public void onImportDataComplete(List<SourceContactListVO> contacts, int importedNum, int current) {
			Message msg = mHandler.obtainMessage();
			msg.what = UPDATE_LISTVIEW;
			msg.obj = current;
			msg.sendToTarget();
			
			imported = importedNum;
			contactList = contacts;
		}
		
		@Override
		public void onImportDataFinish(int imported) {
			Message msg = mHandler.obtainMessage();
			msg.what = FINISH;
			msg.obj = imported;
			msg.sendToTarget();
		}

		@Override
		public void onImportDataError(String errorMsg) {
			Message msg = mHandler.obtainMessage();
			msg.what = ERROR;
			msg.obj = errorMsg;
			msg.sendToTarget();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.data_import_step_2);
		// 设置标题文字
		setTextViewText(R.id.top_v1_tv_center, getString(R.string.title_import));
		//从导入文件中取联系人列表
		Intent intent = getIntent();
		String path = intent.getStringExtra("path");
		dbFile =  new File(path);
		
		if(!dbFile.exists()){
			//TODO 如果文件不存在，怎么处理，暂时跳转到菜单页面
			goToMenuActivity(true);
		}
		
		//点击停止导入按钮
		Button btnStop = (Button)this.findViewById(R.id.import_btn_stop);
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stop = true;
			}
		});
		
		//返回按钮
		initReturnButton();
		
		//联系人列表展示的view
		listView = (ListView) findViewById(R.id.import_step_2_list);
		adapter = new ContactAdapter();
		listView.setAdapter(adapter);
		
		//取列表
		SourceContactService sourceContactService = new SourceContactService(path);
		this.contactList = sourceContactService.getContactList(null, null, true);
		if(this.contactList!=null && !this.contactList.isEmpty()){
			this.total = this.contactList.size();
		}else{
			this.total = 0;
		}
		
		contactService = new ContactService(DataImportStep2.this);
		
		importer = new DataImporter(contactService, sourceContactService, this.contactList, importResponse);
		if(this.total>0){
			showImporting(0,this.total);
		}else{
			//发送完成消息
			importResponse.onImportDataFinish(imported);
		}
	}
	
	private void initReturnButton() {
		Button btnBack = (Button) this
				.findViewById(R.id.top_v1_ib_left);
		btnBack.setBackgroundResource(R.drawable.top_back);
		btnBack.setText(R.string.btn_title_back);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToMenuActivity(true);
			}
		});
	}
	
	private void showImporting(Integer index, Integer count){
		//显示正在导入第index个联系人
		setTextViewText(R.id.import_step_2_msg, getString(R.string.tips_importing));
		setTextViewText(R.id.import_step_2_num, (index+1)+" / "+count);
		setTextViewText(R.id.import_step_2_name, contactList.get(index).getLastName()+" "+contactList.get(index).getFirstName());
		
		//更新联系人列表
		adapter.notifyDataSetChanged();
		
		boolean exists = false;
		if(!ignoreExists){
			//验证联系人是否已经存在，存在则弹出菜单，否则执行导入
			String tantcardId = this.contactList.get(index).getTantcardId();
			exists = contactService.contactExists(tantcardId);
		}
		
		if(exists){
			//显示菜单
			showMenu();
		}else{
			if(!stop){
				importer.processImport();
			}else{
				//发送完成消息
				importResponse.onImportDataFinish(imported);
			}
		}
	}
	
	private void showMenu(){
		LayoutInflater factory = LayoutInflater.from(DataImportStep2.this);
		View dialogView = factory.inflate(R.layout.import_menu, null);
		// 设置标题
		setTextViewText(dialogView, R.id.menu_title,
				getString(R.string.import_step2_menu_title));
		// 绑定数据

		ListView listView = (ListView) dialogView.findViewById(R.id.menu_list);
		ArrayList<Map<String, String>> listData = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		map = new HashMap<String, String>();
		map.put("text", getString(R.string.import_step2_menu_import_current));
		listData.add(map);
		map = new HashMap<String, String>();
		map.put("text", getString(R.string.import_step2_menu_ignore_current));
		listData.add(map);
		map = new HashMap<String, String>();
		map.put("text", getString(R.string.import_step2_menu_import_next));
		listData.add(map);
		map = new HashMap<String, String>();
		map.put("text", getString(R.string.import_step2_menu_ignore_next));
		listData.add(map);
		
		ListView menuView = (ListView) dialogView.findViewById(R.id.menu_list);

		menuView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				menuDlg.dismiss();
				switch(position){
				case 0:
					//导入该条记录
					importer.processImport();
					break;
				case 1:
					//跳过该条记录
					importer.incrementCurrent();
					curIndex++;
					showImporting(curIndex,total);
					break;
				case 2:
					//导入后续记录
					ignoreExists = true;
					importer.processImport();
					break;
				case 3:
					//跳过后续记录
					importResponse.onImportDataFinish(imported);
					break;
				}
			}
		});

		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
				listData, R.layout.import_menu_item, new String[] { "text" },
				new int[] { R.id.import_menu_item_btn });
		listView.setAdapter(adapter);

		// 设置取消按钮的事件
		Button btn = (Button) dialogView.findViewById(R.id.import_menu_btn_cancel);
		btn.setText(R.string.import_step2_menu_cancel);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stop = true;
				importResponse.onImportDataFinish(imported);
				menuDlg.dismiss();
			}
		});
		
		menuDlg = new AlertDialog.Builder(DataImportStep2.this).create();
		Window window = menuDlg.getWindow();
		window.setGravity(Gravity.BOTTOM);
		menuDlg.show();
		
		menuDlg.setContentView(dialogView);
	}
	
	private void showFinish(int imported){
		//显示正在导入第index个联系人
		setTextViewText(R.id.import_step_2_msg, getString(R.string.tips_import_finish));
		setTextViewText(R.id.import_step_2_num, imported+" / "+this.total);
		this.findViewById(R.id.import_step_2_name).setVisibility(View.GONE);
		this.findViewById(R.id.import_btn_stop).setVisibility(View.GONE);
		this.findViewById(R.id.import_progressbar).setVisibility(View.GONE);
		this.findViewById(R.id.top).setVisibility(View.VISIBLE);
	}
	
	class ViewHolder{
		TextView nameText;
		TextView companyText;
		ImageView importedImage;
	}
	
	class ContactAdapter extends BaseAdapter {
		LayoutInflater inflater = null;

		@Override
		public int getCount() {
			if(contactList==null){
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
					inflater = (LayoutInflater) DataImportStep2.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(R.layout.data_import_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.nameText = (TextView) convertView
						.findViewById(R.id.contact_name);
				viewHolder.companyText = (TextView) convertView
						.findViewById(R.id.company_name);
				viewHolder.importedImage = (ImageView) convertView
						.findViewById(R.id.import_complete);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.nameText.setText(contactList.get(position).getLastName()+" "+contactList.get(position).getFirstName());
			viewHolder.companyText.setText(contactList.get(position).getCompany());
			if(contactList.get(position).getIsImported()){
				viewHolder.importedImage.setImageResource(R.drawable.import_complete);
				viewHolder.importedImage.setVisibility(View.VISIBLE);
			}else{
				viewHolder.importedImage.setVisibility(View.GONE);
			}
			
			return convertView;
		}
	}
}