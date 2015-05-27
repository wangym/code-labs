/**
 * JS自定义标签 - 生成JS验证字符串
 * 结果: <script src=""></script>
 */
package me.yumin.ladder.jsp.javascript;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import me.yumin.ladder.etc.LadderConstant;
import me.yumin.ladder.etc.LadderProperties;
import me.yumin.ladder.etc.LadderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yumin
 * 
 */
public class ValidateTag extends JavaScriptTag {

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(ValidateTag.class);
	// 取JS验证用脚本
	private static final String template = LadderProperties.getJSString(LadderConstant.CFG_TEMPLATE_JS_VALIDATE);

	/**
	 * 表单名称,将对该表单应用验证器
	 */
	private String form;

	// ====================
	// methods
	// ====================

	@Override
	public void doTag() throws JspException, IOException {

		try {

			// 将对象转换为JS验证字串
			String JS = LadderUtil.generateValidateJS(template, getProperty());
			// 输出JS头部字符串至页面
			this.getJspContext().getOut().write(JS);

		} catch (Exception e) {

			LOG.error("=== ladder.ValidateTag.doTag ===", e);
		}
	}

	/**
	 * 
	 * @return
	 */
	private Map<String, String> getProperty() {

		String form = getForm();
		Map<String, String> property = new HashMap<String, String>();
		property.put("form", form);
		property.put("messages", LadderProperties.getMsgString(form));

		return property;
	}

	// ====================
	// getters and setters
	// ====================

	/**
	 * @return the form
	 */
	public String getForm() {
		return form;
	}

	/**
	 * @param form the form to set
	 */
	public void setForm(String form) {
		this.form = form;
	}
}
