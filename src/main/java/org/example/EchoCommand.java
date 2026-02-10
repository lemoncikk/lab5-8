package org.example;

import java.util.Arrays;

public class EchoCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, String[] args) {
        return new CommandResult.Success(String.join("",  Arrays.stream(args).toList()), null, false);
    }
}
