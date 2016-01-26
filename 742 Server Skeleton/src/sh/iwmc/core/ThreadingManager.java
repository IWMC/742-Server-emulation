package sh.iwmc.core;

import sh.iwmc.core.service.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by Brent on 01/25/2016.
 */
public interface ThreadingManager extends Service {
    default Future<?> submit(Runnable r) {
        return submit(r, null, ThreadingTarget.MAIN);
    }

    <V> Future<V> submit(Runnable r, V result, ThreadingTarget target);

    default <V> Future<V> submit(Callable<V> c) {
        return submit(c, ThreadingTarget.MAIN);
    }

    <V> Future<V> submit(Callable<V> c, ThreadingTarget target);

    enum ThreadingTarget {
        MAIN, WORLD_FAST, UTILITY, WORLD_SLOW;
    }
}
