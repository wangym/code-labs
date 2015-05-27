/**
 * JSP自定义标签 - 生成HTML表单
 * 结果: <form ...>*</form>
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
public class FormTag extends HTMLTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6955909952325518672L;

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(FormTag.class);
	// 取HTML表单模板
	private static final String formTpl = LadderProperties.getCoreString(LadderConstant.CFG_TEMPLATE_HTML_FORM);
	// 取JS验证用脚本
	private static final String validateTpl = LadderProperties.getJSString(LadderConstant.CFG_TEMPLATE_JS_VALIDATE);

	/**
	 * 
	 */
	private boolean validate;
	private String method;
	private String action;
	private String target;

	// ====================
	// methods
	// ====================

	@Override
	public int doStartTag() {

		try {

			// 将对象转换为HTML字符串
			String HTML = LadderUtil.generateHTML(formTpl, getHTMLProperty());
			// 输出HTML字符串至页面上
			this.pageContext.getOut().write(HTML);

			// 是否需输出验证用JS代码
			if (getValidate()) {
				// 将对象转换为JS验证字串
				String JS = LadderUtil.generateValidateJS(validateTpl, getJSProperty());
				// 输出JS头部字符串至页面
				this.pageContext.getOut().write(JS);
			}

		} catch (Exception e) {

			LOG.error("=== ladder.FormTag.doStartTag ===", e);
		}

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() {

		try {

			this.pageContext.getOut().write("</form>");

		} catch (Exception e) {

			LOG.error("=== ladder.FormTag.doEndTag ===", e);
		}

		return EVAL_PAGE;
	}

	/**
	 * 用于计算HTML表单的参数集
	 * 
	 * @return
	 */
	private Map<String, String> getHTMLProperty() {

		Map<String, String> property = new HashMap<String, String>();
		property.put("name", getName());
		property.put("id", getId());
		property.put("method", getMethod());
		property.put("action", getAction());
		property.put("target", getTarget());

		return property;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private Map<String, String> getJSProperty() {

		String id = getId();
		Map<String, String> property = new HashMap<String, String>();
		property.put("form", "\"#" + id + "\"");
		property.put("messages", LadderProperties.getMsgString(id));

		return property;
	}

	// ====================
	// getters and setters
	// ====================

	/**
	 * @return the validate
	 */
	public boolean getValidate() {
		return validate;
	}

	/**
	 * @param validate the validate to set
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
}
