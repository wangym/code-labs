/**
 * 
 */
package me.yumin.ladder.etc;

import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yumin
 * 
 */
public class LadderProperties {

	/**
	 * 
	 */
	// LOG
	private static final Log LOG = LogFactory.getLog(LadderProperties.class);

	/**
	 * 
	 */
	private static final ResourceBundle core = ResourceBundle.getBundle("ladder-core");
	private static final ResourceBundle js = ResourceBundle.getBundle("ladder-js");
	private static final ResourceBundle msg = ResourceBundle.getBundle("ladder-msg");

	/**
	 * 
	 */
	private LadderProperties() {
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getCoreString(String key) {

		String value = "";

		try {
			value = core.getString(key);
		} catch (Exception e) {
			LOG.error("=== ladder.LadderProperties.getCoreString ===", e);
		}

		return value;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getJSString(String key) {

		String value = "";

		try {
			value = js.getString(key);
		} catch (Exception e) {
			LOG.error("=== ladder.LadderProperties.getJSString ===", e);
		}

		return value;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getMsgString(String key) {

		String value = "";

		try {
			value = msg.getString(key);
		} catch (Exception e) {
			LOG.error("=== ladder.LadderProperties.getMsgString ===", e);
		}

		return value;
	}
}
