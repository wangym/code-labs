/**
 * 
 */
package me.yumin.java.vmspot.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
		System.out.println("VmSpotListener.contextInitialized");
	}
}
