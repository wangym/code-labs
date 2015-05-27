/**
 * 
 */
package com.shimoda.oa.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.shimoda.oa.R;
import com.shimoda.oa.util.BaseActivity;

/**
 * @author yumin
 * 
 */
public class MainMenu extends BaseActivity {

	/**
	 * 
	 */
	private Button btnContact;
	private Button btnBookmark;
	private Button btnCallLog;
	private Button btnImport;
	private Button btnExport;
	private Button btnHelp;
	private Button btnAbout;

	// ====================
	// override methods
	// ====================

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		initWidget();
		initListener();
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

	// ====================
	// private methods
	// ====================

	/**
	 * 
	 */
	private void initWidget() {

		TextView title = (TextView) findViewById(R.id.top_v1_tv_center);
		title.setText(R.string.title_menu);
		btnContact = (Button) findViewById(R.id.main_menu_btn_contact);
		btnBookmark = (Button) findViewById(R.id.main_menu_btn_bookmark);
		btnCallLog = (Button) findViewById(R.id.main_menu_btn_calllog);
		btnImport = (Button) findViewById(R.id.main_menu_btn_import);
		btnExport = (Button) findViewById(R.id.main_menu_btn_export);
		btnHelp = (Button) findViewById(R.id.main_menu_btn_help);
		btnAbout = (Button) findViewById(R.id.main_menu_btn_about);
	}

	/**
	 * 
	 */
	private void initListener() {

		// 联系人按钮监听
		btnContact.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(ContactList.class, false);
			}
		});
		btnContact.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
					//btnContact.getBackground().setAlpha(200);
					btnContact.getBackground().setColorFilter(0xFFF5F5F5,PorterDuff.Mode.SRC_IN);
				}else{
					//btnContact.getBackground().setAlpha(255);
					btnContact.getBackground().clearColorFilter();
				}
				btnContact.invalidate();
				return false;
			}
		});
		// 书签按钮监听
		btnBookmark.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(BookmarkList.class, false);
			}
		});
		btnBookmark.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
					//btnBookmark.getBackground().setAlpha(200);
					btnBookmark.getBackground().setColorFilter(0xFFF5F5F5,PorterDuff.Mode.SRC_IN);
				}else{
					//btnBookmark.getBackground().setAlpha(255);
					btnBookmark.getBackground().clearColorFilter();
				}
				btnBookmark.invalidate();
				return false;
			}
		});
		// 通话记录按钮监听
		btnCallLog.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(CallLogList.class, false);
			}
		});
		btnCallLog.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
					//btnCallLog.getBackground().setAlpha(200);
					btnCallLog.getBackground().setColorFilter(0xFFF5F5F5,PorterDuff.Mode.SRC_IN);
				}else{
					//btnCallLog.getBackground().setAlpha(255);
					btnCallLog.getBackground().clearColorFilter();
				}
				btnCallLog.invalidate();
				return false;
			}
		});
		// 导入按钮监听
		btnImport.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(DataImportStep1.class, false);
			}
		});
		btnImport.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
					//btnImport.getBackground().setAlpha(200);
					btnImport.getBackground().setColorFilter(0xFFF5F5F5,PorterDuff.Mode.SRC_IN);
				}else{
					//btnImport.getBackground().setAlpha(255);
					btnImport.getBackground().clearColorFilter();
				}
				btnImport.invalidate();
				return false;
			}
		});
		// 导出按钮监听
		btnExport.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(DataExportStep1.class, false);
			}
		});
		btnExport.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
					//btnExport.getBackground().setAlpha(200);
					btnExport.getBackground().setColorFilter(0xFFF5F5F5,PorterDuff.Mode.SRC_IN);
				}else{
					//btnExport.getBackground().setAlpha(255);
					btnExport.getBackground().clearColorFilter();
				}
				btnExport.invalidate();
				return false;
			}
		});
		// 帮助按钮监听
		btnHelp.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(Help.class, false);
			}
		});
		btnHelp.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
					//btnHelp.getBackground().setAlpha(200);
					btnHelp.getBackground().setColorFilter(0xFFF5F5F5,PorterDuff.Mode.SRC_IN);
				}else{
					btnHelp.getBackground().setAlpha(255);
					btnHelp.getBackground().clearColorFilter();
				}
				btnHelp.invalidate();
				return false;
			}
		});
		// 关于按钮监听
		btnAbout.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToAnyActivity(About.class, false);
			}
		});
		btnAbout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
					//btnAbout.getBackground().setAlpha(200);
					btnAbout.getBackground().setColorFilter(0xFFF5F5F5,PorterDuff.Mode.SRC_IN);
				}else{
					//btnAbout.getBackground().setAlpha(255);
					btnAbout.getBackground().clearColorFilter();
				}
				btnAbout.invalidate();
				return false;
			}
		});
	}
}
