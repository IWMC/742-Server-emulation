package sh.iwmc.command;

import org.apache.logging.log4j.Marker;
import sh.iwmc.Fern;
import sh.iwmc.command.annotation.Command;
import sh.iwmc.logging.Logger;

/**
 * Created by Brent on 01/25/2016.
 */
public class ConsoleCommands implements Logger {


    @Override
    public Marker getMarker() {
        return CommandHandler.COMMAND_MARKER;
    }

    @Command
    public void version() {
        info("The server is running the Fern framework version " + Fern.getServer().getConfig().getString("server.version", "Unknown") + ".");
    }

    @Command
    public void reloadConfig() {
        info("Reloading configuration...");
        Fern.getServer().getConfig().reload();
        info("Configuration reloaded");
    }

}
