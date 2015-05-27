package com.diaoyumi.android.etc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Options {
	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	
	public void clear(){
		list.clear();
	}
	
	/**
	 * @return the list
	 */
	public ArrayList<HashMap<String, Object>> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(ArrayList<HashMap<String, Object>> list) {
		this.list = list;
	}

	public void append(String name, Object value)
	{
		append(name, value, null, null,null,null);
	}

	public void append(String n1, Object v1, String n2, Object v2){
		append(n1, v1, n2, v2, null,null, null, null);
	}
	
	public void append(String n1, Object v1, String n2, Object v2, String n3, Object v3){
		append(n1,v1, n2, v2, n3, v3, null, null);
	}
	
	public void append(String n1, Object v1, String n2, Object v2, String n3, Object v3, String n4, Object v4){
		HashMap<String, Object> item = new HashMap<String, Object>();
		if (n1 != null && ! "".equals(n1) && v1 != null){
			item.put(n1, v1);
		}
		if (n2 != null && ! "".equals(n2) && v2 != null){
			item.put(n2, v2);
		}
		if (n3 != null && ! "".equals(n3) && v3 != null){
			item.put(n3, v3);
		}
		if (n4 != null && ! "".equals(n4) && v4 != null){
			item.put(n4, v4);
		}
		
		if (item.size() > 0) list.add(item);
	}
	
	
	public void delete(String name)
	{
		if (null == name) return;
		if (null == list) return;
		ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
		
		Iterator<HashMap<String, Object>> it = list.iterator();
		while(it.hasNext())
		{
			HashMap<String, Object> item = (HashMap<String, Object>) it.next();
			if (! item.get("name").equals(name) )
			{
				newList.add(item);
			}
		}		
		list = newList;
	}
	
	public Object get(String name)
	{
		if (null == name) return null;
		if (null == list) return null;
		Iterator<HashMap<String, Object>> it = list.iterator();
		while(it.hasNext())
		{
			HashMap<String, Object> item = (HashMap<String, Object>) it.next();
			if (item.get("name").equals(name))
			{
				return item.get("value");
			}
		}
		return null;
	}
	
	public String[] getNames()
	{
		if (null == list) return null;
		StringBuffer sb = new StringBuffer("");
		
		Iterator<HashMap<String, Object>> it = list.iterator();
		Boolean isFirst = true;
		while(it.hasNext())
		{
			HashMap<String, Object> item = (HashMap<String, Object>) it.next();
			if (isFirst){
				isFirst = false;
			}else{
				sb.append(",");
			}
			sb.append(item.get("name"));
		}
		
		return sb.toString().split(",");
	}
	
	public Object getValueByIndex(int index)
	{
		if (null == list) return null;
		if (index < 0 || index > list.size()) return null;
		HashMap<String, Object> item = list.get(index);
		if (null == item) return null;
		return item.get("value");
	}
	

}
