package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.model.Context;

public class InfoCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        var s = new StringBuilder();
        s.append("Количество элементов в коллекции: ")
                .append(ctx.getStore().size())
                .append("\nДата инициализации: ")
                .append(ctx.getInitDate())
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
