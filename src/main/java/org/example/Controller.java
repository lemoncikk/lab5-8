package org.example;

public class Controller {
    private final Context ctx;
    private final CommandRegistry registry = new CommandRegistry();

    public Controller(Context _ctx) {
        ctx = _ctx;
        registry.registry(new EchoCommand());
        registry.registry(new ExitCommand());
    }

    public CommandResult handle(String commandName, String[] args) {
        if (!registry.containsCommand(commandName)) {
            return new CommandResult.Failure("Такой команды нет", false);
        }
        return registry.get(commandName).execute(ctx, args);
    }
}
