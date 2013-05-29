/**
 * Java陷阱解惑
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

		int expected = 10;

		int count = 0;
		for (int i = 0; i < expected; i++) {
			count = count++;
		}
		System.out.println(count);

		Assert.assertTrue(expected != count);
	}
}
