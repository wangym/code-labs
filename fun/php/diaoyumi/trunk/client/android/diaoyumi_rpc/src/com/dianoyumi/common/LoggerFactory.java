package com.dianoyumi.common;


public class LoggerFactory {

	private static ILogger instance;
	
	@SuppressWarnings("unchecked")
	public static ILogger getLogger(Class logClass)
	{
		if (instance == null){
			instance = new DefaultLogger();
		}
		return instance;
	}
}
