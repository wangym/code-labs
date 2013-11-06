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
	public static synchronized void method(String name) {

		for (int i = 0; i < 50; i++) {
			System.out.println(String.format("[method@%s]%s", name, i));
		}
	}

	/**
	 * 使用synchronized结构
	 */
	public void struct(String name) {

		synchronized (SynchronizedRunnable.class) {
			for (int i = 0; i < 50; i++) {
				System.out.println(String.format("[struct@%s]%s", name, i));
			}
		}
	}

	/** 
     *  
     */
	@Override
	public void run() {

		String name = Thread.currentThread().getName();
		method(name);
		struct(name);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(new SynchronizedRunnable());
		executor.execute(new SynchronizedRunnable());
		executor.shutdown();
	}
}
