/**
 * 
 */
package me.yumin.java.labs.test.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yumin
 * 
 */
public class SynchronizedRunnable implements Runnable {

	/**
	 * 使用synchronized方法
	 */
	public static synchronized void testSyncMethod(String name) {

		long now = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			System.out.println(String.format("[%s-%s]%s[testSyncMethod]", name, now, i));
		}
	}

	/**
	 * 使用synchronized代码块
	 */
	public void testSyncBlock(String name) {

		long now = System.currentTimeMillis();
		synchronized (SynchronizedRunnable.class) {
			for (int i = 0; i < 100; i++) {
				System.out.println(String.format("[%s-%s]%s[testSyncBlock]", name, now, i));
			}
		}
	}

	/** 
     *  
     */
	@Override
	public void run() {

		String name = Thread.currentThread().getName();
		// testSyncMethod(name);
		testSyncBlock(name);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.execute(new SynchronizedRunnable());
		executorService.execute(new SynchronizedRunnable());
		executorService.shutdown();
	}
}
