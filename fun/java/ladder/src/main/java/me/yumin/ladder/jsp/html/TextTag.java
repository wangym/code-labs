/**
 * JSP自定义标签 - 生成HTML文本输入框
 * 结果: <input type="text" ... />
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
public class TextTag extends HTMLTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3416132587512657745L;

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(TextTag.class);
	// 取HTML文本模板
	private static final String template = LadderProperties.getCoreString(LadderConstant.CFG_TEMPLATE_HTML_TEXT);

	/**
	 * 
	 */
	private String minlength;
	private String maxlength;
	private String datatype;
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

			LOG.error("=== ladder.TextTag.doStartTag ===", e);
		}

		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 用于计算输入框属性的参数集
	 * 
	 * @return
	 */
	private Map<String, String> getProperty() {

		Map<String, String> property = new HashMap<String, String>();
		property.put("name", getName());
		property.put("id", getId());
		property.put("value", getValue());
		// JS验证字段时用参数集合
		String required = (getRequired() ? "required" : "");
		String datatype = (LadderUtil.isNotNullAndEmpty(getDatatype()) ? " " + getDatatype() : "");
		property.put("class", (required + datatype).trim());
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
	 * @return the datatype
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
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
