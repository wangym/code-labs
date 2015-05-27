/**
 * 查找好友
 */
package me.shuotao.activity;

import me.shuotao.etc.Constant;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * @author yumin
 * 
 */
public class PhoneContactsActivity extends BaseActivity {

	/**
	 * 
	 */
	private Bundle bundle;

	/**
	 * 
	 */
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		this.callCustomTitleBar(R.layout.phone_contacts, R.layout.titlebar);
		super.onCreate(savedInstanceState);

		// 控件初始设置
		init();
		// 设置监听事件
		setOnClickListener();
	}

	/**
	 * 
	 */
	private void init() {

		// 获取按钮控件
		initTitlebarControl();

		// 设置控件内容
		leftBtn.setVisibility(Button.INVISIBLE);
		centerTV.setText(Constant.TITLE_FIND_FRIEND);
		rightBtn.setText(Constant.BTN_CONTINUE);

		// 继续按钮监听
		rightBtn.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View view) {

				// 调用继续逻辑
				rightBtnOnClick();
			}
		});

		// 接收传递参数
		bundle = getIntent().getExtras();
		type = bundle.getString(Constant.KEY_TYPE);

		// 取联系人数据
		Cursor cursor = getContentResolver().query(People.CONTENT_URI, null, null, null, null);
		// startManagingCursor(cursor);
		ListAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.phone_contacts, cursor,
				new String[] { Contacts.People.NAME, Contacts.People.NUMBER },
				new int[] { R.id.tvPhoneContactsName, R.id.tvPhoneContactsMobile });
		ListView listView = new ListView(this);
		listView.setAdapter(adapter);
		setContentView(listView);
	}

	/**
	 * 
	 */
	private void setOnClickListener() {

	}

	/**
	 * 继续按钮逻辑
	 */
	private void rightBtnOnClick() {

		if (Constant.KEY_REGISTER.equalsIgnoreCase(type)) {

		}
	}

}
