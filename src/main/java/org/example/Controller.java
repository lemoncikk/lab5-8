package org.example;

import org.example.command.*;
import org.example.command.commands.AddCommand;
import org.example.command.commands.ExitCommand;
import org.example.command.commands.SaveCommand;
import org.example.command.commands.ShowCommand;
import org.example.command.exceptions.CommandExecutionException;
import org.example.model.Context;

public class Controller {
    private final Context ctx;

    public Controller(Context ctx) {
        this.ctx = ctx;
        ctx.registry.registry(new ExitCommand());
        ctx.registry.registry(new AddCommand());
        ctx.registry.registry(new ShowCommand());
        ctx.registry.registry(new SaveCommand());
    }

    public CommandResult handle(String commandName, CommandArgs args) throws CommandExecutionException {
        if (!ctx.registry.containsCommand(commandName)) {
            throw new CommandExecutionException("Такой команды нет");
        }
        return ctx.registry.get(commandName).execute(ctx, args);
    }

    public CommandArgs getCommandModel(String commandName) throws CommandExecutionException {
        if (!ctx.registry.containsCommand(commandName)) {
            System.out.println(commandName);
            throw new CommandExecutionException("Такой команды нет");
        }
        return ctx.registry.get(commandName).getModel();
    }
}
