package com.shimoda.oa.util.importer;

import java.util.List;

import com.shimoda.oa.model.SourceContactListVO;


public interface IDataImportResponse {
	public void onImportDataComplete(List<SourceContactListVO> contactList, int imported, int current);
	
	public void onImportDataFinish(int imported);
	
	public void onImportDataError(String errorMsg);
}
