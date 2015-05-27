package com.diaoyumi.android.activity;

import com.diaoyumi.android.etc.Diaoyumi;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Main extends ActivityGroup implements
		OnClickListener {
	private LinearLayout mainTab;
	private LinearLayout mainTabContainer = null;
	private LocalActivityManager localActivityManager = null;
	
	public final static int TAB_POST = R.id.btnPost;
	public final static int TAB_MY = R.id.btnMy;
	public final static int TAB_SEARCH = R.id.btnSearch;
	public final static int TAB_SETUP = R.id.btnSetup;
	public static int initTab = 0; 
	
	public final static void setInitTab(int tab){
		initTab = tab;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mainTab = (LinearLayout) findViewById(R.id.mainTab);
		mainTabContainer = (LinearLayout) findViewById(R.id.mainTabContainer);
		for(int i = 0; i < mainTab.getChildCount(); i++){
			View item = mainTab.getChildAt(i);
			if (item instanceof Button){
				((Button) item).setOnClickListener(this);
			}
		}
		
		localActivityManager = this.getLocalActivityManager();
		goTab(TAB_MY);
	}
	
 
	@Override
	protected void onResume() {
		if (initTab != 0) goTab(initTab);
		super.onResume();
	}	
	
	private void goTab(int tabId){
		for(int i = 0; i < mainTab.getChildCount(); i++){
			View item = mainTab.getChildAt(i);
			if (item instanceof Button && item.getId() == tabId){
				onClick((Button) item);
				break;
			}
		}
	}
	
	private void refreshTabContent(String id, Class<?> activityClass){
		Intent intent = new Intent(this, activityClass);
        mainTabContainer.removeAllViews();
        mainTabContainer.addView(localActivityManager.startActivity(id, intent).getDecorView());
  }



	@Override
	public void onClick(View v) {
		Button btn = (Button) v;
		for(int i = 0; i < mainTab.getChildCount(); i++){
			View item = mainTab.getChildAt(i);
			if (item instanceof Button){
				((Button) item).setEnabled(true);
			}
		}
		btn.setEnabled(false);
		switch (btn.getId()) {
		case TAB_POST:
			refreshTabContent("Post" ,Post.class);
			break;
		case TAB_MY:
			refreshTabContent("MyActivity" ,My.class);
			break;
		case TAB_SEARCH:
			refreshTabContent("SearchActivity" ,Search.class);
			break;
		case TAB_SETUP:
			refreshTabContent("SetupActivity", Setup.class);
			break;
		default:
			break;
		}
		
	}

}