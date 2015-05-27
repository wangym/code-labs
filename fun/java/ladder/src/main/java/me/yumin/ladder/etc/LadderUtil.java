/**
 * 
 */
package me.yumin.ladder.etc;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yumin
 * 
 */
@SuppressWarnings("rawtypes")
public class LadderUtil {

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(LadderUtil.class);

	/**
	 * 按模板生成HTML字符串
	 * 
	 * @param template
	 * @param property
	 * @return
	 */
	public static String generateHTML(String template, Map<String, String> property) {

		String HTML = "";

		try {

			if (isNotNullAndEmpty(template) && isNotNullAndEmpty(property)) {
				Iterator iterator = property.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					if (!isNotNullAndEmpty(value)) {
						// 若某属性未设值则将对应的模板标识符置空
						template = template.replace(generateTemplateSymbol(key), "");
					} else if (template.contains(key)) {
						// 若某属性有设值则将对应的模板标识符替换
						template = template.replace(generateTemplateSymbol(key), generateHTMLProperty(key, value));
					}
				}
				// 去除可能存在的多余空格
				HTML = template.replaceAll("( )+", " ");
			}

		} catch (Exception e) {

			LOG.error("=== ladder.LadderUtil.generateHTML ===", e);
		}

		return HTML;
	}

	/**
	 * 按模板生成JS验证字串
	 * 
	 * @param template
	 * @param property
	 * @return
	 */
	public static String generateValidateJS(String template, Map<String, String> property) {

		String JS = "";

		try {

			if (isNotNullAndEmpty(template) && isNotNullAndEmpty(property)) {
				Iterator iterator = property.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					if (!isNotNullAndEmpty(value)) {
						// 若某属性未设值则将对应的模板标识符置空
						template = template.replace(generateTemplateSymbol(key), "");
					} else if (template.contains(key)) {
						// 若某属性有设值则将对应的模板标识符替换
						template = template.replace(generateTemplateSymbol(key), value);
					}
				}
				// 去除可能存在的多余空格
				JS = template.replaceAll("( )+", " ");
			}

		} catch (Exception e) {

			LOG.error("=== ladder.LadderUtil.generateValidateJS ===", e);
		}

		return JS;
	}

	/**
	 * 生成自定义模板的标识符(用于替换模板上标识符)
	 * 
	 * @param key
	 * @return
	 */
	public static String generateTemplateSymbol(String key) {
		return "{$" + key + "}";
	}

	/**
	 * 生成HTML单个属性形式(如type="text")
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String generateHTMLProperty(String key, String value) {
		return key + "=\"" + value + "\"";
	}

	/**
	 * 生成jQuery的equalTo属性(如#password)
	 * 
	 * @param equalTo
	 * @return
	 */
	public static String generateEqualTo(String equalTo) {
		return (LadderUtil.isNotNullAndEmpty(equalTo) ? "#" + equalTo : "");
	}

	/**
	 * 是否为非空String
	 * 
	 * @param str
	 * @return true非空|false为空
	 */
	public static boolean isNotNullAndEmpty(String str) {

		boolean result = false;

		if (null != str && 0 < str.length()) {
			result = true;
		}

		return result;
	}

	/**
	 * 是否为非空Map
	 * 
	 * @param map
	 * @return true非空|false为空
	 */
	public static boolean isNotNullAndEmpty(Map map) {

		boolean result = false;

		if (null != map && 0 < map.size()) {
			result = true;
		}

		return result;
	}
}
