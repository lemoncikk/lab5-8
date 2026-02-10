package org.example;

public class ExitCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, String[] args) {
        return new CommandResult.Success("Bye!", null, true);
    }
}
