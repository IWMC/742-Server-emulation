package sh.iwmc.core;

import sh.iwmc.Fern;
import sh.iwmc.core.service.ServiceManifest;
import sh.iwmc.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static sh.iwmc.core.ThreadingManager.ThreadingTarget.*;

/**
 * Created by Brent on 01/25/2016.
 */
@ServiceManifest(name = "Thread management")
public class DefaultThreadingManager implements ThreadingManager, Logger {

    private Map<ThreadingTarget[], ExecutorService> executorMap = new HashMap<>();

    @Override
    public <V> Future<V> submit(Runnable r, V result, ThreadingTarget target) {
        return getExecutorFor(target).submit(r, result);
    }

    @Override
    public <V> Future<V> submit(Callable<V> c, ThreadingTarget target) {
        return getExecutorFor(target).submit(c);
    }

    private ExecutorService getExecutorFor(ThreadingTarget target) {
        return executorMap.get(executorMap.keySet().stream().filter(l -> Stream.of(l).anyMatch(t -> t == target)).findFirst().get());
    }

    @Override
    public void start() {
        executorMap.put(new ThreadingTarget[]{MAIN, UTILITY, WORLD_SLOW, WORLD_FAST}, Executors.newCachedThreadPool());
    }

    @Override
    public void stop() {
        executorMap.values().forEach(e -> {
            try {
                e.shutdown();
                String time = Fern.getServer().getConfig().getString("server.threading.shutdown_wait");
                String[] timeSplit = time.split("[0-9]+", 2);
                timeSplit[0] = time.replace(timeSplit[1], "");

                TimeUnit unit = TimeUnit.SECONDS;
                if (timeSplit[1].equalsIgnoreCase("s")) {
                    unit = TimeUnit.SECONDS;
                } else if (timeSplit[1].equalsIgnoreCase("ms")) {
                    unit = TimeUnit.MILLISECONDS;
                } else if (timeSplit[1].equalsIgnoreCase("m")) {
                    unit = TimeUnit.MINUTES;
                }

                int timeValue = Integer.valueOf(timeSplit[0]);
                if (!e.awaitTermination(timeValue, unit)) {
                    warn("Forcing shutdown of an executor after " + timeSplit[0] + " "
                            + (timeValue == 1 ? unit.name().toLowerCase().substring(0, unit.name().length() - 1) : unit.name().toLowerCase())
                            + ", some tasks may be incomplete!");
                    List<Runnable> incompletes = e.shutdownNow();
                    debug(incompletes.size() + " unfinished tasks were discarded!");
                }
            } catch (InterruptedException e1) {
                e.shutdownNow();
            }
        });
    }
}
