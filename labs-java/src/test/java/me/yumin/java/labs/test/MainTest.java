/**
 * 
 */
package me.yumin.java.labs.test;

import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yumin
 * 
 */
public class MainTest {

	@Test
	public void test() {

		long time = new Date().getTime();
		System.out.println(time);

		Assert.assertTrue(true);
	}
}
