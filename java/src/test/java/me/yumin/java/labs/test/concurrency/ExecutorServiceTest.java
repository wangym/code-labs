/**
 *
 */
package me.yumin.java.labs.test.concurrency;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author yumin
 * @since 2014-11-30 17:03
 */
public class ExecutorServiceTest {

    public String getThreadName() {

        String threadName = null;

        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("My-%d").setDaemon(true).build();
        final ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory);

        return threadName;
    }
}
