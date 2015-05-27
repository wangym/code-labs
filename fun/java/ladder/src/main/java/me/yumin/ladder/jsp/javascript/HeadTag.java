/**
 * JS自定义标签 - 生成JS头部字符串
 * 结果: <script src=""></script>
 */
package me.yumin.ladder.jsp.javascript;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import me.yumin.ladder.etc.LadderConstant;
import me.yumin.ladder.etc.LadderProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yumin
 * 
 */
public class HeadTag extends JavaScriptTag {

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(HeadTag.class);
	// 取JS头部脚本
	private static final String template = LadderProperties.getJSString(LadderConstant.CFG_TEMPLATE_JS_HEAD);

	// ====================
	// methods
	// ====================

	@Override
	public void doTag() throws JspException, IOException {

		try {

			// 输出JS头部字符串至页面
			this.getJspContext().getOut().write(template);

		} catch (Exception e) {

			LOG.error("=== ladder.HeadTag.doTag ===", e);
		}
	}

	// ====================
	// getters and setters
	// ====================
}
