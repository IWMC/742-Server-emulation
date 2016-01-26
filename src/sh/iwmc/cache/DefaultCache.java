package sh.iwmc.cache;

import sh.iwmc.Fern;
import sh.iwmc.core.service.ServiceManifest;
import sh.iwmc.logging.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Brent on 01/24/2016.
 */
@ServiceManifest(name = "cache")
public class DefaultCache implements Cache, Logger {

    private boolean started;
    private Path path;

    @Override
    public void start() {
        if(started) {
            return;
        }
        started = true;
        path = Paths.get(Fern.getServer().getConfig().getString("server.cache"));
        debug("Loading cache from " + path);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public Path getPath() {
        return path;
    }
}
