package com.shimoda.oa.model;

public class SourceContactListVO {
	private Integer userId;
	
	private String tantcardId;
	
	private String lastName;
	
	private String firstName;
	
	private String company;
	
	private boolean isImported = false;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public boolean getIsImported() {
		return isImported;
	}

	public void setIsImported(boolean isImported) {
		this.isImported = isImported;
	}
	
}
