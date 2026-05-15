package org.example.command;

import org.example.exceptions.AppException;
import org.example.Context;

public interface Command {
    CommandResult execute(Context ctx, CommandArgs args) throws Exception;

    String getDescription();

    default String getName() {
        String[] s = this.getClass().getName().split("\\.");
        return s[s.length-1].replace("Command", "").toLowerCase();
    }

    CommandArgs getModel();
}
