package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.StringField;
import org.example.model.Context;
import org.example.model.MusicBand;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FilterCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        String s = (String)(args.getFields().getFirst().getValue());
        ArrayList<MusicBand> m = ctx.getStore().stream().filter(
                e -> e.getName().startsWith(s)).collect(Collectors.toCollection(ArrayList::new));
        return new CommandResult("Были найдены совпадения:", m, false);
    }

    @Override
    public String getDescription() {
        return "Возвращает список всех групп название которых начинается с данного слова";
    }

    @Override
    public String getName() {
        return "filter_starts_with_name";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new StringField("Слово", "Слово по которому будет идти фильтрация", true));
        return prepare;
    }
}
