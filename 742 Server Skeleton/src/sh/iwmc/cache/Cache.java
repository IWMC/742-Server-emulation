package sh.iwmc.cache;

import sh.iwmc.core.service.Service;

import java.nio.file.Path;

/**
 * Created by Brent on 01/24/2016.
 */
public interface Cache extends Service {
    Path getPath();
}
