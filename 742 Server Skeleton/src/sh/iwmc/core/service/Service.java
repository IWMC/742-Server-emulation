package sh.iwmc.core.service;

/**
 * Created by Brent on 01/24/2016.
 */
public interface Service extends Runnable {
    void start();
    void stop();

    default boolean hasStarted() {
        return false;
    }

    @Override
    default void run() {
        start();
    }
}
