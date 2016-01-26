package sh.iwmc.core;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import sh.iwmc.cache.Cache;
import sh.iwmc.cache.DefaultCache;
import sh.iwmc.command.ConsoleInputListener;
import sh.iwmc.config.Configuration;
import sh.iwmc.core.service.Service;
import sh.iwmc.core.service.ServiceHandler;
import sh.iwmc.core.service.ServiceManifest;
import sh.iwmc.logging.Logger;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Brent on 01/24/2016.
 */
@ServiceManifest(name = "server")
public class DefaultServer implements Server, ServiceHandler, Logger {

    private static final Marker startupMarker = MarkerManager.getMarker("STARTUP");
    private List<Service> services = new ArrayList<>();
    private boolean started;
    private Cache cache;
    private Configuration serverConfig;
    private ThreadingManager threadingManager;

    private ConsoleInputListener consoleInputListener;

    @Override
    public void registerService(Service s) {
        if (!services.contains(s)) {
            services.add(s);
        }

        if (!s.hasStarted()) {
            if (s.getClass().isAnnotationPresent(ServiceManifest.class)) {
                ServiceManifest sm = s.getClass().getDeclaredAnnotation(ServiceManifest.class);
                switch (sm.threadingType()) {
                    case SYNCHRONOUS:
                    default:
                        s.start();
                        break;
                    case ASYNCHRONOUS:
                        if (getThreadingManager() != null) {
                            debug("Delegating service to thread pool");
                            getThreadingManager().submit(s);
                        } else {
                            debug("Cannot delegate to thread pool because it does not yet exist! Starting " + s.getClass().getSimpleName() + " on main thread.");
                            s.start();
                        }
                }
            } else {
                debug("Service " + s.getClass().getSimpleName() + " does not have a ServiceManifest");
                s.start();
            }
        }
    }

    public <SERVICE> SERVICE getService(Class<SERVICE> serviceClass) {
        for (Service serv : services) {
            if (serv.getClass().isAssignableFrom(serviceClass)) {
                return (SERVICE) serv;
            }
        }
        return null;
    }

    @Override
    public void unregisterService(Service s) {
        if (services.contains(s)) {
            services.remove(s);
        }

        s.stop();
    }

    @Override
    public Collection<Service> getServices() {
        return services;
    }

    @Override
    public ServiceHandler getServiceHandler() {
        return this;
    }

    @Override
    public Cache getCache() {
        return cache;
    }

    @Override
    public Configuration getConfig() {
        return serverConfig;
    }

    @Override
    public ThreadingManager getThreadingManager() {
        return threadingManager;
    }

    @Override
    public void start() {
        if (started) {
            return;
        }
        started = true;
        threadingManager = new DefaultThreadingManager();
        registerService(threadingManager);
        registerService(this);
        loadConfiguration();
        info(startupMarker, "Welcome to " + serverConfig.getString("server.name", "Untitled") + " (version " + serverConfig.getString("server.version", "Unknown") + ")");

        info(startupMarker, "Starting cache...");
        cache = new DefaultCache();
        registerService(cache);

        consoleInputListener = new ConsoleInputListener();
        registerService(consoleInputListener);

        info(startupMarker, "Server is online!");
    }

    @Override
    public void stop() {
        new Thread(() -> {
            info("Stopping server...");
            services.forEach(s -> {
                if (s != this) {
                    s.stop();
                }
            });
            info("Server stopped!");
        }).start();
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    private void loadConfiguration() {
        serverConfig = new Configuration(Paths.get("server.yml"));
    }

    @Override
    public String getLoggerName() {
        return "Server";
    }
}
