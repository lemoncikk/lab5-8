package org.example.commands;

import org.example.Context;
import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.IntField;
import org.example.OneTreadContext;
import org.example.exceptions.AppException;

public class RemoveCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) throws Exception {
        ctx.removeById((int)(args.getFields().get(0).getValue()));
        return new CommandResult("Объект успешно удалён", null, false);
    }

    @Override
    public String getDescription() {
        return "Удаляет их коллекции элемент с указанным ID";
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new IntField("ID", "Уникальный номер элемента в коллекции", true));
        return prepare;
    }
}
