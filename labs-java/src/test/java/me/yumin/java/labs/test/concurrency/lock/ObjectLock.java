/**
 *
 */
package me.yumin.java.labs.test.concurrency.lock;

/**
 * @author yumin
 * @since 2014-11-03 11:04
 */
public class ObjectLock {

    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("start time = " + System.currentTimeMillis() + "ms");
        LockTestClass test = new LockTestClass();
        for (int i = 0; i < 3; i++) {
            Thread thread = new ObjectThread(test, i);
            thread.start();
        }
    }
}

