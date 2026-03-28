package org.example.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.exceptions.CommandExecutionException;
import org.example.command.fields.*;
import org.example.model.Context;

public class SaveCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        String path = ((StringField)(args.getFields().get(0))).getValue();
        if (path == null) {
            var ctx_args = ctx.getArgs();
            if (ctx.getArgs() == null || ctx.getArgs().length == 0) {
                throw new CommandExecutionException("No argument");
            }
            path = ctx_args[0];
        }
        try {
            ctx.saveToFile(path);
            return new CommandResult(String.format("Коллекция была успешно сохранена в файл %s", path), null, false);
        }catch (Exception e) {
            throw new CommandExecutionException(e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Сохраняет коллекцию в XML файл";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new StringField("Путь до файла", "Путь до файла где будет сохранена коллекция"));
        return prepare;
    }
}
