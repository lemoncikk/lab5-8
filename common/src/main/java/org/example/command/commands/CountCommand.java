package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.IntField;
import org.example.model.Context;

public class CountCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        int i = (int)(args.getFields().get(0).getValue());

        var count = ctx.getStore().stream().filter(e -> e.getGenre().getId() > i).count();
        return new CommandResult(String.format("%d", count), null, false);
    }

    @Override
    public String getDescription() {
        return "Выводит количество групп жанр которых больше некоторого варианта (PSYCHEDELIC_ROCK - 1, HIP_HOP - 2, BLUES - 3)";
    }

    @Override
    public String getName() {
        return "count_greater_than_genre";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new IntField("Число", "Число жанр которого больше некоторого варианта (PSYCHEDELIC_ROCK - 1, HIP_HOP - 2, BLUES - 3)", true));
        return prepare;
    }
}
