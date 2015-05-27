package com.diaoyumi.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "diaoyumi.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String SQL_CONF_CREATE = "" +
			"CREATE TABLE \"conf\" ("+
					"\"key\" VARCHAR(20) PRIMARY KEY  NOT NULL ," + 
					"\"value\" TEXT NOT NULL"+
			")";
	
	private static final String SQL_EVENT_CREATE = "" +
			"CREATE TABLE \"event\" ("+
					"\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
					"\"user_id\" INTEGER NOT NULL,"+ 
					"\"rid\" VARCHAR(50) NOT NULL  UNIQUE ,"+ 
					"\"type\" CHAR(1) NOT NULL , "+
					"\"event_time\" DATETIME NOT NULL  DEFAULT \"1971-01-01 00:00:00\","+ 
					"\"lat\" REAL,"+ 
					"\"lng\" REAL, "+
					"\"place\" VARCHAR(100),"+ 
					"\"is_new_place\" CHAR(1) NOT NULL  DEFAULT \"N\","+ 
					"\"companion\" VARCHAR(100), "+
					"\"picture\" VARCHAR(100), "+
					"\"title\" VARCHAR(100), "+
					"\"price\" DOUBLE(8,2), "+
					"\"intro\" TEXT, "+
					"\"properties\" TEXT,"+ 
					"\"status\" INTEGER NOT NULL  DEFAULT 0,"+ 
					"\"created\" DATETIME NOT NULL  DEFAULT \"1970-01-01 00:00:00\","+ 
					"\"modified\" DATETIME NOT NULL  DEFAULT \"1970-01-01 00:00:00\" "+
			")";		
	
	private static final String SQL_EVENT_LOCATION = "" +
			"CREATE TABLE \"event_location\" (" +
				"\"id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
				"\"user_id\" INTEGER NOT NULL,"+
				"\"lat\" REAL,"+
				"\"lng\" REAL,"+
				"\"place\" VARCHAR(100),"+
				"\"visits\" INTEGER NOT NULL DEFAULT 0,"+
				"\"created\" DATETIME NOT NULL  DEFAULT \"1970-01-01 00:00:00\","+ 
				"\"modified\" DATETIME NOT NULL  DEFAULT \"1970-01-01 00:00:00\" "+
		")";		
				
	
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CONF_CREATE);
		db.execSQL(SQL_EVENT_CREATE);
		db.execSQL(SQL_EVENT_LOCATION);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
