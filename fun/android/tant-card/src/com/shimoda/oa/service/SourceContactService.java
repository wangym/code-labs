package com.shimoda.oa.service;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.shimoda.oa.model.Contact;
import com.shimoda.oa.model.SourceContactListVO;

public class SourceContactService {
	private static final String SOURCE_TABLE_NAME = "user";
	
	private String path;
	
	private SQLiteDatabase sourceDb;
	
	/**
	 * 需要导入的联系人总个数
	 */
	private Integer count;
	
	/**
	 * 需要导入的联系人列表
	 */
	private List<SourceContactListVO> contactList;
	
	
	public SourceContactService(String path){
		this.path = path;
	}
		
	/**
	 * 获取导入db文件中的联系人总数
	 * @param path db文件路径
	 * @return
	 */
	public Integer getContactCount(){
		if(count==null){
			//重新加载所有联系人
			if(!loadAllContact()){
				return 0;
			}
		}
		
		
		return this.count;
	}
	
	/**
	 * 获取联系人列表
	 * @param start 分页开始条数
	 * @param num 取记录数
	 * @param loadAll 是否取所有联系人
	 * @return
	 */
	public List<SourceContactListVO> getContactList(Integer start,Integer num, boolean loadAll){
		if(this.contactList==null){
			//重新加载所有联系人
			if(!loadAllContact()){
				return null;
			}
		}
		if(loadAll){
			//返回所有联系人
			return this.contactList;
		}
		
		//获取分页数据
		if(this.contactList==null || this.contactList.isEmpty() || this.contactList.size()<start){
			return null;
		}
		
		List<SourceContactListVO> result = new ArrayList<SourceContactListVO>();
		int max = start+num-1;
		if(this.contactList.size()<max){
			max = this.contactList.size();
		}
		
		for(int i=start;i<=max;i++){
			result.add(this.contactList.get(i));
		}
		
		return result;
	}
	
	/**
	 * 根据联系人自增ID取联系人信息
	 * @param userId
	 * @return
	 */
	public Contact getConatctByUserId(Integer userId){
		if(!this.openSourceDb()){
			return null;
		}
		
		Contact contact = null;
		String sql = "SELECT * FROM "+SOURCE_TABLE_NAME+" WHERE user_id="+userId;
		try{
			Cursor cur = sourceDb.rawQuery(sql, null);
			if(cur!=null && cur.moveToNext()){
				contact = this.convertToContact(cur);
			}
			cur.close();
		}catch (Exception e) {
			//do nothing
			e.printStackTrace();
		}finally{
			this.closeSourceDb();
		}
		
		return contact;
	}
	
	/**
	 * 根据联系人自增ID删除联系人信息
	 * @param userId
	 * @return
	 */
	public boolean delContactByUserId(Integer userId){
		if(!this.openSourceDb()){
			return false;
		}
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("user_id = ").append('\'').append(userId).append('\'');
		try{
			int ret = sourceDb.delete(SOURCE_TABLE_NAME, whereClause.toString(), null);
			return ret>0;
		}catch (Exception e) {
			//do nothing
			e.printStackTrace();
		}finally{
			this.closeSourceDb();
		}
		return false;
	}
	
	
	/**
	 * 关闭导入源文件的数据库
	 * @return
	 */
	private boolean closeSourceDb(){
		if(sourceDb==null){
			return true;
		}
		
		try {
			sourceDb.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 打开导入源文件的数据库
	 * @param path 数据库文件
	 * @param flags 
	 * @return
	 */
	private boolean openSourceDb(){
		try{
			sourceDb = SQLiteDatabase.openDatabase(this.path, null, SQLiteDatabase.OPEN_READWRITE);
		}catch(SQLiteException ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 加载所有联系人
	 */
	private boolean loadAllContact(){
		if(!this.openSourceDb()){
			return false;
		}
		String sql = "SELECT user_id,tantcard_id,first_name,last_name,company FROM "+SOURCE_TABLE_NAME;
		try{
			Cursor cur = sourceDb.rawQuery(sql, null);
			if(cur==null || cur.getCount()==0){
				this.count = 0;
				this.contactList = new ArrayList<SourceContactListVO>();
			}else{
				this.count = cur.getCount();
				this.contactList = new ArrayList<SourceContactListVO>();
				while (cur.moveToNext()){
					this.contactList.add(this.convertToSourceContactListVO(cur));
				}
				cur.close();
			}
		}catch (Exception e) {
			//do nothing
			return false;
		}finally{
			this.closeSourceDb();
		}
		return true;
	}
	
	private SourceContactListVO convertToSourceContactListVO(Cursor cur){
		SourceContactListVO contact = new SourceContactListVO();
		
		contact.setCompany(cur.getString(cur.getColumnIndex("company")));
		contact.setFirstName(cur.getString(cur.getColumnIndex("first_name")));
		contact.setLastName(cur.getString(cur.getColumnIndex("last_name")));
		contact.setTantcardId(cur.getString(cur.getColumnIndex("tantcard_id")));
		contact.setUserId(cur.getInt(cur.getColumnIndex("user_id")));
		
		return contact;
	}
	
	private Contact convertToContact(Cursor cur){
		Contact contact = new Contact();
		
		contact.setAddress1(cur.getString(cur.getColumnIndex("address_1")));
		contact.setAddress2(cur.getString(cur.getColumnIndex("address_2")));
		contact.setAddress3(cur.getString(cur.getColumnIndex("address_3")));
		contact.setAffiliation(cur.getString(cur.getColumnIndex("affiliation")));
		contact.setBuilding1(cur.getString(cur.getColumnIndex("building_1")));
		contact.setBuilding2(cur.getString(cur.getColumnIndex("building_2")));
		contact.setBuilding3(cur.getString(cur.getColumnIndex("building_3")));
		contact.setCity1(cur.getString(cur.getColumnIndex("city_1")));
		contact.setCity2(cur.getString(cur.getColumnIndex("city_2")));
		contact.setCity3(cur.getString(cur.getColumnIndex("city_3")));
		contact.setClassification1(cur.getString(cur.getColumnIndex("classification1")));
		contact.setClassification2(cur.getString(cur.getColumnIndex("classification2")));
		contact.setCompany(cur.getString(cur.getColumnIndex("company")));
		contact.setCompanyKana(cur.getString(cur.getColumnIndex("company_kana")));
		contact.setEmail1(cur.getString(cur.getColumnIndex("email_1")));
		contact.setEmail2(cur.getString(cur.getColumnIndex("email_2")));
		contact.setEmail3(cur.getString(cur.getColumnIndex("email_3")));
		contact.setFax1(cur.getString(cur.getColumnIndex("fax_1")));
		contact.setFax2(cur.getString(cur.getColumnIndex("fax_2")));
		contact.setFax3(cur.getString(cur.getColumnIndex("fax_3")));
		contact.setFirstName(cur.getString(cur.getColumnIndex("first_name")));
		contact.setKanaFirstName(cur.getString(cur.getColumnIndex("kana_first_name")));
		contact.setKanaLastName(cur.getString(cur.getColumnIndex("kana_last_name")));
		contact.setLastName(cur.getString(cur.getColumnIndex("last_name")));
		contact.setMeetingDay(cur.getString(cur.getColumnIndex("meetingday")));
		contact.setMobilephone1(cur.getString(cur.getColumnIndex("mobilephone_1")));
		contact.setMobilephone2(cur.getString(cur.getColumnIndex("mobilephone_2")));
		contact.setMobilephone3(cur.getString(cur.getColumnIndex("mobilephone_3")));
		contact.setNote(cur.getString(cur.getColumnIndex("note")));
		contact.setRegistrationDay(cur.getString(cur.getColumnIndex("registrationday")));
		contact.setReserve1(cur.getString(cur.getColumnIndex("reserve1")));
		contact.setReserve2(cur.getString(cur.getColumnIndex("reserve2")));
		contact.setReserve3(cur.getString(cur.getColumnIndex("reserve3")));
		contact.setReserve4(cur.getString(cur.getColumnIndex("reserve4")));
		contact.setReserve5(cur.getString(cur.getColumnIndex("reserve5")));
		contact.setReserveImg1(cur.getBlob(cur.getColumnIndex("reserve_img1")));
		contact.setReserveImg2(cur.getBlob(cur.getColumnIndex("reserve_img2")));
		contact.setRole(cur.getString(cur.getColumnIndex("role")));
		contact.setSkype1(cur.getString(cur.getColumnIndex("skype_1")));
		contact.setSkype2(cur.getString(cur.getColumnIndex("skype_2")));
		contact.setSkype3(cur.getString(cur.getColumnIndex("skype_3")));
		contact.setState1(cur.getString(cur.getColumnIndex("state_1")));
		contact.setState2(cur.getString(cur.getColumnIndex("state_2")));
		contact.setState3(cur.getString(cur.getColumnIndex("state_3")));
		contact.setTantcardId(cur.getString(cur.getColumnIndex("tantcard_id")));
		contact.setTel1(cur.getString(cur.getColumnIndex("tel_1")));
		contact.setTel2(cur.getString(cur.getColumnIndex("tel_2")));
		contact.setTel3(cur.getString(cur.getColumnIndex("tel_3")));
		contact.setUrl(cur.getString(cur.getColumnIndex("url")));
		contact.setUserId(cur.getInt(cur.getColumnIndex("user_id")));
		contact.setUserImg(cur.getBlob(cur.getColumnIndex("user_img")));
		contact.setZip1(cur.getString(cur.getColumnIndex("zip_1")));
		contact.setZip2(cur.getString(cur.getColumnIndex("zip_2")));
		contact.setZip3(cur.getString(cur.getColumnIndex("zip_3")));
		
		return contact;
	}
}
