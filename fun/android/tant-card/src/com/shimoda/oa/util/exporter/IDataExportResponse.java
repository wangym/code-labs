package com.shimoda.oa.util.exporter;


public interface IDataExportResponse {
	public void onExportDataComplete(int imported, int current);
	
	public void onExportDataFinish(int imported);
	
	public void onExportDataError(String errorMsg);
}
