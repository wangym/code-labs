package com.dianoyumi.common;

public interface ILogger {

	public boolean isEnabledDebug();
	
	public void info(Object message);
	public void debug(Object message);
	public void error(Object message);
	
}
