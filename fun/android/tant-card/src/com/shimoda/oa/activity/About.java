package com.shimoda.oa.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shimoda.oa.R;
import com.shimoda.oa.util.BaseActivity;
import com.shimoda.oa.util.Constants;

public class About extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 自定义标题
		callTopBar(R.layout.about,R.layout.top_v2);
		
		TextView titleNameView = (TextView) this.findViewById(R.id.top_v1_tv_center);
		titleNameView.setText(getString(R.string.title_about));
		
		ImageView image = (ImageView) this.findViewById(R.id.about_img);
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        image.setAdjustViewBounds(true);
        image.setMaxWidth(dm.widthPixels-50);
        image.setMaxHeight(dm.heightPixels/2-30);
		
		//版本
		TextView versionView = (TextView) this.findViewById(R.id.about_app_version);
		versionView.setText(getString(R.string.version)+" "+Constants.APP_VERSION);
		
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
		
		//公司主页按钮
		Button btnHomepage = (Button) this.findViewById(R.id.about_btn_homepage);
		btnHomepage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 弹出确认覆盖对话框
				new AlertDialog.Builder(About.this).setMessage(getString(R.string.visit)).setPositiveButton(
						R.string.about_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialoginterface,
									int i) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(Constants.HOMEPAGE));
								startActivity(intent);
							}
						}).setNegativeButton(R.string.about_no, null).show();
			}
		});
	}
}