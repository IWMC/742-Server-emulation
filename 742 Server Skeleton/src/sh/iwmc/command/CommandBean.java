package sh.iwmc.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brent on 01/25/2016.
 */
public class CommandBean {
    private String name = "";
    private List arguments = new ArrayList<>();

    private CommandSender sender;

    public CommandBean(String name, CommandSender sender) {
        this.name = name.toLowerCase();
        this.sender = sender;
    }

    public CommandBean(String name, CommandSender sender, List arguments) {
        this.name = name;
        this.sender = sender;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List getArguments() {
        return arguments;
    }

    public CommandSender getSender() {
        return sender;
    }
}
