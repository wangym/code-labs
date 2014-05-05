/**
 * 
 */
package me.yumin.java.vmspot;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

/**
 * @author yumin
 *
 */
public class RenderPathParser implements LogChute {

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#init(org.apache.velocity.runtime.RuntimeServices)
	 */
	@Override
	public void init(RuntimeServices rs) throws Exception {
		System.out.println(String.format("[VmSpotLogger]level=%s&message=%s", rs.get));
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#log(int, java.lang.String)
	 */
	@Override
	public void log(int level, String message) {
		System.out.println(String.format("[VmSpotLogger]level=%s&message=%s", level, message));
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#log(int, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(int level, String message, Throwable t) {
		System.out.println(String.format("[VmSpotLogger]level=%s&message=%s&t=", level, message, t));
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#isLevelEnabled(int)
	 */
	@Override
	public boolean isLevelEnabled(int level) {
		System.out.println(String.format("[VmSpotLogger]level=%s", level));
		return false;
	}
}
