/**
 * 
 */
package com.shimoda.oa.util;

import android.net.Uri;


/**
 * 联系人常量定义
 * 
 */
public class ContactConstants {
	public static final Uri PEOPLE_CONTENT_URI = Uri.parse("content://com.android.contacts/raw_contacts");
	
	public static final Uri DATA_CONTENT_URI = Uri.parse("content://com.android.contacts/data");
	
	public static final String PEOPLE_ID = "_id";
	
	public static final String RAW_CONTACT_ID = "raw_contact_id";
	
	public static final String COLUMN_MIMETYPE = "mimetype";
	
	public static final String COLUMN_DATA1 = "data1";
	
	public static final String COLUMN_DATA2 = "data2";
	
	public static final String COLUMN_DATA3 = "data3";
	
	public static final String COLUMN_DATA4 = "data4";
	
	public static final String COLUMN_DATA5 = "data5";
	
	public static final String PHOTO = "data15";
	
	public static final String MIMETYPE_EMAIL = "vnd.android.cursor.item/email_v2";
	
	public static final String MIMETYPE_IM = "vnd.android.cursor.item/im";
	
	public static final String MIMETYPE_ADDRESS = "vnd.android.cursor.item/postal-address_v2";
	
	public static final String MIMETYPE_PHONE = "vnd.android.cursor.item/phone_v2";
	
	public static final String MIMETYPE_NAME = "vnd.android.cursor.item/name";
	
	public static final String MIMETYPE_ORGANIZATION = "vnd.android.cursor.item/organization";
	
	public static final String MIMETYPE_NOTE = "vnd.android.cursor.item/note";
	
	public static final String MIMETYPE_WEBSITE = "vnd.android.cursor.item/website";
	
	public static final String MIMETYPE_GROUP = "vnd.android.cursor.dir/contactsgroup";
	
	public static final int IM_TYPE_SKYPE = 3;
	
	public static final int PHONE_TYPE_TEL = 3;
	
	public static final int PHONE_TYPE_MOBILE = 2;
	
	public static final int PHONE_TYPE_FAX = 4;
	
	public static final int EMAIL_TYPE_WORK = 2;
	
	public static final int ADDRESS_TYPE_WORK = 2;
	
	public static final int ADDRESS_TYPE_OTHER = 3;
	
	public static final int ORGANIZATION_TYPE_COMPANY = 1;
	
	public static final int ORGANIZATION_TYPE_CUSTOM = 0;
	
	public static final int WEBSITE_TYPE_OTHER = 7;
	
	public static final int DATA5_SKYPE = 3;
}
