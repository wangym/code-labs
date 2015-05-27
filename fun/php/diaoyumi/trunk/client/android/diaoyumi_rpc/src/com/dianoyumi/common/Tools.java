package com.dianoyumi.common;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Tools {
	
	private static final double[] LNGS = {111319.5,111302.5,111251.7,111166.9,111048.3,110895.9,110709.7,110489.7,110236.1,109948.9,109628.3,109274.2,108886.8,108466.3,108012.7,107526.3,107007.1,106455.2,105871.0,105254.5,104605.9,103925.5,103213.5,102469.9,101695.2,100889.5,100053.1,99186.1,98289.0,97361.9,96405.2,95419.1,94403.9,93360.0,92287.7,91187.2,90059.0,88903.3,87720.5,86511.1,85275.2,84013.4,82726.0,81413.4,80076.0,78714.3,77328.5,75919.2,74486.8,73031.6,71554.3,70055.1,68534.6,66993.2,65431.4,63849.7,62248.5,60628.4,58989.8,57333.2,55659.2,53968.2,52260.8,50537.5,48798.8,47045.2,45277.2,43495.5,41700.6,39892.9,38073.1,36241.7,34399.2,32546.3,30683.5,28811.3,26930.3,25041.1,23144.3,21240.5,19330.2,17414.0,15492.5,13566.3,11635.9,9702.0,7765.2,5825.9,3884.9,1942.8,0.0};
	private static final double LAT_DISTANCE = 111319.5;
	public static final int MAX_LNG_INDEX = 0;
	public static final int MIN_LNG_INDEX = 1;
	public static final int MAX_LAT_INDEX = 2;
	public static final int MIN_LAT_INDEX = 3;
	
	public static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static final String RID_TYPE_PICTURE = "p";
	public static final String RID_TYPE_FISHING = "f";
	public static final String RID_TYPE_CATCH = "c";
	public static final String RID_TYPE_MIND = "m";
	
	
	public static String md5(String s){
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// System.out.println((int)b);
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean equals(String str1, String str2){
		if (str1 != null){
			return str1.equals(str2);
		}
		return false;
	}
	
	public static long time(){
		return new java.util.Date().getTime() / 1000;
	}
	
	public static boolean isEmpty(String str){
		if (str != null && ! "".equals(str.trim())) return false;
		return true;
	}
	
	
	public static String date2string(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		return sdf.format(date);	
	}
	
	public static Date string2date(String date){
		return string2Date(date, DEFAULT_DATE_PATTERN);
	}
	
	public static Date string2Date(String str, String format){
		if (isEmpty(str)) return null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return	sdf.parse(str);
		} catch (ParseException e) {
		}
		return null;
	}
	
	public static long date2Timestamp(Date date){
		if (date == null) return 0;
		return (long)date.getTime() / 1000;
	}
	
	public static Date timestamp2Date(long timestamp){
		return new Date(timestamp * 1000);
	}
	
	public static long string2Long(String str){
		return string2Long(str, 0);
	}
	
	public static long string2Long(String str, long def){
		if (isEmpty(str)) return def;
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return def;
		}
	}
	
	public static int string2LongInt(String str){
		return string2Int(str, 0);
	}
	
	public static int string2Int(String str, int def){
		if (isEmpty(str)) return def;
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return def;
		}
	}	
		
	
	//获得某个地点周围指定半径的范围
	public static double[] getPlaceRange(double lng, double lat, int radiusMeter){
		double[] ret = new double[4];
		
		double latDifference = radiusMeter / LAT_DISTANCE;
		//在指定的纬度上每个经度间隔距离
		double lngDistance = LNGS[(int) Math.round(lat)];
		double lngDifference = radiusMeter /lngDistance;
		ret[MAX_LNG_INDEX] = lng + lngDifference;
		ret[MIN_LNG_INDEX] = lng - lngDifference;
		ret[MAX_LAT_INDEX] = lat + latDifference;
		ret[MIN_LAT_INDEX] = lat - latDifference;
		return ret;
	}
	
	//计算两个坐标距离 单位：米
	//@return －1 失败
	public static long getDistance(double lng1, double lat1, double lng2, double lat2){
		//两点纵向（纬度）间隔距离 单位:米
		long latDifference = Math.abs(Math.round(((lat2 - lat1) * LAT_DISTANCE)));
		//在指定的纬度上每个经度间隔距离
		double lngDistance = LNGS[(int) Math.round(lat1)];
		long lngDifference = Math.abs(Math.round((lng2 - lng1) * lngDistance));
		return Math.round(Math.sqrt(latDifference * latDifference + lngDifference * lngDifference));
	}
	
	public static String jsonClean(String json){
		if (! isEmpty(json)){
			int pos = json.indexOf('{');
			if (pos > -1 )	return json.substring(pos);
		}
		return "{}";
	}
	
	public static String uuid(){
		UUID uuid  =  UUID.randomUUID(); 
		String s = UUID.randomUUID().toString(); 
		return md5(s);
	}
	
	public static String generateRID(String type){
		return type + "-" + uuid();
	}
	
	public static String getFileExtension(String filePath){
		if (! isEmpty(filePath)){
			int pos = filePath.lastIndexOf('.');
			if (pos > -1) return filePath.substring(pos + 1);
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(md5("abcd"));
		System.out.println(uuid());
		System.out.println(generateRID(RID_TYPE_FISHING));
		
		System.out.println("aaa".indexOf("c"));
		System.out.println(getFileExtension("./test.png"));
		
		System.out.println(Math.abs(-3003.33));
		System.out.println(Math.round(-333.53));
		
		System.out.println(Tools.getDistance(116.27917, 39.50389, 113.55472, 37.99611));
		System.out.println(Tools.getDistance(116, 39, 113, 37));
	}
}
