package com.shimoda.oa.model;

public class ContactImageVO {
	private Integer userId;
	
	private String tantcardId;
	
	private byte[] userImg;
	
	private byte[] reserveImg1;
	
	private byte[] reserveImg2;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public byte[] getUserImg() {
		return userImg;
	}

	public void setUserImg(byte[] userImg) {
		this.userImg = userImg;
	}

	public byte[] getReserveImg1() {
		return reserveImg1;
	}

	public void setReserveImg1(byte[] reserveImg1) {
		this.reserveImg1 = reserveImg1;
	}

	public byte[] getReserveImg2() {
		return reserveImg2;
	}

	public void setReserveImg2(byte[] reserveImg2) {
		this.reserveImg2 = reserveImg2;
	}

	public String getTantcardId() {
		return tantcardId;
	}

	public void setTantcardId(String tantcardId) {
		this.tantcardId = tantcardId;
	}
}