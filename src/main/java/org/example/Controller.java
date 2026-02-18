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
        ctx.registry.registry(new ExitCommand());
        ctx.registry.registry(new AddCommand());
        ctx.registry.registry(new ShowCommand());
        ctx.registry.registry(new SaveCommand());
        ctx.registry.registry(new HelpCommand());
        ctx.registry.registry(new InfoCommand());
        ctx.registry.registry(new ClearCommand());
        ctx.registry.registry(new InsertCommand());
        ctx.registry.registry(new CountCommand());
        ctx.registry.registry(new FilterCommand());
        ctx.registry.registry(new RemoveCommand());
        ctx.registry.registry(new ReorderCommand());
        ctx.registry.registry(new SumCommand());
        ctx.registry.registry(new UpdateCommand());
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
            System.out.println(commandName);
            throw new CommandExecutionException("Такой команды нет");
        }
        return ctx.registry.get(commandName).getModel();
    }
}
