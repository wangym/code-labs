/**
 * 
 */
package me.yumin.java.vmspot;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import me.yumin.java.common.tool.Profiler;

/**
 * @author yumin
 * 
 */
public class VmSpotListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("VmSpotListener.contextDestroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

		System.out.println(Profiler.dump("===== Detail: ", "        "));
		System.out.println("VmSpotListener.contextInitialized");
	}
}
