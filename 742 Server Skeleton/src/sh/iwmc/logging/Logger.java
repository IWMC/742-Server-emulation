package sh.iwmc.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Created by Brent on 01/24/2016.
 */
public interface Logger {

    Marker DEFAULT_MARKER = MarkerManager.getMarker("UNCATEGORIZED");

    default void info(Object msg) {
        info(getMarker(), msg);
    }

    default void info(Marker marker, Object msg) {
        getLogger().info(marker, msg);
    }

    default void warn(Object msg) {
        warn(getMarker(), msg);
    }

    default void warn(Marker marker, Object msg) {
        getLogger().warn(marker, msg);
    }

    default void warn(Marker marker, Object msg, Throwable t) {
        getLogger().warn(marker, msg, t);
    }

    default void error(Object msg) {
        error(getMarker(), msg);
    }

    default void error(Marker marker, Object msg) {
        getLogger().error(marker, msg);
    }

    default void error(Marker marker, Object msg, Throwable t) {
        getLogger().error(marker, msg, t);
    }

    default void debug(Object msg) {
        debug(getMarker(), msg);
    }

    default void debug(Marker marker, Object msg) {
        getLogger().debug(marker, msg);
    }

    default Marker getMarker() {
        return DEFAULT_MARKER;
    }

    default org.apache.logging.log4j.Logger getLogger() {
        return LogManager.getLogger(getLoggerName());
    }

    default String getLoggerName() {
        return this.getClass().getSimpleName();
    }

}
