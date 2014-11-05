/**
 *
 */
package me.yumin.java.labs.test.other;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import java.util.Observable;
import java.util.Observer;

/**
 * @author yumin
 * @since 2014-11-05 16:54
 */
public class SignalTest implements Observer {

    /**
     * @param args
     */
    public static void main(String[] args) {

        new SignalTest().go();
    }

    private void go() {

        try {
            HandlerTest sh = new HandlerTest();
            sh.addObserver(this);
            sh.handleSignal("HUP");
            sh.handleSignal("BUS");
            System.out.println("Sleeping for 60 seconds: hit me with signals!");
            Thread.sleep(60000);
        } catch (Throwable x) {
            x.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void update(Observable arg0, Object arg1) {

        System.out.println("Received signal: " + arg1);
    }

    /**
     * HandlerTest Class
     */
    class HandlerTest extends Observable implements SignalHandler {

        @Override
        public void handle(Signal signal) {

            setChanged();
            notifyObservers(signal);
        }

        /**
         * @param signalName
         * @throws IllegalArgumentException
         */
        public void handleSignal(String signalName) throws IllegalArgumentException {

            try {
                Signal.handle(new Signal(signalName), this);
            } catch (IllegalArgumentException x) {
                throw x;
            } catch (Throwable x) {
                throw new IllegalArgumentException("Signal unsupported: " + signalName, x);
            }
        }
    }
}
