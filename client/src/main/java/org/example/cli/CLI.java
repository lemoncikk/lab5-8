package org.example.cli;

import lombok.extern.slf4j.Slf4j;
import org.example.Controller;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.exceptions.AppException;
import org.example.exceptions.CommandExecutionException;
import org.example.command.fields.Field;
import org.example.exceptions.DataAccessException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class CLI {
    private final BufferedReader ui = new BufferedReader(new InputStreamReader(System.in));
    private final Controller ctrl;
    private boolean stopFlag = false;
    private final FieldPromptService service = new FieldPromptService(ui);

    public CLI(Controller ctrl) throws IOException {
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
            if (res.getCommandName().trim().equalsIgnoreCase("exit")) {
                stopFlag = true;
                System.out.println("Bye!");
                return;
            }
            CommandArgs model = ctrl.getCommandModel(res.getCommandName());

            if (model != null && !collectMissingArgs(model, res)) {
                return; // прервано пользователем
            }

            var result = ctrl.handle(res.getCommandName(), model);
            printResult(result);
            stopFlag = result.stopFlag();

        } catch (CommandExecutionException e) {
            if (e.getCause() instanceof IOException || e.getCause() instanceof DataAccessException) {
                log.error("System error while command execution", e);
            } else {
                log.warn("Command wasn't execute: ", e);
            }
            stopFlag = e.isStopFlag();
        } catch (AppException e) {
            log.warn("Exception:", e);
        }
    }

    private boolean collectMissingArgs(CommandArgs model, Parser.ParseResult res) throws IOException {
        var fields = model.getFields();
        var args = res.args;

        for (int i = 0; i < fields.size(); i++) {
            Field<?> field = fields.get(i);

            if (i < args.size() && field.isValid(args.get(i))) {
                field.setRawValue(args.get(i));
                continue;
            }

            // Если поле не обязательное и нет значения — пропускаем
            if (!field.isRequired() && (i >= args.size() || args.get(i).isBlank())) {
                continue;
            }

            // Запрашиваем у пользователя с повтором при ошибке
            do {
                boolean success = service.promptForField(field);
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
                System.out.println("=====================");
            }
        }
    }

    private void handleSpecialCommand(Parser.ParseResult res, Controller ctrl) {
        try {
            var executor = new ScriptExecutor(res.args.get(0), ctrl);
            stopFlag = executor.exec();
        } catch (FileNotFoundException e) {
            log.warn("File not found", e);
            System.out.println("Файл не найден");
        }
    }

    public void exec() throws IOException {
        System.out.println("Let's go");
        while(!stopFlag) {
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
