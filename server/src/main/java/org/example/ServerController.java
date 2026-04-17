package org.example;

import org.example.command.CommandArgs;
import org.example.command.CommandRegistry;
import org.example.command.CommandResult;
import org.example.exceptions.AppException;
import org.example.exceptions.CommandNotFoundException;
import org.example.model.Context;
import org.example.model.CycledStack;

public class ServerController implements org.example.Controller {
    private final Context ctx;
    private final CycledStack<String> history = new CycledStack<>(12);
    private final CommandRegistry serverCommands = new CommandRegistry();

    public ServerController(Context ctx) {
        this.ctx = ctx;
        CommandLoader.load(this.ctx.registry, "org.example.commands");
        CommandLoader.load(serverCommands, "org.example.serverCommands");
    }

    public CommandResult handle(String commandName, CommandArgs args) throws AppException {
        if (commandName.equals("history")) {
            var s = new StringBuilder();
            s.append("Введённые ранее команды:\n");
            for (var cmd : history) {
                s.append(cmd).append("\n");
            }
            return new CommandResult(s.toString(), null, false);
        }
        if (!ctx.registry.containsCommand(commandName)) {
            throw new CommandNotFoundException("Команда не найдена");
        }
        history.push(commandName);
        return ctx.registry.get(commandName).execute(ctx, args);
    }

    public CommandResult specialHandle(String commandName, CommandArgs args) throws AppException {
        return serverCommands.get(commandName).execute(ctx, args);
    }

    public CommandArgs getCommandModel(String commandName) throws AppException{
        if (commandName.equals("history")) {
            return null;
        }
        if (!ctx.registry.containsCommand(commandName)) {
            throw new CommandNotFoundException("Команда не найдена");
        }
        return ctx.registry.get(commandName).getModel();
    }
}
