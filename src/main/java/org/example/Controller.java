package org.example;

import org.example.command.*;
import org.example.command.commands.*;
import org.example.command.exceptions.CommandExecutionException;
import org.example.model.Context;
import org.example.model.CycledStack;

public class Controller {
    private final Context ctx;
    private final CycledStack<String> history = new CycledStack<>(12);

    public Controller(Context ctx) {
        this.ctx = ctx;
        CommandLoader.load(this.ctx.registry, "org.example.command.commands");
    }

    public CommandResult handle(String commandName, CommandArgs args) throws CommandExecutionException {
        if (commandName.equals("history")) {
            var s = new StringBuilder();
            s.append("Введённые ранее команды:\n");
            for (var cmd : history) {
                s.append(cmd).append("\n");
            }
            return new CommandResult(s.toString(), null, false);
        }
        if (!ctx.registry.containsCommand(commandName)) {
            throw new CommandExecutionException("Такой команды нет");
        }
        history.push(commandName);
        return ctx.registry.get(commandName).execute(ctx, args);
    }

    public CommandArgs getCommandModel(String commandName) throws CommandExecutionException {
        if (commandName.equals("history")) {
            return null;
        }
        if (!ctx.registry.containsCommand(commandName)) {
            throw new CommandExecutionException("Такой команды нет");
        }
        return ctx.registry.get(commandName).getModel();
    }
}
