package org.example;

import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.DateField;
import org.example.command.fields.EnumField;
import org.example.command.exceptions.CommandExecutionException;
import org.example.command.fields.Field;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLI {
    private BufferedReader ui = new BufferedReader(new InputStreamReader(System.in));
    private Controller ctrl;
    private boolean stopflag = false;

    public CLI(Controller ctrl) {
        this.ctrl = ctrl;
    }

    Parser.ParseResult readAndParseCommand() throws IOException{
        String line = ui.readLine();
        return (line == null) ? null : Parser.parse(line);
    }

    private boolean isSpecialCommand(Parser.ParseResult res) {
        return res.getCommandName().equals("execute_script");
    }

    private void handleRegularCommand(Parser.ParseResult res, Controller ctrl) throws IOException {
        try {
            CommandArgs model = ctrl.getCommandModel(res.getCommandName());

            if (model != null && !collectMissingArgs(model, res)) {
                return; // прервано пользователем
            }

            var result = ctrl.handle(res.getCommandName(), model);
            printResult(result);
            stopflag = result.stopFlag();

        } catch (CommandExecutionException e) {
            stopflag = e.isStopFlag();
            System.out.println(e.getMessage());
        }
    }

    private boolean collectMissingArgs(CommandArgs model, Parser.ParseResult res) throws IOException {
        var fields = model.getFields();
        var args = res.args;

        for (int i = 0; i < fields.size(); i++) {
            Field<?> field = fields.get(i);

            // Пробуем взять из аргументов командной строки
            if (i < args.size() && field.isValid(args.get(i))) {
                field.setRawValue(args.get(i));
                continue;
            }

            // Если поле не обязательное и нет значения — пропускаем
            if (!field.isRequired() && (i >= args.size() || args.get(i).isBlank())) {
                continue;
            }

            // Запрашиваем у пользователя с повтором при ошибке
            var fieldPromptService = new FieldPromptService(ui);
            // успешно валидировано
            do {
                boolean success = fieldPromptService.promptForField(field);
                if (!success) return false; // прервано
            } while (field.getValue() == null);
        }
        return true;
    }

    private void printResult(CommandResult res) {
        if (!res.msg().isEmpty()) System.out.println(res.msg());
        if (res.data() != null) {
            for (var i : res.data()) {
                System.out.println(i);
            }
        }
    }

    private void handleSpecialCommand(Parser.ParseResult res, Controller ctrl) {
        try {
            var executor = new ScriptExecutor(res.args.get(0), ctrl);
            stopflag = executor.exec();
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        }
    }

    public void exec() throws IOException {
        System.out.println("Let's go");
        while(!stopflag) {
            var parseResult = readAndParseCommand();
            if (parseResult == null) break;
            if (isSpecialCommand(parseResult)) {
                handleSpecialCommand(parseResult, ctrl);
                continue;
            }
            handleRegularCommand(parseResult, ctrl);
        }
    }
}
