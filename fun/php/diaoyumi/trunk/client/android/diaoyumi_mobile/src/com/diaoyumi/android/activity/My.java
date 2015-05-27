package com.diaoyumi.android.activity;

import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.dianoyumi.vo.Event;
import com.diaoyumi.android.activity.element.ExtendImageView;
import com.diaoyumi.android.database.DBAdapter;
import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Constant;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Options;
import com.diaoyumi.android.etc.ThumbnailUtils;
import com.diaoyumi.android.etc.Util;

public class My extends AbstractActivity implements OnItemClickListener{
	private ListView lvList;
	private Options eventList = new Options();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my);
        lvList = (ListView) findViewById(R.id.lvList);
        lvList.setOnItemClickListener(this);
        lvList.setSelected(false);
        
        initEventList();
    }
    
    private void initEventList(){
    	eventList.clear();
    	List<Event> all = Diaoyumi.getDBAdapter().getMyAllEvent(DBAdapter.MAX_PAGE_SIZE,1);
    	for(int i = 0; i < all.size(); i++){
    		Event event = all.get(i);
    		String strDate = Util.date2string(event.getEventTime(), Util.SIMPLE_DATE_PATTERN);
    		String imgPath = Constant.PATH_IMAGE + "/" + event.getPicture();
    		eventList.append("place", event.getPlace(), "rid", event.getRid(), "date",strDate, "img", imgPath);
    	}
    	
		SimpleAdapter adapter = new SimpleAdapter(this,
        		eventList.getList(),// 数据来源
                R.layout.my_list_item,  
                new String[] { "place", "img", "date"},
                // 分别对应view 的id
                new int[] { R.id.my_list_item_place, R.id.my_list_item_img, R.id.my_list_item_date});
		
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view.getId() == R.id.my_list_item_img){
					ExtendImageView img = (ExtendImageView) view;
					String picPath = (String) data;
					if (Util.isNotEmpty(picPath)){
						Bitmap bmp = ThumbnailUtils.createImageThumbnail(picPath,Images.Thumbnails.MINI_KIND);
						img.setImageBitmap(bmp);
					
						return true;
					}
				}
				return false;
			}
		});
		
		lvList.setAdapter(adapter);
		lvList.setSelection(0);
   	
    }
    
	@Override
	protected void onResume() {
		 initEventList();
		super.onResume();
	}
   
    

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
		String rid = (String) item.get("rid");
		Diaoyumi.info(this, "click " + rid);
		
		
	}
    
	
}
