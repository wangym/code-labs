package com.diaoyumi.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Constant;
import com.diaoyumi.android.etc.Diaoyumi;


public class Post extends AbstractActivity implements OnClickListener{
	private Button btnMultiFish;
	private Button btnBigFish;
	private Button btnLandScape;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        btnMultiFish = (Button) findViewById(R.id.btnMultiFish);
        btnBigFish = (Button) findViewById(R.id.btnBigFish);
        btnLandScape = (Button) findViewById(R.id.btnLandScape);
        
        btnMultiFish.setOnClickListener(this);
        btnBigFish.setOnClickListener(this);
        btnLandScape.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLandScape:
			Diaoyumi.initNew("type", Constant.POST_TYPE_LAND_SPACE);
			Diaoyumi.go(this, PostCamera.class);
			break;
		case R.id.btnBigFish:
			Diaoyumi.initNew("type", Constant.POST_TYPE_BIG_FISH);
			Diaoyumi.go(this, PostCamera.class);
			break;
		case R.id.btnMultiFish:
			Diaoyumi.initNew("type", Constant.POST_TYPE_MULTI_FISH);
			Diaoyumi.go(this, PostCamera.class);
			break;
		default:
			break;
		}
		
	}

	
}
