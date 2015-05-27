package com.diaoyumi.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.diaoyumi.android.activity.element.CornerListView;
import com.diaoyumi.android.database.DBAdapter;
import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Diaoyumi;

public class Setup extends AbstractActivity {
	private CornerListView cornerListView = null;	
	private List<Map<String,String>> listData = null;
	private SimpleAdapter adapter = null;
	private Button btnLogout = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Diaoyumi.confirm(Setup.this,"退出后还可用这个帐号登录，要退出吗？", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Diaoyumi.getDBAdapter().logout();
						Setup.this.startActivity(new Intent(Setup.this, Login.class));	
					}
					
				});
			}
		});
     
        cornerListView = (CornerListView)findViewById(R.id.listSetting);
        setListData();
 
        adapter = new SimpleAdapter(getApplicationContext(), listData, R.layout.setup_list_item,
new String[]{"text", "value"}, new int[]{R.id.setup_list_item_text, R.id.setup_list_item_value});
        cornerListView.setAdapter(adapter);      
        
    }
    
    /**
     * 设置列表数据
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void setListData(){
        listData = new ArrayList();
 
        Map<String,String> map = new HashMap<String, String>();
        map.put("text", "EMAIL");
        map.put("value", Diaoyumi.getDBAdapter().getConf(DBAdapter.CONF_USER_EMAIL));
        listData.add(map);
 
        map = new HashMap<String, String>();
        map.put("text", "昵称");
        map.put("value", Diaoyumi.getDBAdapter().getConf(DBAdapter.CONF_USER_NICK));
        listData.add(map);
 
        map = new HashMap<String, String>();
        map.put("text", "密码");
        map.put("value", "*******");
        listData.add(map);
    }

	
}
