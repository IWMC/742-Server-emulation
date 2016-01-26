package sh.iwmc.command;

import sh.iwmc.Fern;
import sh.iwmc.command.annotation.Command;
import sh.iwmc.core.service.Service;
import sh.iwmc.core.service.ServiceManifest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Brent on 01/25/2016.
 */
@ServiceManifest(name = "console input listener", threadingType = ServiceManifest.ThreadingType.ASYNCHRONOUS)
public class ConsoleInputListener extends CommandHandler implements ConsoleCommandSender, Service {

    private Scanner scanner;
    private boolean running = true;
    private List<String> permissions = new ArrayList<>(Arrays.asList("*"));

    @Override
    public void start() {
        addOwner(ConsoleCommands.class);
        scanner = new Scanner(System.in);
        debug("Enabling input");
        String input = "";
        do {
            input = scanner.nextLine();
            parseCommand(input);
        } while (running);
        debug("Input disabled");
        Fern.getServer().stop();
    }

    @Override
    public void stop() {
        scanner.close();
    }

    private void parseCommand(String line) {
        List<String> args = readCommand(line);
        String name = args.get(0);
        args.remove(name);
        CommandBean c = new CommandBean(name, this, args);
        handleCommand(this, c);
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }


    @Override
    public Result doCommand(Command annotation, CommandBean command, MethodWrapper wrapper) {
        try {
            invoke(wrapper);
            return Result.SUCCESS;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return Result.FAILED;
    }

    @Override
    boolean hasPermission(Command annotation, CommandBean command) {
        return true; //console can do everything
    }

    @Command(name = "stop")
    public void stopCommand() {
        running = false;
    }

    @Override
    public String getLoggerName() {
        return "Console";
    }
}
