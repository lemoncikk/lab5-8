package org.example.command;

import java.util.HashMap;

public class CommandRegistry {
    private final HashMap<String, Command> map = new HashMap<>();

    public void registry(Command cmd) {
        map.put(cmd.getName(), cmd);
    }

    public Command get(String key) {
        return map.get(key);
    }

    public boolean containsCommand(String key) {
        return map.containsKey(key);
    }
}
