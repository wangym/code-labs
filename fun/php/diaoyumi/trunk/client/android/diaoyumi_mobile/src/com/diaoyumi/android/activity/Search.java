package com.diaoyumi.android.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.diaoyumi.android.database.DBAdapter;
import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Listener.OnSelectPhoto;

public class Search extends AbstractActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
    //    new AlertDialog.Builder(this).setTitle("列表框").setItems(new String[] { "Item1", "Item2" }, null).setNegativeButton("确定", null).show();
        
        
        Button btn1 = (Button) findViewById(R.id.button1);
      btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Search.this.getCameraPhoto(new OnSelectPhoto() {
					
					@Override
					public void onSelect(String filePath) {
						Log.d("-----", filePath);
						
					}
				});
			}
		});
      
      Button btn2 = (Button) findViewById(R.id.button2);
      btn2.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.d("_0--", Boolean.toString(Diaoyumi.getDBAdapter().isLogin()));
			Log.d("_1__",Boolean.toString(Diaoyumi.getDBAdapter().register("changgb@hotmail.com", "changgb", "710621")));
			Log.d("_2.1_", Boolean.toString(Diaoyumi.getDBAdapter().login("changgb@hotmail.com", "710620")));
			Log.d("_2.2_", Boolean.toString(Diaoyumi.getDBAdapter().login("changgb@hotmail.com", "710621")));
			Log.d("_3_", Diaoyumi.getDBAdapter().getConf(DBAdapter.CONF_USER_NICK));
			Log.d("_4_", Diaoyumi.getDBAdapter().getConf(DBAdapter.CONF_USER_EMAIL));
			Log.d("_5_", Diaoyumi.getDBAdapter().getConf(DBAdapter.CONF_USER_PASSWORD));
			//Log.d("_6_", Diaoyumi.getDBAdapter().getConf(DBAdapter.CONF_USER_PHOTO));
			Log.d("_7_", Boolean.toString(Diaoyumi.getDBAdapter().changeUserPhoto("/test/1.jpg")));
			Log.d("_8_", Diaoyumi.getDBAdapter().getConf(DBAdapter.CONF_USER_PHOTO));
			
		}
      });
    }

	
}
