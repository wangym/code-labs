package com.shimoda.oa.model;

public class ContactListVO {
	private Integer userId;
	
	private String tantcardId;
	
	private String lastName;
	
	private String firstName;
	
	private String company;
	
	private String kanaFirstName;
	
	private String kanaLastName;
	
	private String companyKana;
	
	private boolean isExported = false;
	
	private boolean isSelected = false;

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
	
	public String getFullName() {
		return lastName+firstName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getKanaFirstName() {
		return kanaFirstName;
	}

	public void setKanaFirstName(String kanaFirstName) {
		this.kanaFirstName = kanaFirstName;
	}

	public String getKanaLastName() {
		return kanaLastName;
	}

	public void setKanaLastName(String kanaLastName) {
		this.kanaLastName = kanaLastName;
	}

	public String getCompanyKana() {
		return companyKana;
	}

	public void setCompanyKana(String companyKana) {
		this.companyKana = companyKana;
	}

	public boolean getIsExported() {
		return isExported;
	}

	public void setIsExported(boolean isExported) {
		this.isExported = isExported;
	}

	public boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
