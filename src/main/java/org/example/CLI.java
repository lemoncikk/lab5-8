package org.example;

import org.example.Commands.CommandArgs;
import org.example.Commands.Fields.DataField;
import org.example.Commands.Fields.EnumField;
import org.example.Commands.Fields.Field;
import org.example.Commands.exceptions.CommandExecutionException;
import org.example.Commands.exceptions.ValidationException;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLI {
    public void handleEnumField(EnumField<?> field) {
        String s = String.format(
                "Выберите один из вариантов(и введите):\n %s",
                field.formatAllowedValues()
        );
        System.out.println(s);
    }
    public void handleDataField(DataField field) {
        String s = String.format(
                "Введите дату\n Формат даты: %s",
                field.formatDataTimeFormat()
        );
        System.out.println(s);
    }
    public void exec(Controller ctrl) throws IOException {
        System.out.println("Start!");
        boolean stopFlag = false;
        outerLoop:
        while(!stopFlag) {
            var br = new BufferedReader(new InputStreamReader(System.in));
            Parser.ParseResult parseRes = Parser.parse(br.readLine());
            try {
                CommandArgs model = ctrl.getCommandModel(parseRes.getCommandName());
                if (model != null) {
                    var fields = model.getFields();
                    for (int i = 0; i<fields.size(); i++) {
                        var field = fields.get(i);
                        if (field instanceof EnumField<?>) {
                            handleEnumField((EnumField<?>) field);
                        } else if (field instanceof DataField) {
                            handleDataField((DataField) field);
                        }
                        else {
                            String s = String.format(
                                    "Введите: \n%s(%s)",
                                    field.getName(),
                                    field.getDescription()
                            );
                            System.out.println(s);
                        }
                        String input = br.readLine();
                        if (input.trim().equals("exit")) {
                            System.out.println("Выполнение команды прервано.");
                            continue outerLoop;
                        }
                        if (field.isValid(input)) {
                            field.setRawValue(input);
                            continue;
                        }
                        System.out.println("Ошибка ввода, неверный тип аргумента. Попробуйте ещё раз!");
                        i--;
                    }

                }
                var res = ctrl.handle(parseRes.getCommandName(), model);
                System.out.println(res.msg());
                stopFlag = res.stopFlag();
            }
            catch (CommandExecutionException e) {
                stopFlag = e.isStopFlag();
                System.out.println(e.getMessage());
            }
        }
    }
}
