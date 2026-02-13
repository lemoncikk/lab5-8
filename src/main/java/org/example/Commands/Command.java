package org.example.Commands;

import org.example.model.Context;

public interface Command {
    CommandResult execute(Context ctx, CommandArgs args);

    String getDescription();

    default String getName() {
        String[] s = this.getClass().getName().split("\\.");
        return s[s.length-1].replace("Command", "").toLowerCase();
    }

    CommandArgs getModel();
}
