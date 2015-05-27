/**
 * JSP自定义标签 - 生成HTML密码输入框
 * 结果: <input type="password" ... />
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
public class PasswordTag extends HTMLTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1990336351156033093L;

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(PasswordTag.class);
	// 取HTML密码模板
	private static final String template = LadderProperties.getCoreString(LadderConstant.CFG_TEMPLATE_HTML_PASSWORD);

	/**
	 * 
	 */
	private String minlength;
	private String maxlength;
	private String equalTo;

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

			LOG.error("=== ladder.PasswordTag.doStartTag ===", e);
		}

		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 用于计算密码框属性的参数集
	 * 
	 * @return
	 */
	private Map<String, String> getProperty() {

		Map<String, String> property = new HashMap<String, String>();
		property.put("name", getName());
		property.put("id", getId());
		property.put("value", getValue());
		// JS验证字段时用参数集合
		property.put("class", (getRequired() ? "required" : ""));
		property.put("minlength", getMinlength());
		property.put("maxlength", getMaxlength());
		property.put("equalTo", LadderUtil.generateEqualTo(getEqualTo()));

		return property;
	}

	// ====================
	// getters and setters
	// ====================

	/**
	 * @return the minlength
	 */
	public String getMinlength() {
		return minlength;
	}

	/**
	 * @param minlength the minlength to set
	 */
	public void setMinlength(String minlength) {
		this.minlength = minlength;
	}

	/**
	 * @return the maxlength
	 */
	public String getMaxlength() {
		return maxlength;
	}

	/**
	 * @param maxlength the maxlength to set
	 */
	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	/**
	 * @return the equalTo
	 */
	public String getEqualTo() {
		return equalTo;
	}

	/**
	 * @param equalTo the equalTo to set
	 */
	public void setEqualTo(String equalTo) {
		this.equalTo = equalTo;
	}
}
