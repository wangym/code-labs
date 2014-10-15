/**
 *
 */
package me.yumin.java.labs.test.concurrency;

import org.junit.Assert;
import org.junit.Test;
import java.util.concurrent.*;

/**
 * @author yumin
 */
public class FutureTaskTest {

    @Test
    public void test() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Long> futureTask = executor.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Long result = 0L;
                for (int i = 0; i < 99999; i++) {
                    result = result + i;
                }
                System.out.println("done");
                return result;
            }
        });

        System.out.println("sum");
        Long sum = 0L;
        for (int j = 0; j < 999999999; j++) {
            sum = sum + j;
        }
        System.out.println(sum);

        try {
            Long result = futureTask.get(5, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (InterruptedException e) {
            futureTask.cancel(true);
            e.printStackTrace();
        } catch (ExecutionException e) {
            futureTask.cancel(true);
            e.printStackTrace();
        } catch (TimeoutException e) {
            futureTask.cancel(true);
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        Assert.assertTrue(true);
    }
}
