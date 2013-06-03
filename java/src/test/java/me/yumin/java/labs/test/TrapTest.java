/**
 * 
 */
package me.yumin.java.labs.test;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author yumin
 * 
 */
public class TrapTest {

	@Test
	public void test自增陷阱() {

		boolean result = false;
		int expected = 10;

		int count = 0;
		for (int i = 0; i < expected; i++) {
			count = count++;
		}
		System.out.println(count);

		result = (expected != count);
		Assert.assertTrue(result);
	}
}
