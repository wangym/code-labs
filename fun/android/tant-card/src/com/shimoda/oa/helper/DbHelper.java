package com.shimoda.oa.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DbHelper extends SQLiteOpenHelper{
	public DbHelper(Context context) {
		super(context, "tantcard", null, 1);
	}
	
	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//do nothinig
	}
	
	private void createTable(SQLiteDatabase db){
		String sqlStrCache = "CREATE TABLE [user] ( [user_id] INTEGER PRIMARY KEY AUTOINCREMENT, [tantcard_id] TEXT NOT NULL, [ios_person_id] INTEGER NOT NULL DEFAULT '0', [classification1] TEXT, [classification2] TEXT, [last_name] TEXT, [first_name] TEXT, [kana_last_name] TEXT, [kana_first_name] TEXT, [company] TEXT, [company_kana] TEXT, [affiliation] TEXT, [role] TEXT, [url] TEXT, [tel_1] TEXT, [fax_1] TEXT, [zip_1] TEXT, [state_1] TEXT, [city_1] TEXT, [address_1] TEXT, [building_1] TEXT, [email_1] TEXT, [mobilephone_1] TEXT, [skype_1] TEXT, [tel_2] TEXT, [fax_2] TEXT, [zip_2] TEXT, [state_2] TEXT, [city_2] TEXT, [address_2] TEXT, [building_2] TEXT, [email_2] TEXT, [mobilephone_2] TEXT, [skype_2] TEXT, [tel_3] TEXT, [fax_3] TEXT, [zip_3] TEXT, [state_3] TEXT, [city_3] TEXT, [address_3] TEXT, [building_3] TEXT, [email_3] TEXT, [mobilephone_3] TEXT, [skype_3] TEXT, [meetingday] TEXT, [registrationday] TEXT, [note] TEXT, [user_img] BLOB, [reserve_img1] BLOB, [reserve_img2] BLOB, [reserve1] TEXT, [reserve2] TEXT, [reserve3] TEXT, [reserve4] TEXT, [reserve5] TEXT );";
		db.execSQL(sqlStrCache);
		
		sqlStrCache = "CREATE TABLE [calllog] ([id] INTEGER PRIMARY KEY, [tantcard_id] TEXT NOT NULL, [call_date] TEXT, [tel] TEXT);";
		db.execSQL(sqlStrCache);
		
		sqlStrCache = "CREATE TABLE [bookmark] ([id] INTEGER PRIMARY KEY, [tantcard_id] TEXT NOT NULL);";
		db.execSQL(sqlStrCache);
	}
}
