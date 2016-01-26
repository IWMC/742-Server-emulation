package sh.iwmc.command;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import sh.iwmc.command.annotation.Command;
import sh.iwmc.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Brent on 01/25/2016.
 */
public abstract class CommandHandler implements Logger {

    public static final Marker COMMAND_MARKER = MarkerManager.getMarker("COMMAND");
    private Map<String, Method> link = new HashMap<>();
    private Map<String, Command> annotLink = new HashMap<>();
    private Map<Method, Class> methodClassLink = new HashMap<>();
    private Map<Class, Object> ownerInstanceMap = new HashMap<>();
    private boolean needsScan = true;
    private List<Class> owners = new ArrayList<>();


    public CommandHandler(Class... owners) {
        this.owners.addAll(Arrays.asList(owners));
        scan();
    }

    public CommandHandler() {
        owners.add(this.getClass());
        scan();
    }

    public void addOwner(Class c) {
        owners.add(c);
        needsScan = true;
        scan();

    }

    public List<String> readCommand(String command) {
        command = command.replace("::", "");
        List<String> list = new ArrayList<>();
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (matcher.find()) {
            list.add(matcher.group(1).replace("\"", ""));
        }

        return list;
    }

    public Result handleCommand(CommandSender sender, CommandBean command) {
        if (needsScan) {
            scan();
        }

        if (link.containsKey(command.getName())) {
            Method resultingMethod = link.get(command.getName().toLowerCase());
            Command annot = annotLink.get(command.getName().toLowerCase());

            if (annot == null || resultingMethod == null) {
                return Result.FAILED;
            }
            //check for permissions
            if (hasPermission(annot, command)) {
                return doCommand(annot, command, new MethodWrapper(resultingMethod));
            } else {
                return Result.NO_PERMISSION;
            }
        }

        return Result.FAILED;
    }

    @Override
    public Marker getMarker() {
        return COMMAND_MARKER;
    }

    public void invoke(MethodWrapper wrapper, Object... args) throws InvocationTargetException, IllegalAccessException {
        Object ownerInstance = ownerInstanceMap.getOrDefault(methodClassLink.get(wrapper.method), this);
        wrapper.method.invoke(ownerInstance, args);
    }

    public abstract Result doCommand(Command annotation, CommandBean command, MethodWrapper wrapper);

    boolean hasPermission(Command annotation, CommandBean command) {
        return true;
    }

    private void scan() {
        needsScan = false;
        for (Class owner : owners) {
            Stream.of(owner.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Command.class)).forEach(method -> {
                Command c = method.getDeclaredAnnotation(Command.class);
                String name;

                if (c.name().isEmpty()) {
                    name = method.getName();
                } else {
                    name = c.name();
                }

                link.put(name.toLowerCase(), method);
                annotLink.put(name.toLowerCase(), c);
                methodClassLink.put(method, owner);
                try {
                    if (owner != this.getClass()) {
                        ownerInstanceMap.put(owner, owner.newInstance());
                    } else {
                        ownerInstanceMap.put(owner, this);
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public enum Result {
        SUCCESS, NO_PERMISSION, FAILED;
    }

    /**
     * Designed to prevent direct invocation of a command Method. It should be called using {@link CommandHandler#invoke(MethodWrapper, Object...)} instead.
     */
    public class MethodWrapper {
        private final Method method;
        public MethodWrapper(Method m) {
            method = m;
        }
    }
}
