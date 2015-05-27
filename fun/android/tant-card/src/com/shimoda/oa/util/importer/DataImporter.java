package com.shimoda.oa.util.importer;

import java.util.List;

import com.shimoda.oa.model.Contact;
import com.shimoda.oa.model.SourceContactListVO;
import com.shimoda.oa.service.ContactService;
import com.shimoda.oa.service.SourceContactService;

public class DataImporter {
	private int current = 0;
	private int total = 0;
	private int imported = 0;
	
	private IDataImportResponse responseInterface;
	
	private List<SourceContactListVO> contactList;
	
	private SourceContactService sourceContactService;
	
	private ContactService contactService;
	
	public DataImporter(ContactService contactService,SourceContactService sourceContactService,List<SourceContactListVO> contactList,IDataImportResponse response){
		this.sourceContactService = sourceContactService;
		this.contactList = contactList;
		if(this.contactList!=null && !this.contactList.isEmpty()){
			this.total = this.contactList.size();
		}else{
			this.total = 0;
		}
		
		this.responseInterface = response;
		
		this.contactService = contactService;
	}
	
	public void processImport(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				int type = 0;
				String msg = "";
				try {
					//执行导入
					if(total>0){
						SourceContactListVO currentContact = contactList.get(current);
						if(!importContact(currentContact)){
							type = 2;
							//TODO 放到string并且优化成stringbuffer
							msg = "导入失败："+currentContact==null?"contact null":currentContact.getLastName()+" "+currentContact.getFirstName();
						}else{
							currentContact.setIsImported(true);
							imported++;
						}
					}
					if(current+1>=total){
						//导入结束
						type =1 ;
					}
					switch(type){
					case 0:
						//导入成功
						responseInterface.onImportDataComplete(contactList,imported,current+1);
						break;
					case 1:
						//导入结束
						responseInterface.onImportDataFinish(imported);
						break;
					case 2:
						//导入出错
						responseInterface.onImportDataError(msg);
						break;
					}
				} catch (Exception e) {
					responseInterface.onImportDataError(e.getMessage());
				}finally{
					current++;
				}
			}
			
		}.start();
	};
	
	public void incrementCurrent(){
		current++;
	}
	
	private boolean importContact(SourceContactListVO sourceContact){
		if(sourceContact==null){
			return false;
		}
		
		//获取联系人信息
		Contact contact = sourceContactService.getConatctByUserId(sourceContact.getUserId());
		if(contact==null){
			return false;
		}
		
		//执行导入
		boolean result = false;
		try{
			if(contactService.contactExists(contact.getTantcardId())){
				result = contactService.updateContact(contact);
			}else{
				result = contactService.insertContact(contact);
			}
			if(result){
				//删除db中的记录
				sourceContactService.delContactByUserId(contact.getUserId());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
