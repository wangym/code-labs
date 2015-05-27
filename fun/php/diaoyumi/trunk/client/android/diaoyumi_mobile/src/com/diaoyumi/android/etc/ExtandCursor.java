package com.diaoyumi.android.etc;

import java.util.Date;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ExtandCursor extends CursorWrapper {

	public ExtandCursor(Cursor cursor) {
		super(cursor);
	}
	
	public int getIntByName(String fieldName){
		return getInt(getColumnIndex(fieldName));
	}
	
	public double getDoubleByName(String fieldName){
		return getDouble(getColumnIndex(fieldName));
	}
	
	public String getStringByName(String fieldName){
		return getString(getColumnIndex(fieldName));
	}
	
	public Date getDateByName(String fieldName){
		String str = getStringByName(fieldName);
		if (Util.isNotEmpty(str)){
			return Util.string2date(str);
		}
		return null;
	}

}
