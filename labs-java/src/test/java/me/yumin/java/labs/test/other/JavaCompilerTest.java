/**
 *
 */
package me.yumin.java.labs.test.other;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * @author yumin
 * @since 2014-11-22 14:18
 */
public class JavaCompilerTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    }
}
