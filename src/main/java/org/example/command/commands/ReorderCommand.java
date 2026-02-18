package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.model.Context;

public class ReorderCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        ctx.sort(true);
        return new CommandResult("Коллекция была отсортирована в обратном порядке", null, false);
    }

    @Override
    public String getDescription() {
        return "Сортирует коллекцию в обратном текущему порядке";
    }

    @Override
    public CommandArgs getModel() {
        return null;
    }
}
