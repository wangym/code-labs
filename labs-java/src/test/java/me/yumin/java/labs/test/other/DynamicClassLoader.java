/**
 *
 */
package me.yumin.java.labs.test.other;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author yumin
 * @since 2014-11-22 14:38
 */
public class DynamicClassLoader extends URLClassLoader {

    /**
     * @param urls
     * @param parent
     */
    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> findClassByClassName(String className) throws ClassNotFoundException {
        return this.findClass(className);
    }

    /**
     * @param fullName
     * @param classData
     * @return
     */
    public Class<?> loadClass(String fullName, byte[] classData) {
        return this.defineClass(fullName, classData, 0, classData.length);
    }
}
