package com.shimoda.oa.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shimoda.oa.helper.DbHelper;
import com.shimoda.oa.model.CallLogVO;
import com.shimoda.oa.util.StringUtil;

public class CallLogService {
	private DbHelper dbHelper;
	
	private SQLiteDatabase db;
	
	public CallLogService(Context context){
		this.dbHelper = new DbHelper(context);
	}
	
	public boolean deleteCallLogById(Integer id){
		if(!openWriteableDatabase()){
			return false;
		}
		
		long ret = -1;
		try{
			//删除数据
			ret = db.delete("calllog", "id=?", new String[]{String.valueOf(id)});
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	public boolean deleteCallLogByTantcardId(String tantcardId){
		if(StringUtil.isEmpty(tantcardId)){
			return true;
		}
		
		if(!openWriteableDatabase()){
			return false;
		}
		
		long ret = -1;
		try{
			//删除数据
			ret = db.delete("calllog", "tantcard_id=?", new String[]{tantcardId});
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	public boolean insertCallLog(String tantcardId,String tel,Date date){
		if(!openWriteableDatabase()){
			return false;
		}
		
		long ret = -1;
		try{
			//插入数据
			ContentValues values = new ContentValues();
			values.put("call_date", StringUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"));
			values.put("tel", tel);
			values.put("tantcard_id", tantcardId);
			
			ret = db.insert("calllog", null, values);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	/**
	 * 查询通话记录列表
	 * @return
	 */
	public List<CallLogVO> list(){
		if(!this.openReadableDatabase()){
			return null;
		}
		List<CallLogVO> result = null;
		String sql = "SELECT calllog.*,user.last_name,user.first_name FROM calllog,user WHERE calllog.tantcard_id=user.tantcard_id ORDER BY calllog.call_date DESC";
		try{
			Cursor cur = db.rawQuery(sql, null);
			if(cur!=null){
				result = new ArrayList<CallLogVO>();
				while (cur.moveToNext()){
					result.add(this.convertToCallLogVO(cur));
				}
				cur.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.closeDatabase();
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
	
	
	private CallLogVO convertToCallLogVO(Cursor cur){
		CallLogVO callLog = new CallLogVO();
		callLog.setId(cur.getInt(cur.getColumnIndex("id")));
		callLog.setCallDate(cur.getString(cur.getColumnIndex("call_date")));
		callLog.setTel(cur.getString(cur.getColumnIndex("tel")));
		callLog.setTantcardId(cur.getString(cur.getColumnIndex("tantcard_id")));
		callLog.setFirstName(cur.getString(cur.getColumnIndex("first_name")));
		callLog.setLastName(cur.getString(cur.getColumnIndex("last_name")));
		
		return callLog;
	}
}
