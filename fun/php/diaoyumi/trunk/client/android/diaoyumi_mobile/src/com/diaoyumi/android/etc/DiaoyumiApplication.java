package com.diaoyumi.android.etc;


import android.app.Application;

public class DiaoyumiApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		Diaoyumi.startup(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Diaoyumi.shutdown();
	}

	
	
}
