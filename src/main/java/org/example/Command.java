package org.example;

public interface Command {
    public CommandResult execute(Context ctx, String[] args);
    default public String getName() {
        String[] s = this.getClass().getName().split("\\.");
        return s[s.length-1].replace("Command", "").toLowerCase();
    }
}
