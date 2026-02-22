package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.exceptions.CommandExecutionException;
import org.example.command.fields.StringField;
import org.example.model.Context;

public class HelpCommand implements Command {

    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        StringBuilder msg = new StringBuilder();
        String cmdName = ((StringField)(args.getFields().get(0))).getValue();
        if (cmdName == null) {
            for (var entry : ctx.registry) {
                msg.append(String.format(
                        "Команда %s - %s\n",
                        entry.getValue().getName(),
                        entry.getValue().getDescription()
                ));
            }
        } else {
            if (!ctx.registry.containsCommand(cmdName)) {
                throw new CommandExecutionException("Такой команды нет");
            }
            Command cmd = ctx.registry.get(cmdName);
            msg.append(String.format(
                    "Команда %s - %s\nАргументы команды(в порядке ввода):\n",
                    cmd.getName(),
                    cmd.getDescription()
                    ));
            for (var field : cmd.getModel().getFields()) {
                msg.append(String.format(
                        "%s(принимает %s) - %s\nТип аргумента - %s\nЗначение по умолчанию - %s\n\n",
                        field.getName(),
                        field.getType().toString(),
                        field.getDescription(),
                        (field.isRequired() ? "Обязательный" : "Опциональный"),
                        (field.getDefaultValue() == null ? "Нет" : field.getDefaultValue().toString())
                        ));
            }
            msg.append("Флаги(если есть):\n");
            for (var flag : cmd.getModel().getFlags()) {
                msg.append(String.format(
                        "-%s --%s\n %s\n",
                        flag.getShortName(),
                        flag.getLongName(),
                        flag.getDescription()
                        ));
            }
        }
        return new CommandResult(msg.toString(), null, false);
    }

    @Override
    public String getDescription() {
        return "Выводит информацию о всех командах или о отдельной команде в частности";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new StringField("Название команды", "Название команды о которой необходимо вывести больше информации"));
        return prepare;
    }
}
