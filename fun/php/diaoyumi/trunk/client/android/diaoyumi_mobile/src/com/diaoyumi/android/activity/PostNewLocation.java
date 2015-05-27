package com.diaoyumi.android.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.diaoyumi.android.activity.element.SelectLocationOverlay;
import com.diaoyumi.android.etc.Diaoyumi;
import com.diaoyumi.android.etc.Util;

/**
 * 方便用户通过移动大头针来确定经纬度。另退出时要确保用户输入该经纬度的地点名称
 * @author robin
 *
 */
public class PostNewLocation extends MapActivity implements OnClickListener{

	public static final String INTENT_PARAM_INIT_LAT1E6 = "initLat1E6";
	public static final String INTENT_PARAM_INIT_LNG1E6 = "initLng1E6";
	public static final String INTENT_PARAM_INIT_ZOOM = "initZoom";
	public static final String INTENT_PARAM_LOCATION_NAME = "locationName";
	private static final int DEFAULT_INIT_LNG = (int) (108.5323 * 1E6);
	private static final int DEFAULT_INIT_LAT = (int) (33.37 * 1E6);
	private static final int DEFAULT_INIT_ZOOM = 5;
	
	private MapView mapView;
	private Button btnCancel;
	private Button btnOk;
	//初始化时坐标
	private int initLat1E6 = DEFAULT_INIT_LAT;
	private int initLng1E6 = DEFAULT_INIT_LNG;
	private int initZoom = DEFAULT_INIT_ZOOM;
	private String locationName;
	private EditText edLocationName;
	private boolean isConfirmName = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.post_new_loaction);
		edLocationName = new EditText(this);
		
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		mapView = (MapView) findViewById(R.id.bmapView);
		
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		
		Diaoyumi.getBaiduMapManager().start();
        super.initMapActivity(Diaoyumi.getBaiduMapManager());
		mapView.setTraffic(true);
	    mapView.setBuiltInZoomControls(true);
        //设置在缩放动画过程中也显示overlay,默认为不绘制
	    mapView.setDrawOverlayWhenZooming(true);
		// 添加定位图层
	    
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOk:
			executeOk();
			break;
		case R.id.btnCancel:
			Diaoyumi.go(this, PostLocation.class);
			break;
		default:
			break;
		}
		
	}
	

	@Override
	protected void onStart() {
		super.onStart();
		initLat1E6 = (Diaoyumi.getNew("here_lat1E6") == null) ? DEFAULT_INIT_LAT : (Integer) Diaoyumi.getNew("here_lat1E6");
		initLng1E6 = (Diaoyumi.getNew("here_lng1E6") == null) ? DEFAULT_INIT_LNG : (Integer) Diaoyumi.getNew("here_lng1E6");
		//如果取到了当前位置则，zoomlevle降低
		initZoom = (initLat1E6 != DEFAULT_INIT_LAT) ? DEFAULT_INIT_ZOOM + 9: DEFAULT_INIT_ZOOM;
		isConfirmName = false;
		
	}	
	
	@Override
	protected void onPause() {
		Diaoyumi.getBaiduMapManager().stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Diaoyumi.getBaiduMapManager().start();
		mapView.getController().setZoom(initZoom);
		mapView.getController().animateTo(new GeoPoint(initLat1E6, initLng1E6));
		mapView.getOverlays().clear();
		mapView.getOverlays().add(new SelectLocationOverlay(initLat1E6, initLng1E6));
		super.onResume();
	}
	
	
	private void executeOk(){
		//地名不为空且确认过则执行下一步
		if (Util.isNotEmpty(locationName) && isConfirmName){
			SelectLocationOverlay slo = null;
			for(int i = 0 ; i < mapView.getOverlays().size(); i++){
				if (mapView.getOverlays().get(i) instanceof SelectLocationOverlay){
					slo = (SelectLocationOverlay) mapView.getOverlays().get(i);
					break;
				}
			}
			
			if (slo != null){
				Diaoyumi.putNew("location_lat1E6", slo.getLat1E6());
				Diaoyumi.putNew("location_lng1E6", slo.getLng1E6());
				Diaoyumi.putNew("location_name", locationName);
				Diaoyumi.putNew("location_isnew", true);
				Diaoyumi.go(this, PostInfo.class);
			}else{
				//@TODO 出错 暂时返回main
				Diaoyumi.go(this, Main.class);
			}
			return ;
		}
		ConfirmName();
	}
	
	private void ConfirmName(){
		AlertDialog.Builder builder = new Builder(this);
		String title = (Util.isNotEmpty(locationName)) ? "请确认你标注的地名" : "请输入你标注的地名(必填)";
		edLocationName = new EditText(this);
		edLocationName.setText(locationName);
		builder.setTitle(title);
		builder.setView(edLocationName);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setNegativeButton("取消",null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PostNewLocation.this.locationName = edLocationName.getText().toString();
				PostNewLocation.this.executeOk();
			}
		});
		builder.show();
		isConfirmName = true;
	}



	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


}
