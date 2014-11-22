/**
 *
 */
package me.yumin.java.labs.test.concurrency;

/**
 * @author yumin
 * @since 2014-11-03 11:33
 */
public class LockTestClass {

    /**
     *
     */
    private static int i = 0; // 用于类锁计数
    private Object object = new Object(); // 私有锁

    /**
     * 无锁方法
     *
     * @param threadID
     * @param thread
     */
    public void noSynMethod(long threadID, ObjThread thread) {

        System.out.println("nosyn: class obj is " + thread + ", threadId is" + threadID);
    }

    /**
     * 对象锁方法1
     */
    public synchronized void synOnMethod() {

        System.out.println("synOnMethod begins" + ", time = " + System.currentTimeMillis() + "ms");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("synOnMethod ends");
    }

    /**
     * 对象锁方法2,采用synchronized(this)来加锁
     */
    public void synInMethod() {

        synchronized (this) {
            System.out.println("synInMethod begins" + ", time = " + System.currentTimeMillis() + "ms");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("synInMethod ends");
        }
    }

    /**
     * 对象锁方法3
     */
    public void synMethodWithObj() {

        synchronized (object) {
            System.out.println("synMethodWithObj begins" + ", time = " + System.currentTimeMillis() + "ms");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("synMethodWithObj ends");
        }
    }

    /**
     * 类锁
     */
    public static synchronized void increament() {

        System.out.println("class synchronized. i = " + i + ", time = " + System.currentTimeMillis() + "ms");
        i++;
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("class synchronized ends.");
    }
}

