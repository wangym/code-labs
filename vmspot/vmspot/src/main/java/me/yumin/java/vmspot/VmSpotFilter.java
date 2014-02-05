/**
 * 
 */
package me.yumin.java.vmspot;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.yumin.java.common.servlet.AbstractFilter;
import me.yumin.java.common.tool.Profiler;

/**
 * @author yumin
 * 
 */
public class VmSpotFilter extends AbstractFilter {

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		Profiler.start();

		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Profiler.release();
			System.out.println(String.format("[Profiler]dump=%s", Profiler.dump()));
			Profiler.reset();
		}
	}
}
