package org.example.Commands;

import org.example.model.Context;

public class ExitCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        return new CommandResult("Bye", null, true);
    }

    @Override
    public String getDescription() {
        return "Завершает работу программы";
    }

    @Override
    public CommandArgs getModel() {
        return null;
    }
}
