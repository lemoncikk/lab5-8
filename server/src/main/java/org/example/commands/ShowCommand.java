package org.example.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.IntField;
import org.example.command.fields.StringField;
import org.example.model.Context;

public class ShowCommand implements Command {
    static final int MAX_COUNT = 50;

    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        var fields = args.getFields();
        var limit = (fields.isEmpty() || fields.get(0) == null) ? MAX_COUNT : ((IntField)(fields.get(0))).getValue();
        return new CommandResult("", ctx.getAll(Math.min(MAX_COUNT, limit)), false);
    }

    @Override
    public String getDescription() {
        return "Выводит элементы коллекции";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new IntField("Кол-во объектов", "Количество объектов которое будет возвращено", true));
        return prepare;
    }
}
