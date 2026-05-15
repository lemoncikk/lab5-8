package org.example.commands;

import org.example.Context;
import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.OneTreadContext;

public class InfoCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        var s = new StringBuilder();
        s.append("Количество элементов в коллекции: ")
                .append(ctx.getAll().size())
                .append("\nТип элементов: ")
                .append("MusicBand");
        return new CommandResult(s.toString(), null, false);
    }

    @Override
    public String getDescription() {
        return "Выводит информацию о коллекции";
    }

    @Override
    public CommandArgs getModel() {
        return null;
    }
}
