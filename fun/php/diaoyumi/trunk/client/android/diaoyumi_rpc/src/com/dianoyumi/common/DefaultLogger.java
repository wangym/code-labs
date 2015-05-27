package com.dianoyumi.common;

public class DefaultLogger implements ILogger {

	@Override
	public void debug(Object message) {
		System.out.println(message);

	}

	@Override
	public void error(Object message) {
		System.out.println(message);

	}

	@Override
	public void info(Object message) {
		System.out.println(message);

	}

	@Override
	public boolean isEnabledDebug() {
		return true;
	}

}
