package com.shimoda.oa.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.ContactMethodsColumns;
import android.provider.Contacts.OrganizationColumns;
import android.provider.Contacts.Organizations;
import android.provider.Contacts.People;
import android.provider.Contacts.PeopleColumns;
import android.provider.Contacts.Phones;
import android.provider.Contacts.PhonesColumns;
import android.provider.ContactsContract;

import com.shimoda.oa.model.Contact;
import com.shimoda.oa.util.ContactConstants;
import com.shimoda.oa.util.EnvironmentUtil;
import com.shimoda.oa.util.StringUtil;

public class SystemContactService {
	private Context context;

	public SystemContactService(Context context) {
		this.context = context;
	}

	/**
	 * 检查联系人是否存在
	 * 
	 * @param contactId
	 * @return
	 */
	public boolean contactExists(Integer contactId) {
		boolean result = false;
		try {
			Cursor cur = null;
			if (EnvironmentUtil.aboveDonut()) {
				cur = context.getContentResolver().query(
						ContactConstants.PEOPLE_CONTENT_URI,
						new String[] { "deleted" },
						ContactConstants.PEOPLE_ID + "=?",
						new String[] { String.valueOf(contactId) }, null);
				if (cur != null && cur.moveToNext()) {
					if (cur.getInt(cur.getColumnIndex("deleted")) != 1) {
						result = true;
					}
				}
			} else {
				cur = context.getContentResolver().query(People.CONTENT_URI,
						new String[] { BaseColumns._ID }, "people._id=?",
						new String[] { String.valueOf(contactId) }, null);
				if (cur != null && cur.moveToNext()) {
					result = true;
				}
			}
			if (cur != null) {
				cur.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 更新联系人
	 * 
	 * @param contact
	 * @return
	 */
	public boolean updateContact(Contact contact) {
		boolean result = false;

		// 更新基本信息
		try {
			if (EnvironmentUtil.aboveDonut()) {
				updateOther2(contact);
			} else {
				ContentValues values = contactToPeopleContentValues(contact);
				try {
					context.getContentResolver().update(
							Uri.parse(People.CONTENT_URI.toString() + "/"
									+ contact.getIosPersonId()),
							values,
							"people._id=?",
							new String[] { String.valueOf(contact
									.getIosPersonId()) });
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				// 更细其他信息
				updateOther(contact);
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 新增联系人
	 * 
	 * @param contact
	 * @return
	 */
	public Integer insertContact(Contact contact) {
		Integer id = null;
		try {
			if (EnvironmentUtil.aboveDonut()) {
				ContentValues values = new ContentValues();
				Uri uri = context.getContentResolver().insert(
						ContactConstants.PEOPLE_CONTENT_URI, values);
				id = new Integer((int) ContentUris.parseId(uri));

				if (id > 0) {
					// 插入其他信息
					insertOther2(id, contact);
				}
			} else {
				// 插入联系人姓名等基础数据
				ContentValues values = contactToPeopleContentValues(contact);
				Uri uri = People.createPersonInMyContactsGroup(context
						.getContentResolver(), values);
				id = new Integer((int) ContentUris.parseId(uri));
				// 插入其他数据
				if (id != null && id > 0) {
					insertOther(id, contact);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	private ContentValues contactToPeopleContentValues(Contact contact) {
		ContentValues values = new ContentValues();
		if (contact.getFullName() != null) {
			values.put(PeopleColumns.NAME, contact.getFullName());
		}
		if (!StringUtil.isEmpty(contact.getNote())) {
			values.put(PeopleColumns.NOTES, contact.getNote());
		}
		return values;
	}

	private void updateOther(Contact contact) {
		Integer id = contact.getIosPersonId();
		// 清空原有数据
		ContentResolver resolver = context.getContentResolver();

		List<Integer> ids = new ArrayList<Integer>();
		Cursor cur = null;
		// phone
		try {
			// 查询列表
			cur = resolver.query(Phones.CONTENT_URI, new String[] { "_id" },
					Phones.PERSON_ID + "=?",
					new String[] { String.valueOf(id) }, null);
			if (cur != null) {
				while (cur.moveToNext()) {
					ids.add(cur.getInt(0));
				}
				cur.close();
			}
			// 删除原有数据
			for (Integer i : ids) {
				resolver.delete(Uri.parse(Phones.CONTENT_URI.toString() + "/"
						+ i), "_id=?", new String[] { String.valueOf(i) });
			}

		} catch (Exception e) {
			// do nothing
			e.printStackTrace();
		}

		// contact methods
		ids.clear();
		try {
			// 查询列表
			cur = resolver.query(ContactMethods.CONTENT_URI,
					new String[] { "_id" }, ContactMethods.PERSON_ID + "=?",
					new String[] { String.valueOf(id) }, null);
			if (cur != null) {
				while (cur.moveToNext()) {
					ids.add(cur.getInt(0));
				}
				cur.close();
			}
			// 删除原有数据
			for (Integer i : ids) {
				resolver
						.delete(Uri.parse(ContactMethods.CONTENT_URI.toString()
								+ "/" + i), "_id=?", new String[] { String
								.valueOf(i) });
			}

		} catch (Exception e) {
			// do nothing
			e.printStackTrace();
		}

		// organizations
		ids.clear();
		try {
			// 查询列表
			cur = resolver.query(Organizations.CONTENT_URI,
					new String[] { "_id" }, OrganizationColumns.PERSON_ID + "=?",
					new String[] { String.valueOf(id) }, null);
			if (cur != null) {
				while (cur.moveToNext()) {
					ids.add(cur.getInt(0));
				}
				cur.close();
			}
			// 删除原有数据
			for (Integer i : ids) {
				resolver
						.delete(Uri.parse(Organizations.CONTENT_URI.toString()
								+ "/" + i), "_id=?", new String[] { String
								.valueOf(i) });
			}

		} catch (Exception e) {
			// do nothing
			e.printStackTrace();
		}

		// 新增数据
		insertOther(id, contact);
	}

	private void updateOther2(Contact contact) {
		Integer id = contact.getIosPersonId();
		// 清空原有数据
		ContentResolver resolver = context.getContentResolver();
		// data
		resolver.delete(ContactConstants.DATA_CONTENT_URI,
				ContactConstants.RAW_CONTACT_ID + "=?", new String[] { String
						.valueOf(id) });

		// 新增数据
		insertOther2(id, contact);
	}

	private void insertOther(Integer id, Contact contact) {
		ContentValues values = new ContentValues();
		ContentResolver resolver = context.getContentResolver();
		// phone
		if (!StringUtil.isEmpty(contact.getTel1())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_WORK);
			values.put(PhonesColumns.NUMBER, contact.getTel1());
			resolver.insert(Phones.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getTel2())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_WORK);
			values.put(PhonesColumns.NUMBER, contact.getTel2());
			resolver.insert(Phones.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getTel3())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_WORK);
			values.put(PhonesColumns.NUMBER, contact.getTel3());
			resolver.insert(Phones.CONTENT_URI, values);
		}

		if (!StringUtil.isEmpty(contact.getMobilephone1())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_MOBILE);
			values.put(PhonesColumns.NUMBER, contact.getMobilephone1());
			resolver.insert(Phones.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getMobilephone2())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_MOBILE);
			values.put(PhonesColumns.NUMBER, contact.getMobilephone2());
			resolver.insert(Phones.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getMobilephone3())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_MOBILE);
			values.put(PhonesColumns.NUMBER, contact.getMobilephone3());
			resolver.insert(Phones.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getFax1())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_FAX_WORK);
			values.put(PhonesColumns.NUMBER, contact.getFax1());
			resolver.insert(Phones.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getFax2())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_FAX_WORK);
			values.put(PhonesColumns.NUMBER, contact.getFax2());
			resolver.insert(Phones.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getFax3())) {
			values.clear();
			values.put(Phones.PERSON_ID, id);
			values.put(PhonesColumns.TYPE, PhonesColumns.TYPE_FAX_WORK);
			values.put(PhonesColumns.NUMBER, contact.getFax3());
			resolver.insert(Phones.CONTENT_URI, values);
		}

		// contact methods
		if (!StringUtil.isEmpty(contact.getEmail1())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethodsColumns.KIND, Contacts.KIND_EMAIL);
			values.put(ContactMethodsColumns.DATA, contact.getEmail1());
			values.put(ContactMethodsColumns.TYPE, ContactMethodsColumns.TYPE_HOME);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getEmail2())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethodsColumns.KIND, Contacts.KIND_EMAIL);
			values.put(ContactMethodsColumns.DATA, contact.getEmail2());
			values.put(ContactMethodsColumns.TYPE, Contacts.ContactMethods.TYPE_HOME);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getEmail3())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_EMAIL);
			values.put(ContactMethods.DATA, contact.getEmail3());
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_HOME);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}

		if (!StringUtil.isEmpty(contact.getSkype1())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_IM);
			values.put(ContactMethods.DATA, contact.getSkype1());
			// TODO
			values.put(ContactMethods.AUX_DATA, "pre:3");
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getSkype2())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_IM);
			values.put(ContactMethods.DATA, contact.getSkype2());
			// TODO
			values.put(ContactMethods.AUX_DATA, "pre:3");
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getSkype3())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_IM);
			values.put(ContactMethods.DATA, contact.getSkype3());
			// TODO
			values.put(ContactMethods.AUX_DATA, "pre:3");
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}

		if (!StringUtil.isEmpty(contact.getAddress1())
				|| !StringUtil.isEmpty(contact.getState1())
				|| !StringUtil.isEmpty(contact.getCity1())
				|| !StringUtil.isEmpty(contact.getZip1())) {
			String address = contact.getAddress1() + "\n";
			if (StringUtil.isEmpty(contact.getCity1())) {
				address += "";
			} else {
				address += contact.getCity1();
			}
			address += ",";
			if (StringUtil.isEmpty(contact.getState1())) {
				address += "";
			} else {
				address += contact.getState1();
			}
			address += " ";
			if (StringUtil.isEmpty(contact.getZip1())) {
				address += "";
			} else {
				address += contact.getZip1();
			}

			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_POSTAL);
			values.put(ContactMethods.DATA, address);
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getAddress2())
				|| !StringUtil.isEmpty(contact.getState2())
				|| !StringUtil.isEmpty(contact.getCity2())
				|| !StringUtil.isEmpty(contact.getZip2())) {
			String address = contact.getAddress2() + "\n";
			if (StringUtil.isEmpty(contact.getCity2())) {
				address += "";
			} else {
				address += contact.getCity2();
			}
			address += ",";
			if (StringUtil.isEmpty(contact.getState2())) {
				address += "";
			} else {
				address += contact.getState2();
			}
			address += " ";
			if (StringUtil.isEmpty(contact.getZip2())) {
				address += "";
			} else {
				address += contact.getZip2();
			}

			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_POSTAL);
			values.put(ContactMethods.DATA, address);
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getAddress3())
				|| !StringUtil.isEmpty(contact.getState3())
				|| !StringUtil.isEmpty(contact.getCity3())
				|| !StringUtil.isEmpty(contact.getZip3())) {
			String address = contact.getAddress3() + "\n";
			if (StringUtil.isEmpty(contact.getCity3())) {
				address += "";
			} else {
				address += contact.getCity3();
			}
			address += ",";
			if (StringUtil.isEmpty(contact.getState3())) {
				address += "";
			} else {
				address += contact.getState3();
			}
			address += " ";
			if (StringUtil.isEmpty(contact.getZip3())) {
				address += "";
			} else {
				address += contact.getZip3();
			}

			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_POSTAL);
			values.put(ContactMethods.DATA, address);
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getBuilding1())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_POSTAL);
			values.put(ContactMethods.DATA, contact.getBuilding1());
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getBuilding2())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_POSTAL);
			values.put(ContactMethods.DATA, contact.getBuilding2());
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getBuilding3())) {
			values.clear();
			values.put(ContactMethods.PERSON_ID, id);
			values.put(ContactMethods.KIND, Contacts.KIND_POSTAL);
			values.put(ContactMethods.DATA, contact.getBuilding3());
			values.put(ContactMethods.TYPE, Contacts.ContactMethods.TYPE_OTHER);
			resolver.insert(ContactMethods.CONTENT_URI, values);
		}

		// organizations
		if (!StringUtil.isEmpty(contact.getCompany())
				|| !StringUtil.isEmpty(contact.getAffiliation())
				|| !StringUtil.isEmpty(contact.getRole())) {
			values.clear();
			values.put(Organizations.PERSON_ID, id);
			values.put(Organizations.COMPANY, contact.getCompany());
			values.put(Organizations.TYPE, Organizations.TYPE_WORK);
			values.put(Organizations.TITLE, contact.getAffiliation() + " "
					+ contact.getRole());
			resolver.insert(Organizations.CONTENT_URI, values);
		}

		// if(contact.getClassification1()!=null){
		// values.put("classification1", contact.getClassification1());
		// }
		// if(contact.getClassification2()!=null){
		// values.put("classification2", contact.getClassification2());
		// }

		// if(contact.getMeetingDay()!=null){
		// values.put("meetingday", contact.getMeetingDay());
		// }

		// if(contact.getRegistrationDay()!=null){
		// values.put("registrationday", contact.getRegistrationDay());
		// }
		// if(contact.getReserve1()!=null){
		// values.put("reserve1", contact.getReserve1());
		// }
		// if(contact.getReserve2()!=null){
		// values.put("reserve2", contact.getReserve2());
		// }
		// if(contact.getReserve3()!=null){
		// values.put("reserve3", contact.getReserve3());
		// }
		// if(contact.getReserve4()!=null){
		// values.put("reserve4", contact.getReserve4());
		// }
		// if(contact.getReserve5()!=null){
		// values.put("reserve5", contact.getReserve5());
		// }
		// if(contact.getReserveImg1()!=null){
		// values.put("reserve_img1", contact.getReserveImg1());
		// }
		// if(contact.getReserveImg2()!=null){
		// values.put("reserve_img2", contact.getReserveImg2());
		// }
		// if(contact.getUrl()!=null){
		// values.put("url", contact.getUrl());
		// }
		if(contact.getUserImg()!=null){
			byte[] userImg = contact.getUserImg();
			if (null != userImg && 0 < userImg.length){
				Bitmap image = BitmapFactory.decodeByteArray(userImg, 0,userImg.length);
			
				values.put("user_img", contact.getUserImg());
				Contacts.People.setPhotoData(resolver, ContactMethods.CONTENT_URI, userImg);
			}
		}
	}

	private void insertOther2(Integer id, Contact contact) {
		ContentValues values = new ContentValues();
		ContentResolver resolver = context.getContentResolver();
		// 姓名
		values.clear();
		values.put(ContactConstants.RAW_CONTACT_ID, id);
		values.put(ContactConstants.COLUMN_MIMETYPE,
				ContactConstants.MIMETYPE_NAME);
		values.put(ContactConstants.COLUMN_DATA1, contact.getFullName());
		context.getContentResolver().insert(ContactConstants.DATA_CONTENT_URI,
				values);

		// phone
		if (!StringUtil.isEmpty(contact.getTel1())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getTel1());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_TEL);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getTel2())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getTel2());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_TEL);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getTel3())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getTel3());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_TEL);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		if (!StringUtil.isEmpty(contact.getMobilephone1())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values
					.put(ContactConstants.COLUMN_DATA1, contact
							.getMobilephone1());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_MOBILE);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getMobilephone2())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values
					.put(ContactConstants.COLUMN_DATA1, contact
							.getMobilephone2());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_MOBILE);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getMobilephone3())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values
					.put(ContactConstants.COLUMN_DATA1, contact
							.getMobilephone3());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_MOBILE);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getFax1())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getFax1());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_FAX);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getFax2())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getFax2());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_FAX);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getFax3())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_PHONE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getFax3());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.PHONE_TYPE_FAX);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		// contact methods
		if (!StringUtil.isEmpty(contact.getEmail1())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_EMAIL);
			values.put(ContactConstants.COLUMN_DATA1, contact.getEmail1());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.EMAIL_TYPE_WORK);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getEmail2())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_EMAIL);
			values.put(ContactConstants.COLUMN_DATA1, contact.getEmail2());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.EMAIL_TYPE_WORK);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getEmail3())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_EMAIL);
			values.put(ContactConstants.COLUMN_DATA1, contact.getEmail3());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.EMAIL_TYPE_WORK);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		if (!StringUtil.isEmpty(contact.getSkype1())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_IM);
			values.put(ContactConstants.COLUMN_DATA1, contact.getSkype1());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.IM_TYPE_SKYPE);
			values.put(ContactConstants.COLUMN_DATA5,
					ContactConstants.DATA5_SKYPE);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getSkype2())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_IM);
			values.put(ContactConstants.COLUMN_DATA1, contact.getSkype2());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.IM_TYPE_SKYPE);
			values.put(ContactConstants.COLUMN_DATA5,
					ContactConstants.DATA5_SKYPE);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getSkype3())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_IM);
			values.put(ContactConstants.COLUMN_DATA1, contact.getSkype3());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.IM_TYPE_SKYPE);
			values.put(ContactConstants.COLUMN_DATA5,
					ContactConstants.DATA5_SKYPE);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		// TODO 地址分隔还有问题
		if (!StringUtil.isEmpty(contact.getAddress1())
				|| !StringUtil.isEmpty(contact.getState1())
				|| !StringUtil.isEmpty(contact.getCity1())
				|| !StringUtil.isEmpty(contact.getZip1())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_ADDRESS);
			String address = contact.getAddress1() + "\n";
			if (StringUtil.isEmpty(contact.getCity1())) {
				address += "";
			} else {
				address += contact.getCity1();
			}
			address += ",";
			if (StringUtil.isEmpty(contact.getState1())) {
				address += "";
			} else {
				address += contact.getState1();
			}
			address += " ";
			if (StringUtil.isEmpty(contact.getZip1())) {
				address += "";
			} else {
				address += contact.getZip1();
			}
			values.put(ContactConstants.COLUMN_DATA1, address);
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.ADDRESS_TYPE_WORK);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getAddress2())
				|| !StringUtil.isEmpty(contact.getState2())
				|| !StringUtil.isEmpty(contact.getCity2())
				|| !StringUtil.isEmpty(contact.getZip2())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_ADDRESS);
			String address = contact.getAddress2() + "\n";
			if (StringUtil.isEmpty(contact.getCity2())) {
				address += "";
			} else {
				address += contact.getCity2();
			}
			address += ",";
			if (StringUtil.isEmpty(contact.getState2())) {
				address += "";
			} else {
				address += contact.getState2();
			}
			address += " ";
			if (StringUtil.isEmpty(contact.getZip2())) {
				address += "";
			} else {
				address += contact.getZip2();
			}
			values.put(ContactConstants.COLUMN_DATA1, address);
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.ADDRESS_TYPE_WORK);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getAddress3())
				|| !StringUtil.isEmpty(contact.getState3())
				|| !StringUtil.isEmpty(contact.getCity3())
				|| !StringUtil.isEmpty(contact.getZip3())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_ADDRESS);
			String address = contact.getAddress3() + "\n";
			if (StringUtil.isEmpty(contact.getCity3())) {
				address += "";
			} else {
				address += contact.getCity3();
			}
			address += ",";
			if (StringUtil.isEmpty(contact.getState3())) {
				address += "";
			} else {
				address += contact.getState3();
			}
			address += " ";
			if (StringUtil.isEmpty(contact.getZip3())) {
				address += "";
			} else {
				address += contact.getZip3();
			}
			values.put(ContactConstants.COLUMN_DATA1, address);
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.ADDRESS_TYPE_WORK);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getBuilding1())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_ADDRESS);
			values.put(ContactConstants.COLUMN_DATA1, contact.getBuilding1());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.ADDRESS_TYPE_OTHER);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getBuilding2())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_ADDRESS);
			values.put(ContactConstants.COLUMN_DATA1, contact.getBuilding2());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.ADDRESS_TYPE_OTHER);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}
		if (!StringUtil.isEmpty(contact.getBuilding3())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_ADDRESS);
			values.put(ContactConstants.COLUMN_DATA1, contact.getBuilding3());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.ADDRESS_TYPE_OTHER);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		// organizations
		if (!StringUtil.isEmpty(contact.getCompany())
				|| !StringUtil.isEmpty(contact.getRole())
				|| !StringUtil.isEmpty(contact.getAffiliation())) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_ORGANIZATION);
			values.put(ContactConstants.COLUMN_DATA1, contact.getCompany());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.ORGANIZATION_TYPE_COMPANY);
			values.put(ContactConstants.COLUMN_DATA4, contact.getAffiliation()
					+ " " + contact.getRole());
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		if (contact.getNote() != null) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_NOTE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getNote());
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		if (contact.getUrl() != null) {
			values.clear();
			values.put(ContactConstants.RAW_CONTACT_ID, id);
			values.put(ContactConstants.COLUMN_MIMETYPE,
					ContactConstants.MIMETYPE_WEBSITE);
			values.put(ContactConstants.COLUMN_DATA1, contact.getUrl());
			values.put(ContactConstants.COLUMN_DATA2,
					ContactConstants.WEBSITE_TYPE_OTHER);
			resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		}

		// if(contact.getClassification1()!=null){
		// values.put("classification1", contact.getClassification1());
		// }
		// if(contact.getClassification2()!=null){
		// values.put("classification2", contact.getClassification2());
		// }

		// if(contact.getMeetingDay()!=null){
		// values.put("meetingday", contact.getMeetingDay());
		// }

		// if(contact.getRegistrationDay()!=null){
		// values.put("registrationday", contact.getRegistrationDay());
		// }
		// if(contact.getReserve1()!=null){
		// values.put("reserve1", contact.getReserve1());
		// }
		// if(contact.getReserve2()!=null){
		// values.put("reserve2", contact.getReserve2());
		// }
		// if(contact.getReserve3()!=null){
		// values.put("reserve3", contact.getReserve3());
		// }
		// if(contact.getReserve4()!=null){
		// values.put("reserve4", contact.getReserve4());
		// }
		// if(contact.getReserve5()!=null){
		// values.put("reserve5", contact.getReserve5());
		// }
		// if(contact.getReserveImg1()!=null){
		// values.put("reserve_img1", contact.getReserveImg1());
		// }
		// if(contact.getReserveImg2()!=null){
		// values.put("reserve_img2", contact.getReserveImg2());
		// }

		if(contact.getUserImg()!=null){
			byte[] userImg = contact.getUserImg();
			if (null != userImg && 0 < userImg.length){
				values.clear();
				values.put(ContactConstants.RAW_CONTACT_ID, id);
				values.put(ContactConstants.COLUMN_MIMETYPE,
						ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
				values.put(ContactsContract.CommonDataKinds.Photo.PHOTO,
						userImg);
				resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
			}
		}
		/*
		// Contact Group
		Integer groupId = findGroup();
		if( groupId == null ) {
			groupId = createGroup();
		}
		values.clear();
		values.put(ContactConstants.RAW_CONTACT_ID, id);
		values.put(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID, id);
		values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId);
		values.put(
				ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
				ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
					
		resolver.insert(ContactConstants.DATA_CONTENT_URI, values);
		*/
	}
	
	/*
	// the code bellow is not used by this programe
	// find group
	public Integer findGroup() {
		Integer id = null;
		ContentResolver resolver = context.getContentResolver();
		ContentValues groupValues = new ContentValues();
		groupValues.put(ContactsContract.Groups.TITLE, "TantCard");
		Cursor cur = resolver.query(
				ContactsContract.Groups.CONTENT_URI,
				new String [] { ContactsContract.Groups.TITLE },
				ContactsContract.Groups.TITLE + "=?",
				new String[] { "TantCard" }, null);
		id = cur.getInt(cur.getColumnIndex(ContactsContract.Groups.TITLE));
		
		return id;
	}
	
	// create group doest not currently support by API directory
	// you have to post to google api to get this done
	public Integer createGroup() {
		Integer id = null;
		ContentResolver resolver = context.getContentResolver();
		ContentValues groupValues = new ContentValues();
		groupValues.put(ContactsContract.Groups.TITLE, "TantCard");
		Uri uri = resolver.insert(ContactsContract.Groups.CONTENT_URI, groupValues);
		id = new Integer((int) ContentUris.parseId(uri));
		
		return id;
	}
	*/
}
