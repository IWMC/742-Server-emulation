package sh.iwmc.core.service;

import java.util.Collection;

/**
 * Created by Brent on 01/24/2016.
 */
public interface ServiceHandler {
    void registerService(Service s);
    void unregisterService(Service s);
    Collection<Service> getServices();
}
