package org.example;

import org.example.Commands.*;
import org.example.Commands.exceptions.CommandExecutionException;

public class Controller {
    private final Context ctx;
    private final CommandRegistry registry = new CommandRegistry();

    public Controller(Context ctx) {
        this.ctx = ctx;
        registry.registry(new ExitCommand());
    }

    public CommandResult handle(String commandName, CommandArgs args) throws CommandExecutionException {
        if (!registry.containsCommand(commandName)) {
            throw new CommandExecutionException("Такой команды нет");
        }
        return registry.get(commandName).execute(ctx, args);
    }

    public CommandArgs getCommandModel(String commandName) throws CommandExecutionException {
        if (!registry.containsCommand(commandName)) {
            throw new CommandExecutionException("Такой команды нет");
        }
        return registry.get(commandName).getModel();
    }
}
