package com.shimoda.oa.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 字符串工具类
 * 
 * @author youcai.lu
 * 
 */
public class StringUtil {
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equalsIgnoreCase("")) {
			return true;
		}
		return false;
	}

	public static String subString(String src, int length) {
		int len = 0;
        char[] chars = src.toCharArray();
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while(len < length) {
            if(i >= chars.length) {
                return sb.toString();
            }
            sb.append(chars[i]);
            if(chars[i++] > 0xff) {
                len += 2;                
            }else{
                len++;
            }
        }        
        return sb.toString();
	}
	
	public static Date strToDate(String dateStr,String sourceFormat){
		try{
			DateFormat formatter = new SimpleDateFormat(sourceFormat);
			return formatter.parse(dateStr);
		}catch (Exception e) {
			//do nothing
			e.printStackTrace();
		}
		return null;
	}
	
	public static String formatDate(String dateStr,String sourceFormat, String format){
		try{
			DateFormat formatter = new SimpleDateFormat(sourceFormat);
			Date date = formatter.parse(dateStr);
			formatter = new SimpleDateFormat(format);
			return formatter.format(date);
		}catch (Exception e) {
			//do nothing
			e.printStackTrace();
		}
		return "";
	}
	
	public static String dateFormat(Date date, String format){
		try{
			DateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(date);
		}catch (Exception e) {
			//do nothing
			e.printStackTrace();
		}
		return "";
	}
}
