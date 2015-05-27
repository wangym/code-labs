package com.shimoda.oa.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shimoda.oa.helper.DbHelper;
import com.shimoda.oa.model.BookmarkVO;

public class BookmarkService {
	private DbHelper dbHelper;
	
	private SQLiteDatabase db;
	
	public BookmarkService(Context context){
		this.dbHelper = new DbHelper(context);
	}
	
	public boolean bookmarkExists(String tantcardId){
		if(!openWriteableDatabase()){
			return false;
		}
		
		boolean exist = false;
		try{
			//查询是否存在
			Cursor cur = db.query("bookmark", new String[]{"id"}, "tantcard_id=?", new String[]{tantcardId},null,null,null);
			if(cur!=null && cur.moveToNext()){
				exist = true;
			}
			if(cur!=null){
				cur.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return exist;
	}
	
	public boolean deleteBookmarkByTantcardId(String tantcardId){
		if(!openWriteableDatabase()){
			return false;
		}
		
		long ret = -1;
		try{
			//删除数据
			ret = db.delete("bookmark", "tantcard_id=?", new String[]{tantcardId});
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	public boolean insertBookmark(String tantcardId){
		if(!openWriteableDatabase()){
			return false;
		}
		
		long ret = -1;
		try{
			//查询是否存在
			boolean exist = false;
			Cursor cur = db.query("bookmark", new String[]{"id"}, "tantcard_id=?", new String[]{tantcardId},null,null,null);
			if(cur!=null && cur.moveToNext()){
				exist = true;
			}
			if(cur!=null){
				cur.close();
			}
			//插入数据
			if(!exist){
				ContentValues values = new ContentValues();
				values.put("tantcard_id", tantcardId);
				ret = db.insert("bookmark", null, values);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDatabase();
		}
		
		return ret>=0;
	}
	
	/**
	 * 查询bookmark列表
	 * @return
	 */
	public List<BookmarkVO> list(){
		if(!this.openReadableDatabase()){
			return null;
		}
		List<BookmarkVO> result = null;
		String sql = "SELECT bookmark.*,user.last_name,user.first_name,user.company FROM bookmark, user WHERE bookmark.tantcard_id=user.tantcard_id ORDER BY bookmark.id DESC";
		try{
			Cursor cur = db.rawQuery(sql, null);
			if(cur!=null){
				result = new ArrayList<BookmarkVO>();
				while (cur.moveToNext()){
					result.add(this.convertToBookmarkVO(cur));
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
	
	
	private BookmarkVO convertToBookmarkVO(Cursor cur){
		BookmarkVO bookmark = new BookmarkVO();
		bookmark.setId(cur.getInt(cur.getColumnIndex("id")));
		bookmark.setTantcardId(cur.getString(cur.getColumnIndex("tantcard_id")));
		bookmark.setFirstName(cur.getString(cur.getColumnIndex("first_name")));
		bookmark.setLastName(cur.getString(cur.getColumnIndex("last_name")));
		bookmark.setCompany(cur.getString(cur.getColumnIndex("company")));
		
		return bookmark;
	}
}
