/**
 * 
 */
package me.yumin.ladder.test;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import me.yumin.ladder.etc.LadderConstant;
import me.yumin.ladder.etc.LadderProperties;
import me.yumin.ladder.etc.LadderUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author yumin
 * 
 */
public class LadderUtilTest {

	/**
	 * 
	 */
	// HTML文本模板
	private static final String textTpl = LadderProperties.getCoreString(LadderConstant.CFG_TEMPLATE_HTML_TEXT);
	// 取JS验让脚本
	private static final String validateTpl = LadderProperties.getJSString(LadderConstant.CFG_TEMPLATE_JS_VALIDATE);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public void testGenerateHTML() {

		boolean result = false;

		Map<String, String> property = new HashMap<String, String>();
		property.put("name", "username");
		property.put("id", "");
		property.put("equalTo", "username");

		String html = LadderUtil.generateHTML(textTpl, property);
		System.out.println("===== testGenerateHTML: " + html);
		if (null != html && 0 < html.length()) {
			result = true;
		}

		assertTrue(result);
	}

	/**
	 * 
	 */
	@Test
	public void testGenerateValidateJS() {

		boolean result = false;

		Map<String, String> property = new HashMap<String, String>();
		property.put("form", "#myform");
		property.put("messages", "");

		String validateJS = LadderUtil.generateValidateJS(validateTpl, property);
		System.out.println("===== testGenerateValidateJS: " + validateJS);
		if (null != validateJS && 0 < validateJS.length()) {
			result = true;
		}

		assertTrue(result);
	}
}
