package org.example.Commands;

import org.example.Context;

public class ExitCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        return new CommandResult.Success("Bye!", null, true);
    }

    @Override
    public CommandArgs getModel() {
        return null;
    }
}
