package com.shimoda.oa.model;

public class MenuItem {
	public static final int TYPE_WEBSITE = 1;
	
	public static final int TYPE_COMPANY = 2;
	
	public static final int TYPE_TEL = 3;
	
	public static final int TYPE_EMAIL = 4;
	
	public static final int TYPE_ADDRESS = 5;
	
	public static final int TYPE_BOOKMARK_ADD = 6;
	
	public static final int TYPE_BOOKMARK_DEL = 7;
	
	public static final int TYPE_EXPORT = 8;
	
	public static final int TYPE_EDIT = 9;
	
	public static final int TYPE_DEL = 10;
	
	public static final int TYPE_EDIT_SAVE = 11;
	
	public static final int TYPE_EDIT_GIVEUP = 12;
	
	public static final int TYPE_SKYPE = 13;
	
	private String title;
	
	private Integer type;
	
	private String data;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
}