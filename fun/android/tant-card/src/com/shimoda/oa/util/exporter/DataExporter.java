package com.shimoda.oa.util.exporter;

import java.util.List;

import android.content.Context;

import com.shimoda.oa.model.Contact;
import com.shimoda.oa.model.ContactListVO;
import com.shimoda.oa.service.ContactService;
import com.shimoda.oa.service.SystemContactService;
import com.shimoda.oa.util.StringUtil;

public class DataExporter {
	private int current = 0;
	private int total = 0;
	private int exported = 0;
	
	private IDataExportResponse responseInterface;
	
	private List<ContactListVO> contactList;
	
	private ContactService contactService;
	
	private SystemContactService systemContactService;
	
	public DataExporter(SystemContactService systemContactService,ContactService contactService,List<ContactListVO> contactList,IDataExportResponse response){
		this.systemContactService = systemContactService;
		this.contactService = contactService;
		this.contactList = contactList;
		this.total = 0;
		if (this.contactList != null && !this.contactList.isEmpty()) {
			for (ContactListVO vo : this.contactList) {
				if (vo.getIsSelected()) {
					this.total++;
				}
			}
		}		
		this.responseInterface = response;
	}
	
	public DataExporter(Context context){
		this.systemContactService = new SystemContactService(context);
		this.contactService = new ContactService(context);
	}
	
	public void processExport(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				int type = 0;
				String msg = "";
				try {
					//执行导入
					if(total>0){
						ContactListVO contact = findCurrent();
						if(exportContact(contact.getTantcardId())){
							//导出成功
							contact.setIsExported(true);
							exported++;
						}else{
							//导出失败
							type = 2;
							msg = "导出失败："+contact==null?"contact null":contact.getFullName();
						}
					}
					if(current+1>=total){
						//导入结束
						type = 1 ;
					}
					switch(type){
					case 0:
						//导入成功
						responseInterface.onExportDataComplete(exported,current+1);
						break;
					case 1:
						//导入结束
						responseInterface.onExportDataFinish(exported);
						break;
					case 2:
						//导入出错
						responseInterface.onExportDataError(msg);
						break;
					}
				} catch (Exception e) {
					responseInterface.onExportDataError(e.getMessage());
				}finally{
					current++;
				}
			}
			
		}.start();
	};
	
	private ContactListVO findCurrent(){
		if (this.contactList == null || this.contactList.isEmpty()) {
			return null;
		}
		
		int i = -1;
		for (ContactListVO vo : this.contactList) {
			if (vo.getIsSelected()) {
				i++;
				if(i==current){
					return vo;
				}
			}
		}
		
		return null;
	}
	
	public boolean exportContact(String tantcardId){
		if(StringUtil.isEmpty(tantcardId)){
			return false;
		}
		
		//获取联系人信息
		Contact contact = contactService.getContactByTantcardId(tantcardId);
		if(contact==null){
			return false;
		}
		
		//执行导入
		boolean result = false;
		try{
			if(contact.getIosPersonId()>0 && systemContactService.contactExists(contact.getIosPersonId())){
				result = systemContactService.updateContact(contact);
			}else{
				Integer id = systemContactService.insertContact(contact);
				if(id!=null && id>0){
					//更新联系人的IosPersonId字段
					Contact update =  new Contact();
					update.setIosPersonId(id);
					update.setTantcardId(contact.getTantcardId());
					contactService.updateContact(update);
				}
			}
			result = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean sysContactExists(String tantcardId){
		if(StringUtil.isEmpty(tantcardId)){
			return false;
		}
		
		//获取联系人信息
		Contact contact = contactService.getContactByTantcardId(tantcardId);
		if(contact==null){
			return false;
		}
		
		if(contact.getIosPersonId()>0 && systemContactService.contactExists(contact.getIosPersonId())){
			return true;
		}
		
		return false;
	}
}
