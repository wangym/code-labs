package com.diaoyumi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dianoyumi.vo.Event;
import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Constant;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Util;

public class PostInfo extends AbstractActivity implements OnClickListener {
	
	private TextView tvTitle;
	private EditText edWeight;
	private EditText edRemark;
	private Button btnCancel;
	private Button btnSave;
	private int type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_info);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		edWeight = (EditText) findViewById(R.id.edWeight);
		edRemark = (EditText) findViewById(R.id.edRemark);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnSave = (Button) findViewById(R.id.btnSave);
		
		btnCancel.setOnClickListener(this);
		btnSave.setOnClickListener(this);
	}
	
	
	private boolean executeSave(){
		Event event = new Event();
		int type = (Diaoyumi.getNew("type") == null) ? Constant.POST_TYPE_LAND_SPACE : (Integer) Diaoyumi.getNew("type");
		int lat1E6 = (Diaoyumi.getNew("location_lat1E6") == null) ? 0 : ((Integer) Diaoyumi.getNew("location_lat1E6"));
		int lng1E6 = (Diaoyumi.getNew("location_lng1E6") == null) ? 0 : ((Integer) Diaoyumi.getNew("location_lng1E6"));
		String place = (String) Diaoyumi.getNew("location_name");
		String pictruePath = Constant.NEW_JPEG_FILE;
		boolean isNewPlace = false;
		if (Diaoyumi.getNew("location_isnew") != null && (Boolean) Diaoyumi.getNew("location_isnew") == true){
			isNewPlace = true;
		}
		//生成文件id
		String newPictureFileName = "p_" + Util.uuid() + ".jpg";
		if (! Util.copyFile(pictruePath, Constant.PATH_IMAGE + "/" +newPictureFileName, true)){
			Diaoyumi.info(this, "复制图片文件失败！sorry.");
			return false;
		}
		event.setLat(lat1E6 / 1E6);
		event.setLng(lng1E6 / 1E6);
		event.setPlace(place);
		event.setType(Integer.toString(type));
		event.setDesc(edRemark.getText().toString());
		event.setPicture(newPictureFileName);
		event.setNewPlace(isNewPlace);
		if (edWeight.getVisibility() != View.GONE){
			event.putProperty(Event.PRO_KEY_WEIGHT, edWeight.getText().toString());
		}
		boolean ret = Diaoyumi.getDBAdapter().insertEvent(event);
		if (! ret) Diaoyumi.info(this, "存储失败，可以再尝试下或者选择取消！sorry.");
		return ret;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			Diaoyumi.go(this, Main.class);
			break;
		case R.id.btnSave:
			if(executeSave()){
				Main.setInitTab(Main.TAB_MY);
				Diaoyumi.go(this, Main.class);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		type = (Diaoyumi.getNew("type") == null) ? Constant.POST_TYPE_LAND_SPACE : (Integer) Diaoyumi.getNew("type");
		switch(type){
			case Constant.POST_TYPE_LAND_SPACE:
				edWeight.setVisibility(View.GONE);
				break;
			default:
				edWeight.setVisibility(View.VISIBLE);
				edWeight.getFocusables(0);
		}
		super.onResume();
	}
	

}
