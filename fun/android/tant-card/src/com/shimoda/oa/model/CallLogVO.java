package com.shimoda.oa.model;

public class CallLogVO {
	private Integer id;
	
	private String tantcardId;
	
	private String lastName;
	
	private String firstName;
	
	private String tel;
	
	private String callDate;
	
	/**
	 * 0:初始 1:可删除
	 */
	private Integer opStatus = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTantcardId() {
		return tantcardId;
	}

	public void setTantcardId(String tantcardId) {
		this.tantcardId = tantcardId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getCallDate() {
		return callDate;
	}

	public void setCallDate(String callDate) {
		this.callDate = callDate;
	}

	public Integer getOpStatus() {
		return opStatus;
	}

	public void setOpStatus(Integer opStatus) {
		this.opStatus = opStatus;
	}
	
	public String getFullName() {
		return lastName + " " + firstName;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
}