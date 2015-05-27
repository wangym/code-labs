package com.shimoda.oa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.shimoda.oa.helper.DbHelper;
import com.shimoda.oa.model.Contact;
import com.shimoda.oa.model.ContactListVO;
import com.shimoda.oa.util.StringUtil;

public class ContactService {
	public static final int ORDERBY_NAME = 1;
	public static final int ORDERBY_COMPANY = 2;
	
	private DbHelper dbHelper;
	
	private SQLiteDatabase db;
	
	public ContactService(Context context){
		this.dbHelper = new DbHelper(context);
	}
	
	/**
	 * 判断指定tantcardId的用户是否已经存在
	 * @param tantcardId
	 * @return
	 */
	public boolean contactExists(String tantcardId){
		if(StringUtil.isEmpty(tantcardId)){
			return false;
		}
		
		if(!openReadableDatabase()){
			return false;
		}
		
		boolean result = false;
		try{
			//查询数据
			String sql = "SELECT user_id FROM user WHERE tantcard_id='"+tantcardId+"'";
			
			Cursor cur = db.rawQuery(sql.toString(), null);
			if (cur.getCount() > 0) {
				result = true;
			}
			cur.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return result;
	}
	
	/**
	 * 根据tantcardId取联系人信息
	 * @param tantcardId
	 * @return
	 */
	public Contact getContactByTantcardId(String tantcardId){
		if(StringUtil.isEmpty(tantcardId)){
			return null;
		}
		
		if(!openReadableDatabase()){
			return null;
		}
		
		Contact contact = null;
		try{
			//查询数据
			String sql = "SELECT * FROM user WHERE tantcard_id='"+tantcardId+"'";
			
			Cursor cur = db.rawQuery(sql.toString(), null);
			if (cur!=null && cur.getCount() > 0 && cur.moveToFirst()) {
				contact = this.convertToContact(cur);
			}
			cur.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return contact;
	}
	
	public Bitmap getUserImgByTantcardId(String tantcardId){
		if(StringUtil.isEmpty(tantcardId)){
			return null;
		}
		
		if(!openReadableDatabase()){
			return null;
		}
		
		Bitmap bitmap = null;
		byte[] userImg = null;
		try{
			//查询数据
			String sql = "SELECT user_img FROM user WHERE tantcard_id='"+tantcardId+"'";
			
			Cursor cur = db.rawQuery(sql.toString(), null);
			if (cur!=null && cur.getCount() > 0 && cur.moveToFirst()) {
				userImg = cur.getBlob(cur.getColumnIndex("user_img"));
			}
			if(cur!=null){
				cur.close();
			}
			if (null != userImg && 0 < userImg.length) {
				bitmap = BitmapFactory.decodeByteArray(userImg, 0, userImg.length);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return bitmap;
	}
	
	/**
	 * 根据tantcardId更新联系人信息
	 * @param contact
	 * @return
	 */
	public boolean updateContact(Contact contact){
		if(contact==null){
			return true;
		}
		
		//检查是否传了ID过来
		if(contact.getTantcardId()==null || contact.getTantcardId().trim().equalsIgnoreCase("")){
			return false;
		}
		
		if(!openWriteableDatabase()){
			return false;
		}
		
		int ret = 0;
		try{
			//更新数据
			ContentValues values = contactToContentValues(contact);
			if(values.size()>0){
				StringBuffer whereClause = new StringBuffer();
				whereClause.append("tantcard_id = ").append('\'').append(contact.getTantcardId()).append('\'');
				ret = db.update("user", values, whereClause.toString(), null);
			}
		}catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	/**
	 * 新增联系人信息
	 * @param contact
	 * @return
	 */
	public boolean insertContact(Contact contact){
		if(contact==null){
			return true;
		}
		
		if(!openWriteableDatabase()){
			return false;
		}
		
		long ret = -1;
		try{
			//插入数据
			ContentValues values = contactToContentValues(contact);
			ret = db.insert("user", null, values);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	public boolean deleteContact(String tantcardId){
		if(!openWriteableDatabase()){
			return false;
		}
		
		long ret = -1;
		try{
			//删除数据
			ret = db.delete("user", "tantcard_id=?", new String[]{tantcardId});
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	/**
	 * 取索引列表
	 * @param contactList 联系人列表
	 * @param type 1:公司名字索引 2:名字索引
	 * @return
	 */
	public List<Map<String,Object>> getKanaIndex(String keyword,Integer type){
		List<ContactListVO> contactList = this.queryContactList(keyword, type);
		if(contactList==null || contactList.isEmpty()){
			return null;
		}
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		int len = contactList.size();
		for(int i=0;i<len;i++){
			ContactListVO contact = contactList.get(i);
			String index = null;
			if(type==ORDERBY_NAME){
				index = getIndex(contact.getKanaLastName());
			}else{
				index = getIndex(contact.getCompanyKana());
			}
			
			if(StringUtil.isEmpty(index)){
				continue;
			}
			
			int pos = indexExists(index, result);
			if(pos>=0){
				List<ContactListVO> list = (List<ContactListVO>)result.get(pos).get("contact");
				list.add(contact);
			}else{
				if(result.size()>0){
					result.get(result.size()-1).put("max", result.size()+i-1);
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("index", index);
				map.put("start", result.size()+i);
				List<ContactListVO> list = new ArrayList<ContactListVO>();
				list.add(contact);
				map.put("contact", list);
				result.add(map);
			}
		}
		
		if(result.size()>0){
			result.get(result.size()-1).put("max", result.size()+len-1);
		}
		
		return result;
	}
	
	
	/**
	 * 查询联系人列表
	 * @param orderby
	 * @return
	 */
	public List<ContactListVO> queryContactList(String keyword,Integer orderby){
		if(!this.openReadableDatabase()){
			return null;
		}
		List<ContactListVO> result = null;
		List<Contact> list = null;
		String sql = "SELECT user_id,address_1,address_2,address_3,affiliation,building_1,building_2,building_3,city_1,city_2,city_3,classification1,classification2,company,company_kana,email_1,email_2,email_3,fax_1,fax_2,fax_3,first_name,ios_person_id,kana_first_name,kana_last_name,last_name,meetingday,mobilephone_1,mobilephone_2,mobilephone_3,note,registrationday,reserve1,reserve2,reserve3,reserve4,reserve5,role,skype_1,skype_2,skype_3,state_1,state_2,state_3,tantcard_id,tel_1,tel_2,tel_3,url,zip_1,zip_2,zip_3 FROM user ";
		if(orderby!=null){
			if(orderby==ORDERBY_NAME){
				sql += " ORDER BY kana_last_name ASC";
			}else if (orderby==ORDERBY_COMPANY) {
				sql += " ORDER BY company_kana ASC";
			}
		}
		try{
			Cursor cur = db.rawQuery(sql, null);
			if(cur!=null){
				list = new ArrayList<Contact>();
				while (cur.moveToNext()){
					list.add(this.convertToContact(cur));
				}
				cur.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.closeDatabase();
		}
		
		if(!StringUtil.isEmpty(keyword) && list!=null){
			//搜索
			for(int i=list.size()-1;i>=0;i--){
				if(list.get(i).toString().indexOf(keyword)<0){
					list.remove(i);
				}
			}
		}
		
		if(list!=null && !list.isEmpty()){
			//转成ContactListVO
			result = new ArrayList<ContactListVO>();
			for(Contact contact:list){
				result.add(this.convertToContactListVO(contact));
			}
			//清除所有数据
			list.clear();
		}
		
		return result;
	}
	
	private boolean openReadableDatabase(){
		try {
			db = this.dbHelper.getReadableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean openWriteableDatabase(){
		try {
			db = this.dbHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean closeDatabase(){
		try {
			if(db!=null && db.isOpen()){
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private String getIndex(String kana){
		if(StringUtil.isEmpty(kana)){
			return null;
		}
		return StringUtil.subString(kana, 1);
	}
	
	private int indexExists(String index,List<Map<String,Object>> indexs){
		if(indexs==null || indexs.isEmpty()){
			return -1;
		}
		
		for(int i=0;i<indexs.size();i++){
			Map<String,Object> map = indexs.get(i);
			if(index.equalsIgnoreCase((String)map.get("index"))){
				return i;
			}
		}
		
		return -1;
	}
	
	private ContactListVO convertToContactListVO(Contact c){
		ContactListVO contact = new ContactListVO();
		
		contact.setCompany(c.getCompany());
		contact.setFirstName(c.getFirstName());
		contact.setLastName(c.getLastName());
		contact.setTantcardId(c.getTantcardId());
		contact.setUserId(c.getUserId());
		contact.setKanaFirstName(c.getKanaFirstName());
		contact.setKanaLastName(c.getKanaLastName());
		contact.setCompanyKana(c.getCompanyKana());
		if(c.getIosPersonId()!=null && c.getIosPersonId()>0){
			contact.setIsExported(true);
		}
		
		return contact;
	}
	
//	private ContactListVO convertToContactListVO(Cursor cur){
//		ContactListVO contact = new ContactListVO();
//		
//		contact.setCompany(cur.getString(cur.getColumnIndex("company")));
//		contact.setFirstName(cur.getString(cur.getColumnIndex("first_name")));
//		contact.setLastName(cur.getString(cur.getColumnIndex("last_name")));
//		contact.setTantcardId(cur.getString(cur.getColumnIndex("tantcard_id")));
//		contact.setUserId(cur.getInt(cur.getColumnIndex("user_id")));
//		contact.setKanaFirstName(cur.getString(cur.getColumnIndex("kana_first_name")));
//		contact.setKanaLastName(cur.getString(cur.getColumnIndex("kana_last_name")));
//		contact.setCompanyKana(cur.getString(cur.getColumnIndex("company_kana")));
//		
//		return contact;
//	}
	
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
		contact.setIosPersonId(cur.getInt(cur.getColumnIndex("ios_person_id")));
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
		contact.setZip1(cur.getString(cur.getColumnIndex("zip_1")));
		contact.setZip2(cur.getString(cur.getColumnIndex("zip_2")));
		contact.setZip3(cur.getString(cur.getColumnIndex("zip_3")));
		
		int index = cur.getColumnIndex("user_img");
		if(index>=0){
			contact.setUserImg(cur.getBlob(index));
		}
		index = cur.getColumnIndex("reserve_img1");
		if(index>=0){
			contact.setReserveImg1(cur.getBlob(index));
		}
		index = cur.getColumnIndex("reserve_img2");
		if(index>=0){
			contact.setReserveImg2(cur.getBlob(index));
		}
		
		return contact;
	}
	
	private ContentValues contactToContentValues(Contact contact){
		ContentValues values = new ContentValues();
		if(contact.getAddress1()!=null){
			values.put("address_1", contact.getAddress1());
		}
		if(contact.getAddress2()!=null){
			values.put("address_2", contact.getAddress2());
		}
		if(contact.getAddress3()!=null){
			values.put("address_3", contact.getAddress3());
		}
		if(contact.getAffiliation()!=null){
			values.put("affiliation", contact.getAffiliation());
		}
		if(contact.getBuilding1()!=null){
			values.put("building_1", contact.getBuilding1());
		}
		if(contact.getBuilding2()!=null){
			values.put("building_3", contact.getBuilding2());
		}
		if(contact.getBuilding3()!=null){
			values.put("building_3", contact.getBuilding3());
		}
		if(contact.getCity1()!=null){
			values.put("city_1", contact.getCity1());
		}
		if(contact.getCity2()!=null){
			values.put("city_2", contact.getCity2());
		}
		if(contact.getCity3()!=null){
			values.put("city_3", contact.getCity3());
		}
		if(contact.getClassification1()!=null){
			values.put("classification1", contact.getClassification1());
		}
		if(contact.getClassification2()!=null){
			values.put("classification2", contact.getClassification2());
		}
		if(contact.getCompany()!=null){
			values.put("company", contact.getCompany());
		}
		if(contact.getCompanyKana()!=null){
			values.put("company_kana", contact.getCompanyKana());
		}
		if(contact.getEmail1()!=null){
			values.put("email_1", contact.getEmail1());
		}
		if(contact.getEmail2()!=null){
			values.put("email_2", contact.getEmail2());
		}
		if(contact.getEmail3()!=null){
			values.put("email_3", contact.getEmail3());
		}
		if(contact.getFax1()!=null){
			values.put("fax_1", contact.getFax1());
		}
		if(contact.getFax2()!=null){
			values.put("fax_2", contact.getFax2());
		}
		if(contact.getFax3()!=null){
			values.put("fax_3", contact.getFax3());
		}
		if(contact.getFirstName()!=null){
			values.put("first_name", contact.getFirstName());
		}
		if(contact.getIosPersonId()!=null){
			values.put("ios_person_id", contact.getIosPersonId());
		}
		if(contact.getKanaFirstName()!=null){
			values.put("kana_first_name", contact.getKanaFirstName());
		}
		if(contact.getKanaLastName()!=null){
			values.put("kana_last_name", contact.getKanaLastName());
		}
		if(contact.getLastName()!=null){
			values.put("last_name", contact.getLastName());
		}
		if(contact.getMeetingDay()!=null){
			values.put("meetingday", contact.getMeetingDay());
		}
		if(contact.getMobilephone1()!=null){
			values.put("mobilephone_1", contact.getMobilephone1());
		}
		if(contact.getMobilephone2()!=null){
			values.put("mobilephone_2", contact.getMobilephone2());
		}
		if(contact.getMobilephone3()!=null){
			values.put("mobilephone_3", contact.getMobilephone3());
		}
		if(contact.getNote()!=null){
			values.put("note", contact.getNote());
		}
		if(contact.getRegistrationDay()!=null){
			values.put("registrationday", contact.getRegistrationDay());
		}
		if(contact.getReserve1()!=null){
			values.put("reserve1", contact.getReserve1());
		}
		if(contact.getReserve2()!=null){
			values.put("reserve2", contact.getReserve2());
		}
		if(contact.getReserve3()!=null){
			values.put("reserve3", contact.getReserve3());
		}
		if(contact.getReserve4()!=null){
			values.put("reserve4", contact.getReserve4());
		}
		if(contact.getReserve5()!=null){
			values.put("reserve5", contact.getReserve5());
		}
		if(contact.getReserveImg1()!=null){
			values.put("reserve_img1", contact.getReserveImg1());
		}
		if(contact.getReserveImg2()!=null){
			values.put("reserve_img2", contact.getReserveImg2());
		}
		if(contact.getRole()!=null){
			values.put("role", contact.getRole());
		}
		if(contact.getSkype1()!=null){
			values.put("skype_1", contact.getSkype1());
		}
		if(contact.getSkype2()!=null){
			values.put("skype_2", contact.getSkype2());
		}
		if(contact.getSkype3()!=null){
			values.put("skype_3", contact.getSkype3());
		}
		if(contact.getState1()!=null){
			values.put("state_1", contact.getState1());
		}
		if(contact.getState2()!=null){
			values.put("state_2", contact.getState2());
		}
		if(contact.getState3()!=null){
			values.put("state_3", contact.getState3());
		}
		if(contact.getTantcardId()!=null){
			values.put("tantcard_id", contact.getTantcardId());
		}
		if(contact.getTel1()!=null){
			values.put("tel_1", contact.getTel1());
		}
		if(contact.getTel2()!=null){
			values.put("tel_2", contact.getTel2());
		}
		if(contact.getTel3()!=null){
			values.put("tel_3", contact.getTel3());
		}
		if(contact.getUrl()!=null){
			values.put("url", contact.getUrl());
		}
		if(contact.getUserImg()!=null){
			values.put("user_img", contact.getUserImg());
		}
		if(contact.getZip1()!=null){
			values.put("zip_1", contact.getZip1());
		}
		if(contact.getZip2()!=null){
			values.put("zip_2", contact.getZip2());
		}
		if(contact.getZip3()!=null){
			values.put("zip_3", contact.getZip3());
		}
		
		return values;
	}
}
