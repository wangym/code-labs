package com.diaoyumi.android.activity;

import com.diaoyumi.android.etc.AbstractActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Test extends AbstractActivity implements
		OnCheckedChangeListener {
	private RadioGroup rgTabGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		rgTabGroup = (RadioGroup) findViewById(R.id.rgTabGroup);
		for(int i = 0; i < rgTabGroup.getChildCount(); i++){
			View item = rgTabGroup.getChildAt(i);
			if (item instanceof RadioButton){
				((RadioButton) item).setOnCheckedChangeListener(this);
			}
		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.rbtnPost:
				Log.d("---","tab:Post");
				break;
			case R.id.rbtnMy:
				Log.d("---", "tab:My");
				break;
			case R.id.rbtnSearch:
				Log.d("---","tab:Search");
				break;
			default:
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rbtnPost:
			Log.d("---","tab:Post");
			
			break;
		case R.id.rbtnMy:
			Log.d("---", "tab:My");
			break;
		case R.id.rbtnSearch:
			Log.d("---","tab:Search");
			break;
		default:
			break;
	}
		
	}

}