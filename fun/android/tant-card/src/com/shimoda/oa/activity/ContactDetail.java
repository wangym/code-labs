/**
 * 联系人详情
 */
package com.shimoda.oa.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView.OnEditorActionListener;

import com.shimoda.oa.R;
import com.shimoda.oa.model.Contact;
import com.shimoda.oa.model.MenuItem;
import com.shimoda.oa.service.BookmarkService;
import com.shimoda.oa.service.CallLogService;
import com.shimoda.oa.service.ContactService;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.Constants;
import com.shimoda.oa.util.EnvironmentUtil;
import com.shimoda.oa.util.StringUtil;
import com.shimoda.oa.util.exporter.DataExporter;

/**
 * @author yumin
 * 
 */
@SuppressWarnings("unused")
public class ContactDetail extends BaseActivity {

	/**
	 * 
	 */
	private final int[] detailTitle = { R.string.detail_title_basic,
			R.string.detail_title_contact1, R.string.detail_title_contact2,
			R.string.detail_title_contact3, R.string.detail_title_attachment };
	private Map<Integer, String> detailKey = null;
	private Map<Integer, List<String>> detailVal = null;

	/**
	 * 
	 */
	private String tantcardId = "";
	private String from = "";
	/**
	 * 列表传过来的状态，返回的时候传回去
	 */
	private int order;
	private String keyword;
	
	private Contact contact = null;
	private List<Map<String, String>> parentData = null;
	private List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
	private List<Map<String, String>> menuData = new ArrayList<Map<String, String>>();
	private Map<String, List<Map<String, String>>> childMenuData = new HashMap<String, List<Map<String, String>>>();
	private Map<String, List<Map<String, String>>> opMenuData = new HashMap<String, List<Map<String, String>>>();

	/**
	 * 
	 */
	private Button ibBack;
	private Button btnMenu;
	private ImageView ivNameCard;
	// private ScrollView verticalScroll;
	// private HorizontalScrollView horizontalScroll;
	private ContactDetailAdapter adapter;
	private ExpandableListView elData;
	private AlertDialog menuDlg;
	private View datePick;
	private View dataList;

	private ImageButton btnHomePage;
	private ImageButton btnPhone;
	private ImageButton btnEmail;
	private ImageButton btnSkype;
	private ImageButton btnGoogle;

	private Bitmap image;
	private boolean isEdit = false;
	private String selectedDate;
	private Map<String, String> update = new HashMap<String, String>();

	private float scaleRate = 0f;

	private ContactService contactService;
	private BookmarkService bookmarkService;

	private GestureDetector gestureDetector;

	private int orientation = Configuration.ORIENTATION_LANDSCAPE;

	/**
	 * for restore instance state
	 */
	private int first;

	// ====================
	// override methods
	// ====================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// init data
		Bundle bundle = getIntent().getExtras();
		tantcardId = bundle.getString(Constants.TANT_CARD_ID); // 用于获取数据
		from = bundle.getString(Constants.FROM_KEY); // 用于返回跳转
		
		order = bundle.getInt(Constants.ORDER);
		keyword = bundle.getString(Constants.KEYWORD);
		
		bookmarkService = new BookmarkService(this);
		contactService = new ContactService(this);
		contact = contactService.getContactByTantcardId(tantcardId);
		if (null == contact) {
			ibBackOnClick(); // 返回按钮逻辑
			return;
		}

		super.onCreate(savedInstanceState);
		orientation = this.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			callTopBar(R.layout.contact_detail_2, R.layout.top_v1);
		} else {
			callTopBar(R.layout.contact_detail, R.layout.top_v1);
		}

		initWidget();
		initListener();
		initNameCard(); // 名片图片区
		initContactData(true); // 资料数据区
		initSysMenuData(); // 点菜单按出来的菜单
		initOpMenuData();// 初始化下面的按钮菜单
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if(isEdit){
			//编辑状态，返回
			return;
		}
		
		//非编辑状态
		int mCurrentOrientation = newConfig.orientation;
		System.out.println("mCurrentOrientation:"+mCurrentOrientation);
		if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
			//切换到竖屏
			setContentView(R.layout.contact_detail);
		} else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			//切换到横屏
			setContentView(R.layout.contact_detail_2);
		}
		
		//初始显示的时候会重置first，所以这里要保存下来
//		int firstOld = first;
		
		initWidget();
		initListener();
		initNameCard(); // 名片图片区
		initContactData(false); // 不重新初始化，直接更新view
		//恢复到第一个可见的项
		elData.setSelection(0);
//		elData.setSelection(firstOld); 位置比较难保留，定位到第一个
	}
	
	protected void onResume(){
		super.onResume();
		
		int mCurrentOrientation = getResources().getConfiguration().orientation;
		System.out.println("mCurrentOrientation2:"+mCurrentOrientation);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void initWidget() {
		getSize();
		int width = widthPixels;
		int height = heightPixels;

		// top
		TextView tvTitle = (TextView) findViewById(R.id.top_v1_tv_center);
		tvTitle.setText("");
		ibBack = (Button) findViewById(R.id.top_v1_ib_left);
		ibBack.setBackgroundResource(R.drawable.top_back);
		if (!StringUtil.isEmpty(from)) {
			if (Constants.FROM_VAL_CONTACT_LIST.equalsIgnoreCase(from)) {
				ibBack.setText(R.string.btn_title_back);
			} else if (Constants.FROM_VAL_BOOKMARK.equalsIgnoreCase(from)) {
				ibBack.setText(R.string.title_bookmark);
			} else if (Constants.FROM_VAL_CALLLOG.equalsIgnoreCase(from)) {
				ibBack.setText(R.string.title_calllog);
			}
		} else {
			ibBack.setText(R.string.btn_title_back);
		}
		ibBack.setVisibility(ImageButton.VISIBLE);

		btnMenu = (Button) this.findViewById(R.id.top_v1_ib_right);
		btnMenu.setText(R.string.btn_title_menu);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setBackgroundResource(R.drawable.top_order);

		// name card
		LinearLayout layoutNameCard = (LinearLayout) findViewById(R.id.contact_detail_name_card);
		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(
		// width, (int)(height*2/6));
		// layoutNameCard.setLayoutParams(layoutParams);
		ivNameCard = (ImageView) findViewById(R.id.contact_detail_iv_name_card);

		// verticalScroll = (ScrollView)
		// findViewById(R.id.contact_detail_vertical_scrollview);
		// horizontalScroll =
		// (HorizontalScrollView)findViewById(R.id.contact_detail_horizontal_scrollview);

		// data list
		elData = (ExpandableListView) findViewById(R.id.contact_detail_el_data);
		elData.setGroupIndicator(null);
		elData.setFocusable(true);
		// LinearLayout layoutData = (LinearLayout)
		// findViewById(R.id.contact_detail_data);
		// layoutParams = new LinearLayout.LayoutParams(width,
		// (int)(height*3/6));
		// layoutData.setLayoutParams(layoutParams);

		dataList = findViewById(R.id.contact_detail_data_ll);
		dataList.setVisibility(View.VISIBLE);

		datePick = findViewById(R.id.contact_detail_datepick);
		datePick.setVisibility(View.GONE);
	}

	/**
	 * 
	 */
	private void initListener() {

		// 返回按钮监听
		ibBack.setOnClickListener(new ImageButton.OnClickListener() {

			public void onClick(View view) {
				// 调用返回逻辑
				ibBackOnClick();
			}
		});

		// 菜单按钮
		btnMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				btnMenu.setFocusableInTouchMode(true);
				btnMenu.requestFocus();
				btnMenu.setFocusableInTouchMode(false);

				// 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) btnMenu
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(btnMenu
							.getApplicationWindowToken(), 0);
				}

				if (menuDlg != null && menuDlg.isShowing()) {
					menuDlg.dismiss();
				} else {
					// 初始化菜单数据
					if (isEdit) {
						initEditMenuData();
					} else {
						initSysMenuData();
					}

					showMenu();
				}
			}
		});

		// 名片长按监听

		ivNameCard.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View paramView) {
				// 长按后跳转至名片全屏查看页
				Bundle bundle = new Bundle();
				bundle.putString(Constants.TANT_CARD_ID, tantcardId);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				goToAnyActivity(UserImg.class, intent, false);
				return false;
			}
		});

		gestureDetector = new GestureDetector(new SimpleGesture());
		if (EnvironmentUtil.aboveDonut()) {
			ivNameCard.setOnTouchListener(new MulitPointTouchListener());
		} else {
			ivNameCard.setOnTouchListener(new MulitClickListener());
		}

		// 滚动的时候，记住当前显示的位置
		elData.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				first = firstVisibleItem;
			}
		});

		// elData.setOnChildClickListener(new OnChildClickListener() {
		// @Override
		// public boolean onChildClick(ExpandableListView expandablelistview,
		// View view, int i, int j, long l) {
		// if(isEdit){
		// if(i==0){
		// elData.setSelection(j+1);
		// }else if(i==1){
		// elData.setSelection(j+11);
		// }else if(i==2){
		// elData.setSelection(j+21);
		// }else if(i==3){
		// elData.setSelection(j+31);
		// }else if(i==4){
		// elData.setSelection(j+41);
		// }
		// }
		// return false;
		// }
		// });

		btnHomePage = (ImageButton) this
				.findViewById(R.id.contact_detail_ib_home);
		btnHomePage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!opMenuData.containsKey("website")) {
					showToast(getString(R.string.detail_op_tips_website_empty));
				} else {
					showOpMenu("website");
				}
			}
		});

		btnPhone = (ImageButton) this
				.findViewById(R.id.contact_detail_ib_phone);
		btnPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!opMenuData.containsKey("tel")) {
					showToast(getString(R.string.detail_op_tips_tel_empty));
				} else {
					showOpMenu("tel");
				}
			}
		});

		btnEmail = (ImageButton) this.findViewById(R.id.contact_detail_ib_mail);
		btnEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!opMenuData.containsKey("email")) {
					showToast(getString(R.string.detail_op_tips_email_empty));
				} else {
					showOpMenu("email");
				}
			}
		});

		btnSkype = (ImageButton) this
				.findViewById(R.id.contact_detail_ib_skype);
		btnSkype.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!opMenuData.containsKey("skype")) {
					showToast(getString(R.string.detail_op_tips_skype_empty));
				} else {
					showOpMenu("skype");
				}
			}
		});

		btnGoogle = (ImageButton) this
				.findViewById(R.id.contact_detail_ib_google);
		btnGoogle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!opMenuData.containsKey("address")) {
					showToast(getString(R.string.detail_op_tips_address_empty));
				} else {
					showOpMenu("address");
				}
			}
		});
	}

	/**
	 * 
	 */
	private void ibBackOnClick() {

		if (!StringUtil.isEmpty(from)) {
			if (Constants.FROM_VAL_CONTACT_LIST.equalsIgnoreCase(from)) {
				Bundle bundle = new Bundle();
				bundle.putString(Constants.TANT_CARD_ID, tantcardId);
				bundle.putInt(Constants.ORDER, order);
				bundle.putString(Constants.KEYWORD, keyword);
				Intent intent = new Intent();
				intent.putExtras(bundle);

				goToAnyActivity(ContactList.class, intent, true);
			} else if (Constants.FROM_VAL_BOOKMARK.equalsIgnoreCase(from)) {
				goToAnyActivity(BookmarkList.class, true);
			} else if (Constants.FROM_VAL_CALLLOG.equalsIgnoreCase(from)) {
				goToAnyActivity(CallLogList.class, true);
			}
		} else {
			goToMenuActivity(true);
		}
	}

	/**
	 * 名片图片区域
	 */
	private void initNameCard() {
		byte[] userImg = contact.getUserImg();
		if (null != userImg && 0 < userImg.length) {
			image = BitmapFactory.decodeByteArray(userImg, 0, userImg.length);
			ivNameCard.setImageBitmap(image);
		}
	}

	/**
	 * 联系数据区域
	 */
	private void initContactData(boolean reload) {
		if(reload){
			initParentData();
			initChildData();
		}

		adapter = new ContactDetailAdapter();
		elData.setAdapter(adapter);

		if (parentData != null && parentData.size() > 0) {
			for (int i = 0; i < parentData.size(); i++) {
				elData.expandGroup(i);
			}
		}
	}

	/**
	 * 初始父节点数据
	 */
	private void initParentData() {

		parentData = new ArrayList<Map<String, String>>();
		for (int i = 0; i < 5; i++) {
			HashMap<String, String> parent = new HashMap<String, String>();
			parent.put("parent", getString(detailTitle[i]));
			parentData.add(parent);
		}
	}

	private void initChildMenuData() {
		if (isEdit) {
			return;
		}
		// 清空操作菜单数据
		childMenuData.clear();
		List<Map<String, String>> subMenu = null;
		Map<String, String> menuItem = null;
		if (!StringUtil.isEmpty(contact.getCompany())
				|| !StringUtil.isEmpty(contact.getUrl())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			if (!StringUtil.isEmpty(contact.getUrl())) {
				menuItem = new HashMap<String, String>();
				menuItem.put("type", String.valueOf(MenuItem.TYPE_WEBSITE));
				menuItem.put("title", contact.getUrl());
				menuItem.put("data", contact.getUrl());
				subMenu.add(menuItem);
			}
			if (!StringUtil.isEmpty(contact.getCompany())) {
				menuItem = new HashMap<String, String>();
				menuItem.put("type", String.valueOf(MenuItem.TYPE_COMPANY));
				menuItem.put("title", contact.getCompany() + " "
						+ getString(R.string.detail_menu_item_company));
				menuItem.put("data", contact.getCompany());
				subMenu.add(menuItem);
			}
			childMenuData.put("company", subMenu);
		}

		if (!StringUtil.isEmpty(contact.getState1())
				|| !StringUtil.isEmpty(contact.getCity1())
				|| !StringUtil.isEmpty(contact.getAddress1())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_ADDRESS));
			menuItem.put("title", contact.getState1() + contact.getCity1()
					+ contact.getAddress1());
			menuItem.put("data", contact.getState1() + contact.getCity1()
					+ contact.getAddress1());
			subMenu.add(menuItem);
			childMenuData.put("address1", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getTel1())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_TEL));
			menuItem.put("title", contact.getTel1());
			menuItem.put("data", contact.getTel1());
			subMenu.add(menuItem);
			childMenuData.put("tel1", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getMobilephone1())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_TEL));
			menuItem.put("title", contact.getMobilephone1());
			menuItem.put("data", contact.getMobilephone1());
			subMenu.add(menuItem);
			childMenuData.put("mobile1", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getEmail1())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_EMAIL));
			menuItem.put("title", contact.getEmail1());
			menuItem.put("data", contact.getEmail1());
			subMenu.add(menuItem);
			childMenuData.put("email1", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getSkype1())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_SKYPE));
			menuItem.put("title", contact.getSkype1());
			menuItem.put("data", contact.getSkype1());
			subMenu.add(menuItem);
			childMenuData.put("skype1", subMenu);
		}

		if (!StringUtil.isEmpty(contact.getState2())
				|| !StringUtil.isEmpty(contact.getCity2())
				|| !StringUtil.isEmpty(contact.getAddress2())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_ADDRESS));
			menuItem.put("title", contact.getState2() + contact.getCity2()
					+ contact.getAddress2());
			menuItem.put("data", contact.getState2() + contact.getCity2()
					+ contact.getAddress2());
			subMenu.add(menuItem);
			childMenuData.put("address2", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getTel2())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_TEL));
			menuItem.put("title", contact.getTel2());
			menuItem.put("data", contact.getTel2());
			subMenu.add(menuItem);
			childMenuData.put("tel2", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getMobilephone2())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_TEL));
			menuItem.put("title", contact.getMobilephone2());
			menuItem.put("data", contact.getMobilephone2());
			subMenu.add(menuItem);
			childMenuData.put("mobile2", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getEmail2())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_EMAIL));
			menuItem.put("title", contact.getEmail2());
			menuItem.put("data", contact.getEmail2());
			subMenu.add(menuItem);
			childMenuData.put("email2", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getSkype2())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_SKYPE));
			menuItem.put("title", contact.getSkype2());
			menuItem.put("data", contact.getSkype2());
			subMenu.add(menuItem);
			childMenuData.put("skype2", subMenu);
		}

		if (!StringUtil.isEmpty(contact.getState3())
				|| !StringUtil.isEmpty(contact.getCity3())
				|| !StringUtil.isEmpty(contact.getAddress3())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_ADDRESS));
			menuItem.put("title", contact.getState3() + contact.getCity3()
					+ contact.getAddress3());
			menuItem.put("data", contact.getState3() + contact.getCity3()
					+ contact.getAddress3());
			subMenu.add(menuItem);
			childMenuData.put("address3", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getTel3())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_TEL));
			menuItem.put("title", contact.getTel3());
			menuItem.put("data", contact.getTel3());
			subMenu.add(menuItem);
			childMenuData.put("tel3", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getMobilephone3())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_TEL));
			menuItem.put("title", contact.getMobilephone3());
			menuItem.put("data", contact.getMobilephone3());
			subMenu.add(menuItem);
			childMenuData.put("mobile3", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getEmail3())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_EMAIL));
			menuItem.put("title", contact.getEmail3());
			menuItem.put("data", contact.getEmail3());
			subMenu.add(menuItem);
			childMenuData.put("email3", subMenu);
		}
		if (!StringUtil.isEmpty(contact.getSkype3())) {
			// 添加菜单
			subMenu = new ArrayList<Map<String, String>>();
			menuItem = new HashMap<String, String>();
			menuItem.put("type", String.valueOf(MenuItem.TYPE_SKYPE));
			menuItem.put("title", contact.getSkype3());
			menuItem.put("data", contact.getSkype3());
			subMenu.add(menuItem);
			childMenuData.put("skype3", subMenu);
		}
	}

	/**
	 * 初始子节点数据
	 */
	private void initChildData() {
		// 初始化子节点菜单
		initChildMenuData();

		// 清空数据
		childData.clear();
		// 基本信息
		List<Map<String, String>> child = new ArrayList<Map<String, String>>();
		HashMap<String, String> map = null;
		if (!isEdit) {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_name));
			map.put("childVal", contact.getFullName() + "\n"
					+ contact.getKanaFullName());
			map.put("key", "fullname");
			child.add(map);
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_company));
			map.put("childVal", contact.getCompany() + "\n"
					+ contact.getCompanyKana());
			if (childMenuData.containsKey("company")) {
				map.put("menu", "company");
			}
			map.put("key", "company");
			child.add(map);
		} else {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_lastname));
			map.put("childVal", contact.getLastName());
			map.put("key", "lastname");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_firstname));
			map.put("childVal", contact.getFirstName());
			map.put("key", "firstname");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_lastname_kana));
			map.put("childVal", contact.getKanaLastName());
			map.put("key", "lastname_kana");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_firstname_kana));
			map.put("childVal", contact.getKanaFirstName());
			map.put("key", "firstname_kana");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_company));
			map.put("childVal", contact.getCompany());
			map.put("key", "company");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_company_kana));
			map.put("childVal", contact.getCompanyKana());
			map.put("key", "company_kana");
			child.add(map);
		}

		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_affiliation));
		map.put("childVal", contact.getAffiliation());
		map.put("key", "affiliation");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_role));
		map.put("childVal", contact.getRole());
		map.put("key", "role");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_website));
		map.put("childVal", contact.getUrl());
		map.put("key", "url");
		if (childMenuData.containsKey("company")) {
			map.put("menu", "company");
		}
		child.add(map);
		childData.add(child);

		// 联系信息1
		child = new ArrayList<Map<String, String>>();

		if (isEdit) {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_zip));
			map.put("childVal", contact.getZip1());
			map.put("key", "zip1");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_state));
			map.put("childVal", contact.getState1());
			map.put("key", "state1");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_city));
			map.put("childVal", contact.getCity1());
			map.put("key", "city1");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_address));
			map.put("childVal", contact.getAddress1());
			map.put("key", "address1");
			child.add(map);
		} else {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_full_address));
			map.put("childVal", contact.getZip1() + "\n" + contact.getState1()
					+ contact.getCity1() + contact.getAddress1());
			if (childMenuData.containsKey("address1")) {
				map.put("menu", "address1");
			}
			map.put("key", "address1");
			child.add(map);
		}

		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_tel));
		map.put("childVal", contact.getTel1());
		if (childMenuData.containsKey("tel1")) {
			map.put("menu", "tel1");
		}
		map.put("key", "tel1");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_fax));
		map.put("childVal", contact.getFax1());
		map.put("key", "fax1");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_mobile));
		map.put("childVal", contact.getMobilephone1());
		if (childMenuData.containsKey("mobile1")) {
			map.put("menu", "mobile1");
		}
		map.put("key", "mobile1");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_email));
		map.put("childVal", contact.getEmail1());
		if (childMenuData.containsKey("email1")) {
			map.put("menu", "email1");
		}
		map.put("key", "email1");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_skype));
		map.put("childVal", contact.getSkype1());
		map.put("key", "skype1");
		if (childMenuData.containsKey("skype1")) {
			map.put("menu", "skype1");
		}
		child.add(map);
		childData.add(child);

		// 联系信息2
		child = new ArrayList<Map<String, String>>();
		if (isEdit) {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_zip));
			map.put("childVal", contact.getZip2());
			map.put("key", "zip2");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_state));
			map.put("childVal", contact.getState2());
			map.put("key", "state2");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_city));
			map.put("childVal", contact.getCity2());
			map.put("key", "city2");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_address));
			map.put("childVal", contact.getAddress2());
			map.put("key", "address2");
			child.add(map);
		} else {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_full_address));
			map.put("childVal", contact.getZip2() + "\n" + contact.getState2()
					+ contact.getCity2() + contact.getAddress2());
			if (childMenuData.containsKey("address2")) {
				map.put("menu", "address2");
			}
			map.put("key", "address2");
			child.add(map);
		}
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_tel));
		map.put("childVal", contact.getTel2());
		if (childMenuData.containsKey("tel2")) {
			map.put("menu", "tel2");
		}
		map.put("key", "tel2");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_fax));
		map.put("childVal", contact.getFax2());
		map.put("key", "fax2");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_mobile));
		map.put("childVal", contact.getMobilephone2());
		if (childMenuData.containsKey("mobile2")) {
			map.put("menu", "mobile2");
		}
		map.put("key", "mobile2");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_email));
		map.put("childVal", contact.getEmail2());
		if (childMenuData.containsKey("email2")) {
			map.put("menu", "email2");
		}
		map.put("key", "email2");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_skype));
		map.put("childVal", contact.getSkype2());
		map.put("key", "skype2");
		if (childMenuData.containsKey("skype2")) {
			map.put("menu", "skype2");
		}
		child.add(map);
		childData.add(child);

		// 联系信息3
		child = new ArrayList<Map<String, String>>();
		if (isEdit) {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_zip));
			map.put("childVal", contact.getZip3());
			map.put("key", "zip3");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_state));
			map.put("childVal", contact.getState3());
			map.put("key", "state3");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_city));
			map.put("childVal", contact.getCity3());
			map.put("key", "city3");
			child.add(map);

			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_address));
			map.put("childVal", contact.getAddress3());
			map.put("key", "address3");
			child.add(map);
		} else {
			map = new HashMap<String, String>();
			map.put("childKey", getString(R.string.detail_info_full_address));
			map.put("childVal", contact.getZip3() + "\n" + contact.getState3()
					+ contact.getCity3() + contact.getAddress3());
			if (childMenuData.containsKey("address3")) {
				map.put("menu", "address3");
			}
			map.put("key", "address3");
			child.add(map);
		}
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_tel));
		map.put("childVal", contact.getTel3());
		if (childMenuData.containsKey("tel3")) {
			map.put("menu", "tel3");
		}
		map.put("key", "tel3");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_fax));
		map.put("childVal", contact.getFax3());
		map.put("key", "fax3");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_mobile));
		map.put("childVal", contact.getMobilephone3());
		if (childMenuData.containsKey("mobile3")) {
			map.put("menu", "mobile3");
		}
		map.put("key", "mobile3");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_email));
		map.put("childVal", contact.getEmail3());
		if (childMenuData.containsKey("email3")) {
			map.put("menu", "email3");
		}
		map.put("key", "email3");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_skype));
		map.put("childVal", contact.getSkype3());
		map.put("key", "skype3");
		if (childMenuData.containsKey("skype3")) {
			map.put("menu", "skype3");
		}
		child.add(map);
		childData.add(child);

		// 其他信息
		child = new ArrayList<Map<String, String>>();
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_classification1));
		map.put("childVal", contact.getClassification1());
		map.put("key", "classification1");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_classification2));
		map.put("childVal", contact.getClassification2());
		map.put("key", "classification2");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_meetingday));
		map.put("childVal", contact.getMeetingDay());
		map.put("key", "meetingday");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_registrationday));
		map.put("childVal", contact.getRegistrationDay());
		map.put("key", "registrationday");
		child.add(map);
		map = new HashMap<String, String>();
		map.put("childKey", getString(R.string.detail_info_note));
		map.put("childVal", contact.getNote());
		map.put("key", "note");
		child.add(map);
		childData.add(child);
	}

	private void initSysMenuData() {
		// 初始化系统菜单
		menuData.clear();

		Map<String, String> item = new HashMap<String, String>();
		if (bookmarkService.bookmarkExists(tantcardId)) {
			item.put("type", String.valueOf(MenuItem.TYPE_BOOKMARK_DEL));
			item
					.put("title",
							getString(R.string.detail_menu_item_bookmark_del));
		} else {
			item.put("type", String.valueOf(MenuItem.TYPE_BOOKMARK_ADD));
			item
					.put("title",
							getString(R.string.detail_menu_item_bookmark_add));
		}
		menuData.add(item);

		item = new HashMap<String, String>();
		item.put("type", String.valueOf(MenuItem.TYPE_EXPORT));
		item.put("title", getString(R.string.detail_menu_item_export));
		menuData.add(item);

		item = new HashMap<String, String>();
		item.put("type", String.valueOf(MenuItem.TYPE_EDIT));
		item.put("title", getString(R.string.detail_menu_item_edit));
		menuData.add(item);

		item = new HashMap<String, String>();
		item.put("type", String.valueOf(MenuItem.TYPE_DEL));
		item.put("title", getString(R.string.detail_menu_item_del));
		menuData.add(item);
	}

	private void initOpMenuData() {
		// 初始化系统菜单
		opMenuData.clear();

		List<Map<String, String>> list = null;
		Map<String, String> item = null;
		if (!StringUtil.isEmpty(contact.getUrl())
				|| !StringUtil.isEmpty(contact.getCompany())) {
			list = new ArrayList<Map<String, String>>();
			if (!StringUtil.isEmpty(contact.getUrl())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_WEBSITE));
				item.put("title", contact.getUrl());
				item.put("data", contact.getUrl());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getCompany())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_COMPANY));
				item.put("title", contact.getCompany() + " "
						+ getString(R.string.detail_menu_item_company));
				item.put("data", contact.getCompany());
				list.add(item);
			}

			opMenuData.put("website", list);
		}

		if (!StringUtil.isEmpty(contact.getTel1())
				|| !StringUtil.isEmpty(contact.getTel2())
				|| !StringUtil.isEmpty(contact.getTel3())
				|| !StringUtil.isEmpty(contact.getMobilephone1())
				|| !StringUtil.isEmpty(contact.getMobilephone2())
				|| !StringUtil.isEmpty(contact.getMobilephone3())) {
			list = new ArrayList<Map<String, String>>();
			if (!StringUtil.isEmpty(contact.getTel1())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_TEL));
				item.put("title", contact.getTel1());
				item.put("data", contact.getTel1());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getTel2())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_TEL));
				item.put("title", contact.getTel2());
				item.put("data", contact.getTel2());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getTel3())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_TEL));
				item.put("title", contact.getTel3());
				item.put("data", contact.getTel3());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getMobilephone1())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_TEL));
				item.put("title", contact.getMobilephone1());
				item.put("data", contact.getMobilephone1());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getMobilephone2())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_TEL));
				item.put("title", contact.getMobilephone2());
				item.put("data", contact.getMobilephone2());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getMobilephone3())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_TEL));
				item.put("title", contact.getMobilephone3());
				item.put("data", contact.getMobilephone3());
				list.add(item);
			}
			opMenuData.put("tel", list);
		}

		if (!StringUtil.isEmpty(contact.getEmail1())
				|| !StringUtil.isEmpty(contact.getEmail2())
				|| !StringUtil.isEmpty(contact.getEmail3())) {
			list = new ArrayList<Map<String, String>>();
			if (!StringUtil.isEmpty(contact.getEmail1())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_EMAIL));
				item.put("title", contact.getEmail1());
				item.put("data", contact.getEmail1());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getEmail2())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_EMAIL));
				item.put("title", contact.getEmail2());
				item.put("data", contact.getEmail2());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getEmail3())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_EMAIL));
				item.put("title", contact.getEmail3());
				item.put("data", contact.getEmail3());
				list.add(item);
			}
			opMenuData.put("email", list);
		}

		if (!StringUtil.isEmpty(contact.getSkype1())
				|| !StringUtil.isEmpty(contact.getSkype2())
				|| !StringUtil.isEmpty(contact.getSkype3())) {
			list = new ArrayList<Map<String, String>>();
			if (!StringUtil.isEmpty(contact.getSkype1())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_SKYPE));
				item.put("title", contact.getSkype1());
				item.put("data", contact.getSkype1());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getSkype2())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_SKYPE));
				item.put("title", contact.getSkype2());
				item.put("data", contact.getSkype2());
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getSkype3())) {
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_SKYPE));
				item.put("title", contact.getSkype3());
				item.put("data", contact.getSkype3());
				list.add(item);
			}
			opMenuData.put("skype", list);
		}

		if (!StringUtil.isEmpty(contact.getState1())
				|| !StringUtil.isEmpty(contact.getCity1())
				|| !StringUtil.isEmpty(contact.getAddress1())
				|| !StringUtil.isEmpty(contact.getState2())
				|| !StringUtil.isEmpty(contact.getCity2())
				|| !StringUtil.isEmpty(contact.getAddress2())
				|| !StringUtil.isEmpty(contact.getState3())
				|| !StringUtil.isEmpty(contact.getCity3())
				|| !StringUtil.isEmpty(contact.getAddress3())) {
			list = new ArrayList<Map<String, String>>();
			if (!StringUtil.isEmpty(contact.getState1())
					|| !StringUtil.isEmpty(contact.getCity1())
					|| !StringUtil.isEmpty(contact.getAddress1())) {
				String address = contact.getState1() + contact.getCity1()
						+ contact.getAddress1();
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_ADDRESS));
				item.put("title", address);
				item.put("data", address);
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getState2())
					|| !StringUtil.isEmpty(contact.getCity2())
					|| !StringUtil.isEmpty(contact.getAddress2())) {
				String address = contact.getState2() + contact.getCity2()
						+ contact.getAddress2();
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_ADDRESS));
				item.put("title", address);
				item.put("data", address);
				list.add(item);
			}
			if (!StringUtil.isEmpty(contact.getState3())
					|| !StringUtil.isEmpty(contact.getCity3())
					|| !StringUtil.isEmpty(contact.getAddress3())) {
				String address = contact.getState3() + contact.getCity3()
						+ contact.getAddress3();
				item = new HashMap<String, String>();
				item.put("type", String.valueOf(MenuItem.TYPE_ADDRESS));
				item.put("title", address);
				item.put("data", address);
				list.add(item);
			}
			opMenuData.put("address", list);
		}

		if (!opMenuData.containsKey("website")) {
			btnHomePage.setAlpha(100);
		} else {
			btnHomePage.setAlpha(255);
		}

		if (!opMenuData.containsKey("tel")) {
			btnPhone.setAlpha(100);
		} else {
			btnPhone.setAlpha(255);
		}

		if (!opMenuData.containsKey("email")) {
			btnEmail.setAlpha(100);
		} else {
			btnEmail.setAlpha(255);
		}

		if (!opMenuData.containsKey("skype")) {
			btnSkype.setAlpha(100);
		} else {
			btnSkype.setAlpha(255);
		}

		if (!opMenuData.containsKey("address")) {
			btnGoogle.setAlpha(100);
		} else {
			btnGoogle.setAlpha(255);
		}
	}

	private void initEditMenuData() {
		menuData.clear();
		Map<String, String> item = new HashMap<String, String>();
		item.put("type", String.valueOf(MenuItem.TYPE_EDIT_SAVE));
		item.put("title", getString(R.string.detail_menu_item_edit_save));
		menuData.add(item);

		item = new HashMap<String, String>();
		item.put("type", String.valueOf(MenuItem.TYPE_EDIT_GIVEUP));
		item.put("title", getString(R.string.detail_menu_item_edit_give_up));
		menuData.add(item);
	}

	private void showMenu() {
		LayoutInflater factory = LayoutInflater.from(ContactDetail.this);
		View dialogView = factory.inflate(R.layout.import_menu, null);
		// 设置标题
		setTextViewText(dialogView, R.id.menu_title,
				getString(R.string.export_menu_title));

		// 绑定数据
		ListView menuView = (ListView) dialogView.findViewById(R.id.menu_list);

		menuView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				menuDlg.dismiss();
				Map<String, String> map = menuData.get(position);
				int op = Integer.parseInt(map.get("type"));
				switch (op) {
				case 1:
					goToWebsite(map.get("data"));
					break;
				case 2:
					goToCompany(map.get("data"));
					break;
				case 3:
					goToTel(map.get("data"));
					break;
				case 4:
					goToEmail(map.get("data"));
					break;
				case 5:
					goToAddress(map.get("data"));
					break;
				case 6:
					bookmarkAdd();
					break;
				case 7:
					bookmarkDel();
					break;
				case 8:
					export();
					break;
				case 9:
					edit();
					break;
				case 10:
					del();
					break;
				case 11:
					editSave();
					break;
				case 12:
					editGiveUp();
					break;
				case 13:
					goToSkype(map.get("data"));
					break;
				}
			}
		});

		final String delMenuText = getString(R.string.detail_menu_item_del);
		SimpleAdapter adapter2 = new SimpleAdapter(getApplicationContext(),
				menuData, R.layout.import_menu_item, new String[] { "title" },
				new int[] { R.id.import_menu_item_btn });
		adapter2.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object obj, String s) {
				if(view instanceof TextView){
					TextView textView = (TextView)view;
					//如果是删除菜单,文本设置成红色
					if(delMenuText.equalsIgnoreCase((String)obj)){
						textView.setTextColor(Color.RED);
					}else{
						textView.setTextColor(Color.WHITE);
					}
				}
				return false;
			}
		});
		
		menuView.setAdapter(adapter2);

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

		menuDlg = new AlertDialog.Builder(ContactDetail.this).create();
		Window window = menuDlg.getWindow();
		window.setGravity(Gravity.BOTTOM);
		menuDlg.show();

		menuDlg.setContentView(dialogView);
	}

	private void showChildMenu(String menu) {
		if (!childMenuData.containsKey(menu)) {
			return;
		}
		menuData = childMenuData.get(menu);
		showMenu();
	}

	private void showOpMenu(String menu) {
		if (!opMenuData.containsKey(menu)) {
			return;
		}
		menuData = opMenuData.get(menu);
		showMenu();
	}

	private void goToWebsite(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(getString(R.string.detail_op_tips_website_empty));
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(data));
		try {
			startActivity(intent);
		} catch (Exception e) {
			showToast(getString(R.string.detail_op_tips_website_fail));
			e.printStackTrace();
		}
	}

	private void goToCompany(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(getString(R.string.detail_op_tips_company_empty));
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri
				.parse("http://www.google.co.jp/search?source=hp&biw=&bih=&q="
						+ URLEncoder.encode(data) + "&btnG=Google"));
		try {
			startActivity(intent);
		} catch (Exception e) {
			showToast(getString(R.string.detail_op_tips_company_fail));
			e.printStackTrace();
		}

	}

	private void goToTel(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(getString(R.string.detail_op_tips_tel_empty));
			return;
		}

		// 保存到通话记录中
		CallLogService callLogService = new CallLogService(this);
		callLogService.insertCallLog(tantcardId, data, new Date());
		// 调用系统拨打电话
		Uri uri = Uri.parse("tel:" + data);
		Intent intent = new Intent(Intent.ACTION_CALL, uri);
		try {
			startActivity(intent);
		} catch (Exception e) {
			showToast(getString(R.string.detail_op_tips_tel_fail));
			e.printStackTrace();
		}
	}

	private void goToEmail(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(getString(R.string.detail_op_tips_email_empty));
			return;
		}

		try {
			Uri uri = Uri.parse("mailto:" + data);
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			startActivity(intent);
		} catch (Exception e) {
			showToast(getString(R.string.detail_op_tips_email_fail));
		}
	}

	private void goToSkype(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(getString(R.string.detail_op_tips_skype_empty));
			return;
		}

		Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED");
		intent.setClassName("com.skype.raider", "com.skype.raider.Main");
		intent.setData(Uri.parse("tel:" + data));
		try {
			startActivity(intent);
		} catch (Exception e) {
			showToast(getString(R.string.detail_op_tips_skype_fail));
		}
	}

	private void goToAddress(String data) {
		if (StringUtil.isEmpty(data)) {
			showToast(getString(R.string.detail_op_tips_address_empty));
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri
				.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q="
						+ URLEncoder.encode(data)));
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		// & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		// intent.setClassName("com.google.android.apps.maps",
		// "com.google.android.maps.MapsActivity");

		try {
			startActivity(intent);
		} catch (Exception e) {
			showToast(getString(R.string.detail_op_tips_address_fail));
		}
	}

	private void bookmarkAdd() {
		boolean res = bookmarkService.insertBookmark(tantcardId);
		int tips = R.string.detail_op_tips_bookmark_add_success;
		if (!res) {
			tips = R.string.detail_op_tips_bookmark_add_fail;
		}
		alert(tips);
	}

	private void bookmarkDel() {
		bookmarkService.deleteBookmarkByTantcardId(tantcardId);
		alert(R.string.detail_op_tips_bookmark_del_success);
	}

	private void export() {
		final DataExporter exporter = new DataExporter(ContactDetail.this);
		if (!exporter.sysContactExists(tantcardId)) {
			// 没有在系统联系人中，直接导出
			new AlertDialog.Builder(this).setMessage(
					R.string.detail_op_tips_export).setPositiveButton(
					R.string.dlg_btn_confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
							// 导出
							doExport(exporter);
						}
					}).setNegativeButton(R.string.dlg_btn_cancel, null).show();
			// doExport(exporter);
		} else {
			// 弹出确认覆盖对话框
			new AlertDialog.Builder(this).setMessage(
					R.string.detail_op_tips_export_exists).setPositiveButton(
					R.string.dlg_btn_confirm,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
							// 导出
							doExport(exporter);
						}
					}).setNegativeButton(R.string.dlg_btn_cancel, null).show();
		}
	}

	private void doExport(DataExporter exporter) {
		boolean result = exporter.exportContact(tantcardId);
		if (result) {
			alert(R.string.detail_op_tips_export_success);
		} else {
			alert(R.string.detail_op_tips_export_fail);
		}
	}

	private void edit() {
		//检查屏幕方向，如果是横屏，弹出不能修改的对话框
		int mCurrentOrientation = getResources().getConfiguration().orientation;
		if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 弹出确认删除对话框
			new AlertDialog.Builder(this).setMessage(
					R.string.detail_op_tips_edit_confirm).setPositiveButton(
					R.string.dlg_btn_confirm,null).show();
			return;
		}
		
		isEdit = true;

		changeEditState();

		relocateNotEditToEdit();
		
		//设置为竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private void del() {
		// 弹出确认删除对话框
		new AlertDialog.Builder(this).setMessage(
				R.string.detail_op_tips_del_confirm).setPositiveButton(
				R.string.dlg_btn_confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						// 删除联系人
						contactService.deleteContact(tantcardId);
						// 删除书签
						bookmarkService.deleteBookmarkByTantcardId(tantcardId);
						// 删除通话记录
						CallLogService callLogService = new CallLogService(
								ContactDetail.this);
						callLogService.deleteCallLogByTantcardId(tantcardId);

						// 跳转回上一个activity
						ibBackOnClick();
					}
				}).setNegativeButton(R.string.dlg_btn_cancel, null).show();
	}

	private void editSave() {
		// 保存数据
		Contact update = new Contact();
		update.setTantcardId(tantcardId);

		contactService.updateContact(mapToContact(update));

		// 重新加载联系人信息初始化listView的值
		contact = contactService.getContactByTantcardId(tantcardId);
		initChildData();

		// 重新初始化下面的按钮菜单
		initOpMenuData();

		isEdit = false;
		changeEditState();

		relocateEditToNotEdit();
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	private void editGiveUp() {
		isEdit = false;
		changeEditState();

		relocateEditToNotEdit();
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	private void relocateEditToNotEdit() {
		// 重新定位
		if (first == 0) {
			elData.setSelection(first);
		} else if (first >= 1 && first <= 4) {
			elData.setSelection(1);
		} else if (first >= 5 && first <= 6) {
			elData.setSelection(2);
		} else if (first >= 7 && first <= 10) {
			elData.setSelection(first - 4);
		} else if (first >= 11 && first <= 14) {
			elData.setSelection(7);
		} else if (first >= 15 && first <= 20) {
			elData.setSelection(first - 7);
		} else if (first >= 21 && first <= 24) {
			elData.setSelection(14);
		} else if (first >= 25 && first <= 30) {
			elData.setSelection(first - 10);
		} else if (first >= 31 && first <= 34) {
			elData.setSelection(21);
		} else if (first >= 35 && first <= 45) {
			elData.setSelection(first - 13);
		}
	}

	private void relocateNotEditToEdit() {
		// 重新定位
		if (first == 0) {
			elData.setSelection(first);
		} else if (first == 1) {
			elData.setSelection(1);
		} else if (first == 2) {
			elData.setSelection(5);
		} else if (first >= 3 && first <= 6) {
			elData.setSelection(first + 4);
		} else if (first == 7) {
			elData.setSelection(11);
		} else if (first >= 8 && first <= 13) {
			elData.setSelection(first + 7);
		} else if (first == 14) {
			elData.setSelection(21);
		} else if (first >= 15 && first <= 20) {
			elData.setSelection(first + 10);
		} else if (first == 21) {
			elData.setSelection(31);
		} else if (first >= 22 && first <= 32) {
			elData.setSelection(first + 13);
		}
	}

	private void changeEditState() {
		if (isEdit) {
			btnMenu.setText(R.string.btn_title_edit_finish);
			update.clear();
		} else {
			btnMenu.setText(R.string.btn_title_menu);
		}

		contact = contactService.getContactByTantcardId(tantcardId);
		// 重新生成数据
		initChildData();

		adapter.notifyDataSetChanged();
	}

	private Contact mapToContact(Contact contact) {
		if (update.containsKey("firstname")) {
			if (!StringUtil.isEmpty(update.get("firstname"))) {
				contact.setFirstName(update.get("firstname"));
			}
		}
		if (update.containsKey("firstname_kana")) {
			if (!StringUtil.isEmpty(update.get("firstname_kana"))) {
				contact.setKanaFirstName(update.get("firstname_kana"));
			}
		}
		if (update.containsKey("lastname")) {
			if (!StringUtil.isEmpty(update.get("lastname"))) {
				contact.setLastName(update.get("lastname"));
			}
		}
		if (update.containsKey("lastname_kana")) {
			if (!StringUtil.isEmpty(update.get("lastname_kana"))) {
				contact.setKanaLastName(update.get("lastname_kana"));
			}
		}
		if (update.containsKey("company")) {
			if (!StringUtil.isEmpty(update.get("company"))) {
				contact.setCompany(update.get("company"));
			}
		}
		if (update.containsKey("company_kana")) {
			if (!StringUtil.isEmpty(update.get("company_kana"))) {
				contact.setCompanyKana(update.get("company_kana"));
			}
		}

		if (update.containsKey("affiliation")) {
			contact.setAffiliation(update.get("affiliation"));
		}

		if (update.containsKey("role")) {
			contact.setRole(update.get("role"));
		}

		if (update.containsKey("url")) {
			contact.setUrl(update.get("url"));
		}

		if (update.containsKey("zip1")) {
			contact.setZip1(update.get("zip1"));
		}
		if (update.containsKey("state1")) {
			contact.setState1(update.get("state1"));
		}
		if (update.containsKey("city1")) {
			contact.setCity1(update.get("city1"));
		}
		if (update.containsKey("address1")) {
			contact.setAddress1(update.get("address1"));
		}
		if (update.containsKey("tel1")) {
			contact.setTel1(update.get("tel1"));
		}
		if (update.containsKey("fax1")) {
			contact.setFax1(update.get("fax1"));
		}
		if (update.containsKey("mobile1")) {
			contact.setMobilephone1(update.get("mobile1"));
		}
		if (update.containsKey("email1")) {
			contact.setEmail1(update.get("email1"));
		}
		if (update.containsKey("skype1")) {
			contact.setSkype1(update.get("skype1"));
		}

		if (update.containsKey("zip2")) {
			contact.setZip2(update.get("zip2"));
		}
		if (update.containsKey("state2")) {
			contact.setState2(update.get("state2"));
		}
		if (update.containsKey("city2")) {
			contact.setCity2(update.get("city2"));
		}
		if (update.containsKey("address2")) {
			contact.setAddress2(update.get("address2"));
		}
		if (update.containsKey("tel2")) {
			contact.setTel2(update.get("tel2"));
		}
		if (update.containsKey("fax2")) {
			contact.setFax2(update.get("fax2"));
		}
		if (update.containsKey("mobile2")) {
			contact.setMobilephone2(update.get("mobile2"));
		}
		if (update.containsKey("email2")) {
			contact.setEmail2(update.get("email2"));
		}
		if (update.containsKey("skype2")) {
			contact.setSkype2(update.get("skype2"));
		}

		if (update.containsKey("zip3")) {
			contact.setZip3(update.get("zip3"));
		}
		if (update.containsKey("state3")) {
			contact.setState3(update.get("state3"));
		}
		if (update.containsKey("city3")) {
			contact.setCity3(update.get("city3"));
		}
		if (update.containsKey("address3")) {
			contact.setAddress3(update.get("address3"));
		}
		if (update.containsKey("tel3")) {
			contact.setTel3(update.get("tel3"));
		}
		if (update.containsKey("fax3")) {
			contact.setFax3(update.get("fax3"));
		}
		if (update.containsKey("mobile3")) {
			contact.setMobilephone3(update.get("mobile3"));
		}
		if (update.containsKey("email3")) {
			contact.setEmail3(update.get("email3"));
		}
		if (update.containsKey("skype3")) {
			contact.setSkype3(update.get("skype3"));
		}

		if (update.containsKey("classification1")) {
			contact.setClassification1(update.get("classification1"));
		}
		if (update.containsKey("classification2")) {
			contact.setClassification2(update.get("classification2"));
		}
		if (update.containsKey("meetingday")) {
			contact.setMeetingDay(update.get("meetingday"));
		}
		if (update.containsKey("registrationday")) {
			contact.setRegistrationDay(update.get("registrationday"));
		}
		if (update.containsKey("note")) {
			contact.setNote(update.get("note"));
		}

		return contact;
	}

	private void showDatePicker(EditText view, int groupPosition,
			int childPosition) {
		dataList.setVisibility(View.GONE);
		datePick.setVisibility(View.VISIBLE);

		TextView groupTitle = (TextView) datePick
				.findViewById(R.id.contact_detail_tv_title);
		DatePicker datePicker = (DatePicker) datePick
				.findViewById(R.id.contact_detail_datepicker);
		// datePick.findViewById(R.id.contact_detail_datepick_btn).setVisibility(View.VISIBLE);
		Button datePickerCancel = (Button) datePick
				.findViewById(R.id.contact_detail_datepick_cancel);
		datePickerCancel.setVisibility(View.VISIBLE);
		datePickerCancel.setText(R.string.date_picker_btn_cancel);
		Button datePickerConfirm = (Button) datePick
				.findViewById(R.id.contact_detail_datepick_confirm);
		datePickerConfirm.setVisibility(View.VISIBLE);
		datePickerConfirm.setText(R.string.date_picker_btn_confirm);

		String title = "";
		String key = childData.get(groupPosition).get(childPosition).get("key");
		if (key == "meetingday") {
			groupTitle.setText(R.string.detail_info_meetingday);
		} else {
			groupTitle.setText(R.string.detail_info_registrationday);
		}
		groupTitle.setTextColor(getResources().getColor(R.drawable.white));

		String format = "yyyy" + getString(R.string.year) + "MM"
				+ getString(R.string.month) + "dd" + getString(R.string.day);

		Date date = StringUtil.strToDate(view.getText().toString(), format);
		if (date == null) {
			date = new Date();
		}
		// 初始化，在没有onchange的时候用
		selectedDate = StringUtil.dateFormat(date, format);

		datePicker.init(1900 + date.getYear(), date.getMonth(), date.getDate(),
				new OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker v, int year,
							int monthOfYear, int dayOfMonth) {
						selectedDate = year + getString(R.string.year)
								+ (monthOfYear + 1) + getString(R.string.month)
								+ dayOfMonth + getString(R.string.day);
					}
				});

		datePickerCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 隐藏
				datePick.setVisibility(View.GONE);
				dataList.setVisibility(View.VISIBLE);
			}
		});

		final EditText editText = view;
		final int gPos = groupPosition;
		final int cPos = childPosition;
		datePickerConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editText.setInputType(InputType.TYPE_CLASS_TEXT);
				editText.setText(selectedDate);
				updateField(selectedDate, gPos, cPos);

				// 隐藏
				datePick.setVisibility(View.GONE);
				dataList.setVisibility(View.VISIBLE);
			}
		});
	}

	private void updateField(String val, int gPos, int cPos) {
		update.put(getKey(gPos, cPos), val);
		childData.get(gPos).get(cPos).put("childVal", val);
	}

	private String getKey(int gPos, int cPos) {
		return childData.get(gPos).get(cPos).get("key");
	}

	private void showUserImg() {
		if (scaleRate == 0) {
			int iw = image.getWidth();
			int ih = image.getHeight();
			System.out.println("ivNameCard.getWidth:" + ivNameCard.getWidth()
					+ ",ivNameCard.getHeight:" + ivNameCard.getHeight());
			System.out.println("iw:" + iw + ",ih:" + ih);
			float scaleX = ivNameCard.getWidth() / (float) iw;
			float scaleY = ivNameCard.getHeight() / (float) ih;

			// float scaleY = verticalScroll.getHeight()/(float)ih;
			// float scaleX = horizontalScroll.getWidth()/(float)iw;

			System.out.println("ScaleX:" + scaleX + ",scaleY:" + scaleY);
			if (scaleX < scaleY) {
				scaleRate = scaleX;
			} else {
				scaleRate = scaleY;
			}
			System.out.println("getScaleType:"
					+ ivNameCard.getScaleType().toString());
			// 设定ImageView大小
			Log.d("WARN", "getScaleType:"
					+ ivNameCard.getScaleType().toString());

		}
		System.out.println("getScaleType:"
				+ ivNameCard.getScaleType().toString() + "scaleRate:"
				+ scaleRate);
		// System.out.println("horizontalView.getWidth:" +
		// horizontalScroll.getWidth() + ",horizontalView.getHeight:" +
		// horizontalScroll.getHeight());
		// System.out.println("verticalView.getWidth:" +
		// verticalScroll.getWidth() + ",verticalView.getHeight:" +
		// verticalScroll.getHeight());
		if (scaleRate != 1) {
			scaleRate *= 1.5;
			if (scaleRate > 1) {
				scaleRate = 1;
			}
			Bitmap bmp = zoomBitmap(image);
			ivNameCard.setScaleType(ScaleType.CENTER);
			ivNameCard.setImageBitmap(bmp);
		} else {
			// 恢复默认尺寸
			ivNameCard.setScaleType(ScaleType.FIT_CENTER);
			ivNameCard.setImageBitmap(image);
			scaleRate = 0;
		}
	}

	private Bitmap zoomBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();

		matrix.postScale(scaleRate, scaleRate);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}

	class ViewHolder {
		TextView keyText;
		EditText valText;
		ImageButton opBtn;
		ImageView indicatorView;
	}

	private class ContactDetailAdapter extends BaseExpandableListAdapter {
		LayoutInflater inflater = null;

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childData.get(groupPosition).get(childPosition);
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
					inflater = (LayoutInflater) ContactDetail.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(R.layout.contact_detail_child,
						null);
				viewHolder = new ViewHolder();
				viewHolder.keyText = (TextView) convertView
						.findViewById(R.id.contact_detail_tv_key);
				viewHolder.valText = (EditText) convertView
						.findViewById(R.id.contact_detail_tv_val);
				viewHolder.opBtn = (ImageButton) convertView
						.findViewById(R.id.contact_detail_tv_op);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.keyText.setText(childData.get(groupPosition).get(
					childPosition).get("childKey"));
			if (isEdit) {
				String currentKey = childData.get(groupPosition).get(
						childPosition).get("key");
				if (!currentKey.equalsIgnoreCase("note")
						&& !currentKey.equalsIgnoreCase("registrationday")
						&& !currentKey.equalsIgnoreCase("meetingday")) {
					// viewHolder.valText.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
					viewHolder.valText.setInputType(InputType.TYPE_CLASS_TEXT);
					viewHolder.valText
							.setOnEditorActionListener(new OnEditorActionListener() {
								@Override
								public boolean onEditorAction(TextView v,
										int actionId, KeyEvent event) {
									System.out.println("ActionId:" + actionId);
									System.out.println("IME_ACTION_GO:"
											+ EditorInfo.IME_ACTION_GO);
									System.out.println("IME_ACTION_NEXT:"
											+ EditorInfo.IME_ACTION_NEXT);
									System.out.println("IME_ACTION_DONE:"
											+ EditorInfo.IME_ACTION_DONE);
									if (actionId == EditorInfo.IME_ACTION_GO
											|| actionId == EditorInfo.IME_ACTION_NEXT
											|| actionId == EditorInfo.IME_ACTION_DONE
											|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
										InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
										imm.hideSoftInputFromWindow(v
												.getWindowToken(), 0);
									}
									return true;
								}
							});
				}
				if (currentKey.equalsIgnoreCase("zip1")
						|| currentKey.equalsIgnoreCase("zip2")
						|| currentKey.equalsIgnoreCase("zip3")) {
					viewHolder.valText
							.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				if (currentKey.equalsIgnoreCase("tel1")
						|| currentKey.equalsIgnoreCase("tel2")
						|| currentKey.equalsIgnoreCase("tel3")
						|| currentKey.equalsIgnoreCase("fax1")
						|| currentKey.equalsIgnoreCase("fax2")
						|| currentKey.equalsIgnoreCase("fax3")
						|| currentKey.equalsIgnoreCase("mobile1")
						|| currentKey.equalsIgnoreCase("mobile2")
						|| currentKey.equalsIgnoreCase("mobile3")) {
					viewHolder.valText
							.setInputType(InputType.TYPE_CLASS_NUMBER);
				}

				if (currentKey.equalsIgnoreCase("note")) {
					viewHolder.valText
							.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE
									| InputType.TYPE_CLASS_TEXT);
				}
				// set the "done" key on input panel
				viewHolder.valText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			} else {
				viewHolder.valText
						.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE
								| InputType.TYPE_CLASS_TEXT);
			}

			viewHolder.valText.setText(childData.get(groupPosition).get(
					childPosition).get("childVal"));
			viewHolder.valText.setEnabled(isEdit);

			final int gPos = groupPosition;
			final int cPos = childPosition;
			viewHolder.valText.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// 非编辑状态，直接返回
					if (!isEdit) {
						return false;
					}

					// 禁止弹出输入法
					String key = childData.get(gPos).get(cPos).get("key");
					if (key.equalsIgnoreCase("meetingday")
							|| key.equalsIgnoreCase("registrationday")) {
						EditText edit = (EditText) v;
						edit.setInputType(InputType.TYPE_NULL);

						if (isEdit
								&& event.getAction() == MotionEvent.ACTION_UP) {
							showDatePicker(edit, gPos, cPos);
						}
					}

					return false;
				}
			});

			viewHolder.valText
					.setOnFocusChangeListener(new OnFocusChangeListener() {
						@Override
						public void onFocusChange(View view, boolean flag) {
							if (isEdit) {
								EditText edit = (EditText) view;
								if (!flag) {
									updateField(edit.getText().toString(),
											gPos, cPos);
								} else {
									// 选中了，滚动到对应的位置
									if (gPos == 0) {
										elData.setSelection(cPos + 1);
									} else if (gPos == 1) {
										elData.setSelection(cPos + 11);
									} else if (gPos == 2) {
										elData.setSelection(cPos + 21);
									} else if (gPos == 3) {
										elData.setSelection(cPos + 31);
									} else if (gPos == 4) {
										elData.setSelection(cPos + 41);
									}
								}
							}
						}
					});
			
			final String menu = childData.get(groupPosition).get(childPosition)
					.get("menu");
			if (!StringUtil.isEmpty(menu) && !isEdit) {
				viewHolder.opBtn.setVisibility(View.VISIBLE);
				viewHolder.opBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						showChildMenu(menu);
					}
				});
			} else {
				viewHolder.opBtn.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return parentData.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return parentData.size();
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
					inflater = (LayoutInflater) ContactDetail.this
							.getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(R.layout.contact_detail_parent,
						null);
				viewHolder = new ViewHolder();
				viewHolder.keyText = (TextView) convertView
						.findViewById(R.id.contact_detail_tv_title);
				viewHolder.indicatorView = (ImageView) convertView
						.findViewById(R.id.contact_detail_tv_indicator);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.keyText.setText(parentData.get(groupPosition).get(
					"parent"));

			if (isEdit) {
				// 编辑状态，文字设置为白色
				viewHolder.keyText.setTextColor(getResources().getColor(
						R.drawable.white));
			} else {
				// 非编辑状态，文字设置为黄色
				viewHolder.keyText.setTextColor(getResources().getColor(
						R.drawable.yellow));
			}

			if (isEdit) {
				viewHolder.indicatorView.setVisibility(View.GONE);
			} else {
				viewHolder.indicatorView.setVisibility(View.VISIBLE);
				if (isExpanded) {
					viewHolder.indicatorView
							.setImageResource(R.drawable.accordion_on_a);
				} else {
					viewHolder.indicatorView
							.setImageResource(R.drawable.accordion_off_a);
				}
			}
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

	private class SimpleGesture extends SimpleOnGestureListener {
		public boolean onDoubleTapEvent(MotionEvent e) {
			if (e.getAction() == MotionEvent.ACTION_UP) {
				// 重新显示图片
				showUserImg();
			}
			return true;
		}
	}

	private class MulitClickListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			return gestureDetector.onTouchEvent(event);
		}
	}

	/**
	 * @author yumin
	 * 
	 */
	@SuppressWarnings("unused")
	private class MulitPointTouchListener implements OnTouchListener {

		/**
		 * 
		 */
		private static final String TAG = "Touch";
		private Matrix matrix = new Matrix();
		private Matrix savedMatrix = new Matrix();
		private static final int NONE = 0;
		private static final int DRAG = 1;
		private static final int ZOOM = 2;
		private int mode = NONE;
		private PointF start = new PointF();
		private PointF mid = new PointF();
		private float oldDist = 1f;

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			ImageView view = (ImageView) v;
			view.setScaleType(ScaleType.CENTER);

			// 如果触发了双击事件，处理后返回
			if (gestureDetector.onTouchEvent(event)) {
				return false;
			}

			// 特别说明:ImageView若设的是matrix则不会居中,但不设matrix又不支持手势缩放
			// 故在layout中center,在进行手势前临时改为matrix,目的是同时兼容上述两种情况
			ivNameCard.setScaleType(ScaleType.MATRIX);

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				matrix.set(view.getImageMatrix());
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				mode = DRAG;

				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				
				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;

			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);
				} else if (mode == ZOOM) {

					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}

				break;
			}
			view.setImageMatrix(matrix);
			
			//TODO 拖动或者缩放，不触发后续事件onLongClick等
			
			return false;
		}

		private float spacing(MotionEvent event) {

			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void midPoint(PointF point, MotionEvent event) {

			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
	}
}
