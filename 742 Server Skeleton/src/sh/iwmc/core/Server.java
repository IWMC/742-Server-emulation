package sh.iwmc.core;

import sh.iwmc.cache.Cache;
import sh.iwmc.config.Configuration;
import sh.iwmc.core.service.Service;
import sh.iwmc.core.service.ServiceHandler;

/**
 * Created by Brent on 01/24/2016.
 */
public interface Server extends Service {
    ServiceHandler getServiceHandler();
    Cache getCache();
    Configuration getConfig();
    ThreadingManager getThreadingManager();
}
