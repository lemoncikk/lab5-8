package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.Flag;
import org.example.command.exceptions.CommandExecutionException;
import org.example.command.fields.StringField;
import org.example.model.Context;

public class ExitCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        if(args.getFlags().getFirst().isOn()) {
            String path = ((StringField)(args.getFields().getFirst())).getValue();
            if (path != null) {
                try {
                    ctx.saveToFile(path);
                    return new CommandResult("Bye", null, true);
                } catch (Exception e) {
                    throw new CommandExecutionException(e.getMessage());
                }
            }
            if (ctx.getArgs().length > 0) {
                try {
                    ctx.saveToFile(path);
                    return new CommandResult("Bye", null, true);
                } catch (Exception e) {
                    throw new CommandExecutionException(e.getMessage());
                }
            }
        }
        return new CommandResult("Bye", null, true);
    }

    @Override
    public String getDescription() {
        return "Завершает работу программы";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addFlag(new Flag("s", "save", "Сохранить коллекцию в файл перед выходом"))
                .addField(new StringField("Путь до файла", "Путь до файла где будет сохранена коллекция"));
        return prepare;
    }
}
