package com.diaoyumi.android.activity.element;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.diaoyumi.android.activity.R;
import com.diaoyumi.android.etc.Diaoyumi;

public class SelectLocationOverlay extends Overlay {
	private int lat1E6;
	private int lng1E6;
	
	public SelectLocationOverlay(int lat1E6, int lng1E6){
		this.lat1E6 = lat1E6;
		this.lng1E6 = lng1E6;
	}
	
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Bitmap bmp = BitmapFactory.decodeResource(Diaoyumi.getResources(), R.drawable.red_dot);
		Point point = mapView.getProjection().toPixels(new GeoPoint(lat1E6, lng1E6), null);
		canvas.drawBitmap(bmp, point.x - Math.round(bmp.getWidth() / 2), point.y - bmp.getHeight(), null);
	}	
	
	public int getLat1E6() {
		return lat1E6;
	}



	public int getLng1E6() {
		return lng1E6;
	}



	@Override
	public boolean onTap(GeoPoint newPoint, MapView mapView) {
		mapView.getOverlays().remove(this);
		SelectLocationOverlay mo = new SelectLocationOverlay(newPoint.getLatitudeE6(), newPoint.getLongitudeE6());
		mapView.getOverlays().add(mo);
		return true;
	}
	

}
