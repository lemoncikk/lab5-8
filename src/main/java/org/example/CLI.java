package org.example;

import org.example.command.CommandArgs;
import org.example.command.fields.DateField;
import org.example.command.fields.EnumField;
import org.example.command.exceptions.CommandExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class CLI {

    public void handleEnumField(EnumField<?> field) {
        String s = String.format(
                "Выберите один из вариантов(и введите):\n%s",
                field.formatAllowedValues()
        );
        System.out.println(s);
    }

    public void handleDataField(DateField field) {
        String s = String.format(
                "Введите дату\nФормат даты: %s",
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
            if (parseRes.getCommandName().equals("execute_script")) {
                try {
                    var executor = new ScriptExecutor(parseRes.args.get(0), ctrl);
                    stopFlag = executor.exec();
                } catch (Exception e) {
                    System.out.println(String.format("Произошла ошибка: %s", e.getMessage()));
                }
                continue;
            }
            try {
                CommandArgs model = ctrl.getCommandModel(parseRes.getCommandName());
                if (model != null && checkArgs(model, parseRes, br)) {
                    continue outerLoop;
                }
                var res = ctrl.handle(parseRes.getCommandName(), model);
                System.out.println(res.msg());
                if (res.data() != null) {
                    System.out.println(
                            res.data().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining("\n"))
                    );
                }
                stopFlag = res.stopFlag();
            }
            catch (CommandExecutionException e) {
                stopFlag = e.isStopFlag();
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean checkArgs(CommandArgs model, Parser.ParseResult res, BufferedReader br) throws IOException {
        var fields = model.getFields();
        var args = res.args;
        for (int i = 0; i<fields.size(); i++) {
            var field = fields.get(i);
            if(i<args.size()) {
                var arg = args.get(i);
                if (field.isValid(arg)) {
                    field.setRawValue(args.get(i));
                    continue;
                }
                System.out.println("Один из введённых изначально аргументов некорректен");
            }
            if(!field.isRequired()) {
                continue;
            }
            if (field instanceof EnumField<?>) {
                handleEnumField((EnumField<?>) field);
            } else if (field instanceof DateField) {
                handleDataField((DateField) field);
            } else {
                String s = String.format(
                        "Введите:\n%s(%s)",
                        field.getName(),
                        field.getDescription()
                );
                System.out.println(s);
            }
            String input = br.readLine();
            if (input.trim().equals("exit")) {
                System.out.println("Выполнение команды прервано.");
                return true;
            }
            if (!(field.isRequired()) && input.trim().isEmpty()) continue;
            if (field.isValid(input)) {
                field.setRawValue(input);
                continue;
            }
            System.out.println("Ошибка ввода, неверный тип аргумента. Попробуйте ещё раз!");
            i--;
        }
        return false;
    }
}
