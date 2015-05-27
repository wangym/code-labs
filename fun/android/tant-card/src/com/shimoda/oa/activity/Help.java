package com.shimoda.oa.activity;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shimoda.oa.R;
import com.shimoda.oa.util.BaseActivity;

@SuppressWarnings("unused")
public class Help extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//自定义标题
		callTopBar(R.layout.help,R.layout.top_v2);
		TextView titleNameView = (TextView) this.findViewById(R.id.top_v1_tv_center);
		titleNameView.setText(getString(R.string.title_help));
		
		//返回按钮
		Button btnBack = (Button) this.findViewById(R.id.top_v1_ib_left);
		btnBack.setBackgroundResource(R.drawable.top_back);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setText(R.string.btn_title_back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				goToMenuActivity(true);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		WebView mWebView = (WebView) findViewById(R.id.helpwebview);       
        mWebView.loadUrl("http://www.shimoda-oa.co.jp/tantcard/android/help.html");    
	}
}