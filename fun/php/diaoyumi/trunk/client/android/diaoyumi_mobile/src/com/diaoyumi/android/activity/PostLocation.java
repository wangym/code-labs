package com.diaoyumi.android.activity;

import java.util.HashMap;
import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.diaoyumi.android.activity.element.CornerListView;
import com.diaoyumi.android.etc.AbstractActivity;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Options;

public class PostLocation extends AbstractActivity implements
		OnClickListener,OnItemClickListener {
	private GeoPoint gpHere;
	private LocationListener locationListener = null;//create时注册此listener，Destroy时需要Remove
	private CornerListView lvLocations;
	private Options locationList = new Options();
	private Button btnCancel;
	private Button btnNext;
	private Button btnNewLocation;
	private com.dianoyumi.vo.Location selected = null;
	private int selectedIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_location);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnNewLocation = (Button) findViewById(R.id.btnNewLocation);
		btnCancel.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnNewLocation.setOnClickListener(this);
		lvLocations = (CornerListView) findViewById(R.id.lvLocations);
		lvLocations.setOnItemClickListener(this);
		lvLocations.setFocusable(true);
		lvLocations.setSelected(true);

		
		Diaoyumi.getBaiduMapManager().start();
		
	    
        // 注册定位事件
        locationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
					int lat = (int) (location.getLatitude() * 1E6);
					int lng = (int) (location.getLongitude() * 1E6);
					PostLocation.this.gpHere = new GeoPoint(lat, lng);
					initLocationList();
					hideProgressBar();
					Diaoyumi.getBaiduMapManager().getLocationManager().removeUpdates(locationListener);
				}
			}
        };
        
        
        
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
		GeoPoint gp = (GeoPoint) item.get("value");
		selectedIndex = position;
		selected.setLat(gp.getLatitudeE6() / 1E6);
		selected.setLng(gp.getLatitudeE6() / 1E6);
		this.onClick(btnNext);
	}
	
	
	private void initLocationList(){
		selected = null;
		selectedIndex = 0;
		locationList.clear();
		
		List<com.dianoyumi.vo.Location> history = Diaoyumi.getDBAdapter().getMyAllPlace();
		if (history.size() == 0){
			this.onClick(btnNewLocation);
			return;
		}
		for(int i = 0; i < history.size(); i++){
			com.dianoyumi.vo.Location loc = history.get(i);
			//@TODO计算默认选什么
			int imgRes = R.drawable.true_invisiable;
			if (i == 0){
				selected = loc;
				selectedIndex = i;
				imgRes = R.drawable.true_visiable;
			}
			locationList.append("name", loc.getName(), "value", new GeoPoint(loc.getLat1E6(), loc.getLng1E6()), "img", imgRes);
		}

		SimpleAdapter adapter = new SimpleAdapter(this,
        		locationList.getList(),// 数据来源
                R.layout.post_location_item,  
                new String[] { "name", "img"},
                // 分别对应view 的id
                new int[] { R.id.post_location_item_name, R.id.post_location_item_img});
		
		
		lvLocations.setAdapter(adapter);
		//-1 使默认值在ListView顶部
		lvLocations.setSelection(selectedIndex - 1);
		lvLocations.requestFocus();
		
	}
	

	private void showProgressBar(){
		LinearLayout plProgressBar = (LinearLayout) findViewById(R.id.plProgressBar);
		plProgressBar.setVisibility(View.VISIBLE);
		
	}
	
	private void hideProgressBar(){
		LinearLayout plProgressBar = (LinearLayout) findViewById(R.id.plProgressBar);
		plProgressBar.setVisibility(View.GONE);
		
	}
	
	@Override
	protected void onPause() {
		// 移除listener
		Diaoyumi.getBaiduMapManager().getLocationManager().removeUpdates(locationListener);
		Diaoyumi.getBaiduMapManager().stop();
		super.onPause();
	}
	@Override
	protected void onResume() {
		//防止从PostNewLocation进入，重复取当前位置
		if (Diaoyumi.getNew("here_lat1E6") == null){
			gpHere = null;
			showProgressBar();
			// 注册Listener
			Diaoyumi.getBaiduMapManager().getLocationManager().requestLocationUpdates(locationListener);
		}
 		Diaoyumi.getBaiduMapManager().start();
		super.onResume();
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			Diaoyumi.go(this, Main.class);
			break;
		case R.id.btnNext:
			if (gpHere != null){
				Diaoyumi.putNew("here_lat1E6", gpHere.getLatitudeE6());
				Diaoyumi.putNew("here_lng1E6", gpHere.getLongitudeE6());
			}
			Diaoyumi.putNew("location_lat1E6", selected.getLat1E6());
			Diaoyumi.putNew("location_lng1E6", selected.getLng1E6());
			Diaoyumi.putNew("location_name", selected.getName());
			Diaoyumi.putNew("location_isnew", false);
			
			
			Diaoyumi.go(this, PostInfo.class);
			break;
		case R.id.btnNewLocation:
			if (gpHere != null){
				Diaoyumi.putNew("here_lat1E6", gpHere.getLatitudeE6());
				Diaoyumi.putNew("here_lng1E6", gpHere.getLongitudeE6());
			}
			Diaoyumi.go(this, PostNewLocation.class);
			break;
		default:
			break;
		}

	}




}
