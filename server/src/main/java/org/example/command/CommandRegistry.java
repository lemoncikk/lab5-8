package org.example.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandRegistry implements Iterable<Map.Entry<String, Command>> {
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

    @Override
    public Iterator<Map.Entry<String, Command>> iterator() {
        return map.entrySet().iterator();
    }
}
