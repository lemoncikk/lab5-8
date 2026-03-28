package org.example.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.model.Context;

public class ShowCommand implements Command {

    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        return new CommandResult("", ctx.getAll(), false);
    }

    @Override
    public String getDescription() {
        return "Выводит все элементы коллекции";
    }

    @Override
    public CommandArgs getModel() {
        return null;
    }
}
