/**
 *
 */
package me.yumin.java.labs.test.concurrency.lock;

/**
 * @author yumin
 * @since 2014-11-03 11:33
 */
public class ObjectThread extends Thread {

    /**
     *
     */
    LockTestClass lock;
    int i = 0;

    /**
     * @param lock
     * @param i
     */
    public ObjectThread(LockTestClass lock, int i) {

        this.lock = lock;
        this.i = i;
    }

    /**
     *
     */
    public void run() {

        //无锁方法
        lock.noSynMethod(this.getId(),this);
        //对象锁方法1，采用synchronized synInMethod的方式
        //lock.synInMethod();
        //对象锁方法2，采用synchronized(this)的方式
        //lock.synOnMethod();
        //私有锁方法，采用synchronized(object)的方式
        //lock.synMethodWithObj();
        //类锁方法，采用static synchronized increment的方式
        //LockTestClass.increament();
    }
}

