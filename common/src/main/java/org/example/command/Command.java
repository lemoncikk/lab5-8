package org.example.command;

import org.example.exceptions.AppException;
import org.example.model.Context;

public interface Command {
    CommandResult execute(Context ctx, CommandArgs args) throws AppException;

    String getDescription();

    default String getName() {
        String[] s = this.getClass().getName().split("\\.");
        return s[s.length-1].replace("Command", "").toLowerCase();
    }

    CommandArgs getModel();
}
