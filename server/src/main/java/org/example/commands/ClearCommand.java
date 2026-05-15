package org.example.commands;

import org.example.Context;
import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.OneTreadContext;

public class ClearCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) throws Exception {
        ctx.clear();
        return new CommandResult("Всё было очищено", null, false);
    }

    @Override
    public String getDescription() {
        return "Очищает коллекцию";
    }

    @Override
    public CommandArgs getModel() {
        return null;
    }
}
