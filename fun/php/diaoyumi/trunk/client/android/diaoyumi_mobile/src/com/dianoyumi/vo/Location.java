package com.dianoyumi.vo;

public class Location {
	private String Name;
	private double lat;
	private double lng;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public double getLat() {
		return lat;
	}
	
	public int getLat1E6(){
		return (int) (lat * 1E6);
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	
	public int getLng1E6(){
		return (int) (lng * 1E6);
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	

}
