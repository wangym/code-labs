package com.shimoda.oa.model;

public class Contact {
	private Integer userId;
	
	private String tantcardId;
	
	private Integer iosPersonId;
	
	private String classification1;
	
	private String classification2;
	
	private String lastName;
	
	private String firstName;
	
	private String kanaLastName;
	
	private String kanaFirstName;
	
	private String company;
	
	private String companyKana;
	
	private String affiliation;
	
	private String role;
	
	private String url;
	
	private String tel1;
	private String fax1;
	private String zip1;
	private String state1;
	private String city1;
	private String address1;
	private String building1;
	private String email1;
	private String mobilephone1;
	private String skype1;
	
	private String tel2;
	private String fax2;
	private String zip2;
	private String state2;
	private String city2;
	private String address2;
	private String building2;
	private String email2;
	private String mobilephone2;
	private String skype2;
	
	private String tel3;
	private String fax3;
	private String zip3;
	private String state3;
	private String city3;
	private String address3;
	private String building3;
	private String email3;
	private String mobilephone3;
	private String skype3;
	
	private String meetingDay;
	
	private String registrationDay;
	
	private String note;
	
	private byte[] userImg;
	
	private byte[] reserveImg1;
	
	private byte[] reserveImg2;
	
	private String reserve1;
	
	private String reserve2;
	
	private String reserve3;
	
	private String reserve4;
	
	private String reserve5;

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

	public Integer getIosPersonId() {
		return iosPersonId;
	}

	public void setIosPersonId(Integer iosPersonId) {
		this.iosPersonId = iosPersonId;
	}

	public String getClassification1() {
		return classification1;
	}

	public void setClassification1(String classification1) {
		this.classification1 = classification1;
	}

	public String getClassification2() {
		return classification2;
	}

	public void setClassification2(String classification2) {
		this.classification2 = classification2;
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
		return lastName + " " + firstName;
	}
	
	public String getKanaFullName() {
		return kanaLastName + " " + kanaFirstName;
	}

	public String getKanaLastName() {
		return kanaLastName;
	}

	public void setKanaLastName(String kanaLastName) {
		this.kanaLastName = kanaLastName;
	}

	public String getKanaFirstName() {
		return kanaFirstName;
	}

	public void setKanaFirstName(String kanaFirstName) {
		this.kanaFirstName = kanaFirstName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyKana() {
		return companyKana;
	}

	public void setCompanyKana(String companyKana) {
		this.companyKana = companyKana;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTel1() {
		return tel1;
	}

	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}

	public String getFax1() {
		return fax1;
	}

	public void setFax1(String fax1) {
		this.fax1 = fax1;
	}

	public String getZip1() {
		return zip1;
	}

	public void setZip1(String zip1) {
		this.zip1 = zip1;
	}

	public String getState1() {
		return state1;
	}

	public void setState1(String state1) {
		this.state1 = state1;
	}

	public String getCity1() {
		return city1;
	}

	public void setCity1(String city1) {
		this.city1 = city1;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getBuilding1() {
		return building1;
	}

	public void setBuilding1(String building1) {
		this.building1 = building1;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getMobilephone1() {
		return mobilephone1;
	}

	public void setMobilephone1(String mobilephone1) {
		this.mobilephone1 = mobilephone1;
	}

	public String getSkype1() {
		return skype1;
	}

	public void setSkype1(String skype1) {
		this.skype1 = skype1;
	}

	public String getTel2() {
		return tel2;
	}

	public void setTel2(String tel2) {
		this.tel2 = tel2;
	}

	public String getFax2() {
		return fax2;
	}

	public void setFax2(String fax2) {
		this.fax2 = fax2;
	}

	public String getZip2() {
		return zip2;
	}

	public void setZip2(String zip2) {
		this.zip2 = zip2;
	}

	public String getState2() {
		return state2;
	}

	public void setState2(String state2) {
		this.state2 = state2;
	}

	public String getCity2() {
		return city2;
	}

	public void setCity2(String city2) {
		this.city2 = city2;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getBuilding2() {
		return building2;
	}

	public void setBuilding2(String building2) {
		this.building2 = building2;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getMobilephone2() {
		return mobilephone2;
	}

	public void setMobilephone2(String mobilephone2) {
		this.mobilephone2 = mobilephone2;
	}

	public String getSkype2() {
		return skype2;
	}

	public void setSkype2(String skype2) {
		this.skype2 = skype2;
	}

	public String getTel3() {
		return tel3;
	}

	public void setTel3(String tel3) {
		this.tel3 = tel3;
	}

	public String getFax3() {
		return fax3;
	}

	public void setFax3(String fax3) {
		this.fax3 = fax3;
	}

	public String getZip3() {
		return zip3;
	}

	public void setZip3(String zip3) {
		this.zip3 = zip3;
	}

	public String getState3() {
		return state3;
	}

	public void setState3(String state3) {
		this.state3 = state3;
	}

	public String getCity3() {
		return city3;
	}

	public void setCity3(String city3) {
		this.city3 = city3;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getBuilding3() {
		return building3;
	}

	public void setBuilding3(String building3) {
		this.building3 = building3;
	}

	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	public String getMobilephone3() {
		return mobilephone3;
	}

	public void setMobilephone3(String mobilephone3) {
		this.mobilephone3 = mobilephone3;
	}

	public String getSkype3() {
		return skype3;
	}

	public void setSkype3(String skype3) {
		this.skype3 = skype3;
	}

	public String getMeetingDay() {
		return meetingDay;
	}

	public void setMeetingDay(String meetingDay) {
		this.meetingDay = meetingDay;
	}

	public String getRegistrationDay() {
		return registrationDay;
	}

	public void setRegistrationDay(String registrationDay) {
		this.registrationDay = registrationDay;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public String getReserve4() {
		return reserve4;
	}

	public void setReserve4(String reserve4) {
		this.reserve4 = reserve4;
	}

	public String getReserve5() {
		return reserve5;
	}

	public void setReserve5(String reserve5) {
		this.reserve5 = reserve5;
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
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(this.getAddress1()).append(" ");
		sb.append(this.getAddress2()).append(" ");
		sb.append(this.getAddress3()).append(" ");
		sb.append(this.getAffiliation()).append(" ");
		sb.append(this.getBuilding1()).append(" ");
		sb.append(this.getBuilding2()).append(" ");
		sb.append(this.getBuilding3()).append(" ");
		sb.append(this.getCity1()).append(" ");
		sb.append(this.getCity2()).append(" ");
		sb.append(this.getCity3()).append(" ");
		sb.append(this.getClassification1()).append(" ");
		sb.append(this.getClassification2()).append(" ");
		sb.append(this.getCompany()).append(" ");
		sb.append(this.getCompanyKana()).append(" ");
		sb.append(this.getEmail1()).append(" ");
		sb.append(this.getEmail2()).append(" ");
		sb.append(this.getEmail3()).append(" ");
		sb.append(this.getFax1()).append(" ");
		sb.append(this.getFax2()).append(" ");
		sb.append(this.getFax3()).append(" ");
		sb.append(this.getFirstName()).append(" ");
		sb.append(this.getKanaFirstName()).append(" ");
		sb.append(this.getKanaLastName()).append(" ");
		sb.append(this.getLastName()).append(" ");
		sb.append(this.getMeetingDay()).append(" ");
		sb.append(this.getMobilephone1()).append(" ");
		sb.append(this.getMobilephone2()).append(" ");
		sb.append(this.getMobilephone3()).append(" ");
		sb.append(this.getNote()).append(" ");
		sb.append(this.getRegistrationDay()).append(" ");
		sb.append(this.getReserve1()).append(" ");
		sb.append(this.getReserve2()).append(" ");
		sb.append(this.getReserve3()).append(" ");
		sb.append(this.getReserve4()).append(" ");
		sb.append(this.getReserve5()).append(" ");
		sb.append(this.getRole()).append(" ");
		sb.append(this.getSkype1()).append(" ");
		sb.append(this.getSkype2()).append(" ");
		sb.append(this.getSkype3()).append(" ");
		sb.append(this.getState1()).append(" ");
		sb.append(this.getState2()).append(" ");
		sb.append(this.getState3()).append(" ");
		sb.append(this.getTantcardId()).append(" ");
		sb.append(this.getTel1()).append(" ");
		sb.append(this.getTel2()).append(" ");
		sb.append(this.getTel3()).append(" ");
		sb.append(this.getUrl()).append(" ");
		sb.append(this.getZip1()).append(" ");
		sb.append(this.getZip2()).append(" ");
		sb.append(this.getZip3());
		
		return sb.toString();
	}
}
