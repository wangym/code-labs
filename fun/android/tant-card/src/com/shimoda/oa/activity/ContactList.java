/**
 * 联系人列表
 */
package com.shimoda.oa.activity;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.RelativeLayout.LayoutParams;
import com.shimoda.oa.R;
import com.shimoda.oa.model.ContactListVO;
import com.shimoda.oa.model.GroupInfo;
import com.shimoda.oa.service.ContactService;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.Constants;
import com.shimoda.oa.util.LoadDataAsyncTask;
import com.shimoda.oa.util.StringUtil;
import com.shimoda.oa.view.ContactIndexView;
import com.shimoda.oa.view.FindEditText;
import com.shimoda.oa.view.CustomGallery;

/**
 * @author yumin
 * 
 */
@SuppressWarnings("unused")
public class ContactList extends BaseActivity {

	/**
	 * 横竖屏公用信息，用来定位当前的记录位置
	 */
	private int order = 1; // 1:名前顺(默认) 2:会社顺
	private String keyword = "";
	private String curTantcardId = null;

	private List<Map<String, Object>> kanaList = null;
	private List<ContactListVO> contactList = null;

	/**
	 * 
	 */
	private Button ibBack;
	private Button ibOrder;
	private FindEditText etKeyword;
	private ExpandableListView lvData;
	private ContactListAdapter adapter;
	private CustomGallery gallery;
	private ImageAdapter imageAdapter;

//	private LinearLayout groupTitleLayout;
//	private TextView groupTitle;

	private int first = -1;
	private int group = -1;

	private int orientation = Configuration.ORIENTATION_LANDSCAPE;

	/**
	 * service
	 */
	private ContactService contactService;

	// ====================
	// override methods
	// ====================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//横竖屏切换恢复相关数据
		restoreInstanceState(savedInstanceState);
		
		// 初始化服务
		contactService = new ContactService(this);

		orientation = this.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.contact_list_2);
			initWidget2();
			initListener2();
		} else {
			setContentView(R.layout.contact_list);
			initWidget();
			initListener();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		//从详情页返回过来时候保存的状态
		restoreFromBundle(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//获取屏幕尺寸
		getSize();
		
		LoadContactAsyncTask task = new LoadContactAsyncTask();
		task.execute(orientation);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			goToMenuActivity(true);
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
	 * 横竖屏切换保存的状态
	 */
	private void restoreInstanceState(Bundle savedInstanceState){
		//切换屏幕数据的恢复
		if(savedInstanceState!=null){
			order = savedInstanceState.getInt("order");
			keyword = savedInstanceState.getString("keyword");
			
			curTantcardId = savedInstanceState.getString("tantcard_id");
		}
	}
	
	/**
	 * 详情页返回过来保存的状态
	 */
	private void restoreFromBundle(Intent intent){
		if(intent==null){
			return;
		}
		
		//详情页面过来时候定位的恢复
		Bundle bundle = intent.getExtras();
		if(bundle!=null){
			if(!StringUtil.isEmpty(bundle.getString(Constants.TANT_CARD_ID))){
				curTantcardId = bundle.getString(Constants.TANT_CARD_ID);
			}
		}
	}

	/**
	 * 
	 */
	private void initWidget() {

		// top
		TextView tvTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		tvTitle.setText(R.string.title_contact_list);
		ibBack = (Button) findViewById(R.id.top_v1_ib_left);
		ibBack.setBackgroundResource(R.drawable.top_back);
		ibBack.setText(R.string.btn_title_back);
		ibBack.setVisibility(View.VISIBLE);
		ibOrder = (Button) findViewById(R.id.top_v1_ib_right);
		ibOrder.setBackgroundResource(R.drawable.top_order);
		if(order==1){
			ibOrder.setText(R.string.btn_order_person);
		}else{
			ibOrder.setText(R.string.btn_order_company);
		}
		ibOrder.setVisibility(View.VISIBLE);
		ibOrder.setFocusable(true);
		ibOrder.setFocusableInTouchMode(true);
		// keyword
		etKeyword = (FindEditText) findViewById(R.id.contact_list_et_keyword);
		if(keyword!=null){
			etKeyword.setText(keyword);
		}
		// list
		lvData = (ExpandableListView) findViewById(R.id.contact_list_lv_data);
		lvData.setGroupIndicator(null);
//		lvData.setDivider(null);
		lvData.setChildDivider(getResources().getDrawable(R.drawable.gray));
		adapter = new ContactListAdapter();

//		groupTitleLayout = (LinearLayout) findViewById(R.id.contact_list_group_title);
//		groupTitleLayout.setVisibility(View.GONE);
//		groupTitleLayout.bringToFront();
//		groupTitleLayout.setBackgroundColor(Color.GRAY);
//		groupTitle = (TextView) findViewById(R.id.group_title);
	}

	/**
	 * 横屏
	 */
	private void initWidget2() {
		// top
		TextView tvTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		tvTitle.setText(R.string.title_contact_list);
		ibBack = (Button) findViewById(R.id.top_v1_ib_left);
		ibBack.setBackgroundResource(R.drawable.top_back);
		ibBack.setText(R.string.btn_title_back);
		ibBack.setVisibility(View.VISIBLE);
		
		gallery = (CustomGallery) findViewById(R.id.gallery);
		gallery.setSpacing(20);
		imageAdapter = new ImageAdapter(this);
	}

	/**
	 * 
	 */
	private void initListener() {

		// 返回按钮监听
		ibBack.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View view) {
				// 调用返回逻辑
				ibBackOnClick();
			}
		});
		// 排序按钮监听
		ibOrder.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View view) {
				// 调用排序逻辑
				ibOrderOnClick();
			}
		});
		// 搜索文本监听
		etKeyword.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				keyword = etKeyword.getText().toString();
				initContactList();
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

		lvData.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (kanaList == null || kanaList.isEmpty()) {
					return;
				}
				GroupInfo groupInfo = findGroup(firstVisibleItem);
				group = groupInfo.getGroupIndex();
				if (group < 0) {
					group = 0;
				}
//				groupTitleLayout.setVisibility(View.VISIBLE);
//				groupTitle.setText((String) kanaList.get(group).get("index"));
				
				first = firstVisibleItem;
			}

		});

		lvData.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 跳详情带参数
				Bundle bundle = new Bundle();
				bundle.putString(Constants.TANT_CARD_ID,
						((List<ContactListVO>) kanaList.get(groupPosition).get(
								"contact")).get(childPosition).getTantcardId());
				bundle.putString(Constants.FROM_KEY,
						Constants.FROM_VAL_CONTACT_LIST);
				
				//当前的条件带过去，返回的时候恢复用
				bundle.putInt(Constants.ORDER,order);
				bundle.putString(Constants.KEYWORD, keyword);
				
				Intent intent = new Intent();
				intent.putExtras(bundle);
				goToAnyActivity(ContactDetail.class, intent, false);
				return false;
			}
		});
	}

	private void initListener2() {
		// 返回按钮监听
		ibBack.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View view) {
				// 调用返回逻辑
				ibBackOnClick();
			}
		});
		
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position>=contactList.size()){
					return;
				}
				String tantcardId = contactList.get(position).getTantcardId();
				Bundle bundle = new Bundle();
				bundle.putString(Constants.TANT_CARD_ID,
						tantcardId);
				bundle.putString(Constants.FROM_KEY,
						Constants.FROM_VAL_CONTACT_LIST);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				goToAnyActivity(ContactDetail.class, intent, false);
			}
		}) ;
	}
	
	/**
	 * 
	 */
	private void ibBackOnClick() {
		goToMenuActivity(true);
	}

	/**
	 * 
	 */
	private void ibOrderOnClick() {

		if (1 == order) {
			// 会社顺:
			order++;
			ibOrder.setText(R.string.btn_order_company);
			initContactList();
		} else if (2 == order) {
			// 名前顺:
			order--;
			ibOrder.setText(R.string.btn_order_person);
			initContactList();
		}
	}

	/**
	 * 
	 */
	private void initContactList() {
		//加载数据
		reloadContactList();
		//展示
		updateView();
		//生成索引
		initIndex();
	}
	
	private void relocate(){
		int pos = findPositionByTantcardId();
		lvData.setSelection(pos);
	}
	
	private void reloadContactList(){
		// 加载联系信息
		kanaList = contactService.getKanaIndex(keyword, order); // 片假名列表
	}
	
	private void updateView(){
		//隐藏固定的组标题
//		groupTitleLayout.setVisibility(View.GONE);

		lvData.setCacheColorHint(0);
		lvData.setAdapter(adapter);

		if (kanaList != null && kanaList.size() > 0) {
			for (int i = 0; i < kanaList.size(); i++) {
				lvData.expandGroup(i);
			}
		}
	}
	
	private void initIndex(){
		LinearLayout layout = (LinearLayout) findViewById(R.id.contact_list_index);
		layout.removeAllViews();
		
		if(kanaList==null || kanaList.size()==0){
			return;
		}
		// 添加索引列表
		ContactIndexView indexView = new ContactIndexView(ContactList.this,
				lvData, kanaList, density);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.width = (int)(20*density);
		indexView.setLayoutParams(lp);
		layout.addView(indexView);
		layout.bringToFront();
	}

	private void initContactList2() {
		//重新加载数据
		reloadContactList2();
		//展示
		updateView2();
	}
	
	private void reloadContactList2(){
		contactList = contactService.queryContactList(keyword, order);
	}
	
	private void updateView2(){
		gallery.setUnselectedAlpha(1.0f);
		gallery.setAdapter(imageAdapter);
	}
	
	private void relocate2(){
		int pos = findPositionByTantcardId();
		gallery.setSelection(pos);
	}
	
	private int findPositionByTantcardId(){
		if(curTantcardId==null){
			return 0;
		}
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if(contactList!=null){
				int len = contactList.size();
				for(int i=0;i<len;i++){
					if(contactList.get(i).getTantcardId().equalsIgnoreCase(curTantcardId)){
						return i;
					}
				}
			}
		} else {
			if(kanaList!=null){
				for(Map<String,Object> map:kanaList){
					List<ContactListVO> contact = (List<ContactListVO>)map.get("contact");
					if(contact!=null){
						int len = contact.size();
						for(int i=0;i<len;i++){
							if(contact.get(i).getTantcardId().equalsIgnoreCase(curTantcardId)){
								return i+(Integer)map.get("start")+1;
							}
						}
					}
				}
			}
		}
		
		return 0;
	}

	private GroupInfo findGroup(int firstItem) {
		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setChildIndex(-1);
		groupInfo.setGroupIndex(-1);
		
		if (kanaList != null && !kanaList.isEmpty()) {
			for (int i = 0; i < kanaList.size(); i++) {
				Map<String, Object> map = kanaList.get(i);
				if ((Integer) map.get("max") >= firstItem) {
					groupInfo.setGroupIndex(i);
					break;
				}
			}
		}

		if (groupInfo.getGroupIndex() >= 0) {
			int max = (Integer) kanaList.get(groupInfo.getGroupIndex()).get(
					"max");
			int diff = max + 1 - firstItem;
			int size = ((List<ContactListVO>) kanaList.get(
					groupInfo.getGroupIndex()).get("contact")).size();
			groupInfo.setChildIndex(size - diff);
		}

		if (groupInfo.getGroupIndex() < 0) {
			groupInfo.setGroupIndex(0);
		}

		return groupInfo;
	}

	class ViewHolder {
		TextView nameText;
		TextView companyText;
	}

	public class ContactListAdapter extends BaseExpandableListAdapter {
		LayoutInflater inflater = null;

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return ((List<ContactListVO>) kanaList.get(groupPosition).get(
					"contact")).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				if (inflater == null) {
					inflater = (LayoutInflater) ContactList.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater
						.inflate(R.layout.contact_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.nameText = (TextView) convertView
						.findViewById(R.id.contact_list_item_tv_fullname);
				viewHolder.companyText = (TextView) convertView
						.findViewById(R.id.contact_list_item_tv_company);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.nameText.setText(((List<ContactListVO>) kanaList.get(
					groupPosition).get("contact")).get(childPosition)
					.getFullName());
			viewHolder.companyText.setText(((List<ContactListVO>) kanaList.get(
					groupPosition).get("contact")).get(childPosition)
					.getCompany());

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return ((List<ContactListVO>) kanaList.get(groupPosition).get(
					"contact")).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return kanaList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			if(kanaList==null){
				return 0;
			}
			return kanaList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				if (inflater == null) {
					inflater = (LayoutInflater) ContactList.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(
						R.layout.contact_list_group_title, null);
				viewHolder = new ViewHolder();
				viewHolder.nameText = (TextView) convertView
						.findViewById(R.id.group_title);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			convertView.setVisibility(View.VISIBLE);
			convertView.setBackgroundColor(Color.GRAY);
			String groupName = (String) ((Map<String, Object>) getGroup(groupPosition))
					.get("index");
			viewHolder.nameText.setText(groupName);
			convertView.setOnClickListener(null);
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
			// TypedArray a =
			// mContext.obtainStyledAttributes(R.styleable.Gallery);
		}

		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);
			// 取图片
			if (contactList != null && position < contactList.size()) {
				Bitmap bitmap = contactService
						.getUserImgByTantcardId(contactList.get(position)
								.getTantcardId());
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setLayoutParams(new Gallery.LayoutParams(widthPixels/2, heightPixels*4/5));
			return imageView;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState){
		savedInstanceState.putInt("order", order);
		savedInstanceState.putString("keyword", keyword);
		
		if(orientation==Configuration.ORIENTATION_LANDSCAPE){
			int pos = gallery.getFirstVisiblePosition();
			if(contactList!=null && pos>=0 && pos<contactList.size()){
				savedInstanceState.putString("tantcard_id", contactList.get(pos).getTantcardId());
			}
		}else{
			int pos = lvData.getFirstVisiblePosition();
			GroupInfo groupInfo = findGroup(pos);
			if(kanaList!=null && groupInfo.getGroupIndex()>=0 && groupInfo.getGroupIndex()<kanaList.size()){
				List<ContactListVO> contact = (List<ContactListVO>)kanaList.get(groupInfo.getGroupIndex()).get("contact");
				if(contact!=null && groupInfo.getChildIndex()<contact.size()){
					if(groupInfo.getChildIndex()<0){
						savedInstanceState.putString("tantcard_id", contact.get(0).getTantcardId());
					}else{
						savedInstanceState.putString("tantcard_id", contact.get(groupInfo.getChildIndex()).getTantcardId());
					}
				}
			}
		}
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		restoreInstanceState(savedInstanceState);
	}
	
	private class LoadContactAsyncTask extends LoadDataAsyncTask{
		public LoadContactAsyncTask(){
		}
		
		@Override
		protected void onPreExecute(){
			showProgressDialog();
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			Integer o = (Integer)arg0[0];
			if (o == Configuration.ORIENTATION_LANDSCAPE) {
				reloadContactList2();
			} else {
				reloadContactList();
			}
			return o;
		}
		
		@Override  
        protected void onPostExecute(Object result) {
			hideProgressDialog();
			Integer o = (Integer)result;
			if (o == Configuration.ORIENTATION_LANDSCAPE) {
				updateView2();
				relocate2();
			} else {
				updateView();
				initIndex();
				relocate();
			}
        }  
	}
}
