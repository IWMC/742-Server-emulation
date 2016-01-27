package sh.iwmc.cache;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.displee.CacheLibrary;
import org.displee.cache.index.Index;
import org.displee.cache.index.archive.Archive;
import org.displee.cache.index.archive.file.File;
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
    private CacheLibrary cacheLib;
    private int[] assetSizes;

    private static final Marker CACHE_MARKER = MarkerManager.getMarker("CACHE");

    @Override
    public void start() {
        if(started) {
            return;
        }
        started = true;
        path = Paths.get(Fern.getServer().getConfig().getString("server.cache"));
        debug("Loading cache from " + path);
        try {
            String pathString = path.toString();
            if(!pathString.endsWith("\\")) {
                pathString += "\\";
            }
            cacheLib = new CacheLibrary(pathString, Fern.getServer().getConfig().getInt("server.revision"));
        } catch (Exception e) {
            error(CACHE_MARKER, "Cache loading failed!", e);
            Fern.getServer().stop();
        }

//        info("Calculating asset sizes... (may take a few minutes)");
//        getSizeKeys();
//        info("Completes calculating asset sizes!");
    }

    @Override
    public void stop() {
        started = false;
        cacheLib = null;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public int getRevision() {
        return cacheLib != null ? cacheLib.getRevision() : 0;
    }

    @Override
    public int[] getSizeKeys() {
        if(cacheLib == null) {
            return new int[0];
        }

        if(assetSizes != null) {
            return assetSizes;
        }

        int[] keys = new int[cacheLib.getIndices().length];
        for(int i = 0; i < cacheLib.getIndices().length; i++) {
            int indexSize = 0;
            Index index = cacheLib.getIndex(i);
            for(int a = 0; a < index.getArchives().length; a++) {
                Archive archive = index.getArchive(a);
                if(archive != null && archive.getFiles() != null) {
                    for (int f = 0; f < archive.getFiles().length; f++) {
                        File file = archive.getFile(f);
                        if (file != null) {
                            byte[] data = file.getData();
                            if (data != null) {
                                indexSize += data.length;
                            }
                        }
                    }
//                    indexSize += archive.getFiles().length;
                }
            }

            keys[i] = indexSize;
            debug("Index " + i + " size = " + indexSize + " (" + (indexSize / 1024) + " kb)");
        }

        assetSizes = keys;
        return keys;
    }

    @Override
    public String getLoggerName() {
        if(getRevision() == 0) {
            return "Cache";
        }

        return "Cache #" + getRevision();
    }

    @Override
    public Marker getMarker() {
        return CACHE_MARKER;
    }
}
