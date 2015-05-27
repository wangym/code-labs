/**
 * 
 */
package com.diaoyumi.android.activity;

import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Diaoyumi;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

/**
 * @author yumin
 * 
 */
public class Loading extends AbstractActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);	
		Diaoyumi.setScreenSize(dm.widthPixels, dm.heightPixels, dm.densityDpi);
		
		if (Diaoyumi.getDBAdapter().isLogin()){
			Diaoyumi.go(this, Main.class);	
		}else{
			startActivity(new Intent(this, Login.class));	
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
