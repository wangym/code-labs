/**
 * JSP自定义标签 - 生成HTML提交按钮
 * 结果: <input type="submit" ... />
 */
package me.yumin.ladder.jsp.html;

import java.util.HashMap;
import java.util.Map;
import me.yumin.ladder.etc.LadderConstant;
import me.yumin.ladder.etc.LadderProperties;
import me.yumin.ladder.etc.LadderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yumin
 * 
 */
public class SubmitTag extends HTMLTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7478082296783529351L;

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(SubmitTag.class);
	// 取HTML提交按钮
	private static final String template = LadderProperties.getCoreString(LadderConstant.CFG_TEMPLATE_HTML_SUBMIT);

	/**
	 * 
	 */

	// ====================
	// methods
	// ====================

	@Override
	public int doStartTag() {

		try {

			// 将对象转换为HTML字符串
			String HTML = LadderUtil.generateHTML(template, getProperty());
			// 输出HTML字符串至页面上
			this.pageContext.getOut().write(HTML);

		} catch (Exception e) {

			LOG.error("=== ladder.SubmitTag.doStartTag ===", e);
		}

		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 用于计算提交按钮的参数集
	 * 
	 * @return
	 */
	private Map<String, String> getProperty() {

		Map<String, String> property = new HashMap<String, String>();
		property.put("name", getName());
		property.put("id", getId());
		property.put("value", getValue());

		return property;
	}

	// ====================
	// getters and setters
	// ====================
}
