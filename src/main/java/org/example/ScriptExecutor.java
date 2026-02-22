package org.example;

import org.example.command.CommandArgs;
import org.example.command.exceptions.CommandExecutionException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class ScriptExecutor {

    private final BufferedReader file;
    private final Controller ctrl;

    public ScriptExecutor(String path, Controller ctrl) throws FileNotFoundException {
        file = new BufferedReader(new FileReader(path));
        this.ctrl = ctrl;
    }

    public boolean exec() {
        int lineCounter = 1;
        boolean stopFlag = false;
        String line;

        try {
            while ((line = file.readLine()) != null) {
                Parser.ParseResult parseRes = Parser.parse(line);
                CommandArgs model = ctrl.getCommandModel(parseRes.getCommandName());

                if (model != null) {
                    var fields = model.getFields();
                    var args = parseRes.args;

                    for (int i = 0; i < fields.size(); i++) {
                        var field = fields.get(i);

                        if (i < args.size()) {
                            var arg = args.get(i);
                            if (field.isValid(arg)) {
                                field.setRawValue(arg);
                                continue;
                            }
                            throw new CommandExecutionException(
                                    String.format("%s <-- Обнаружен невалидный аргумент", line));
                        }

                        if (!field.isRequired()) {
                            continue;
                        }

                        String input = file.readLine();
                        if (input == null) {
                            throw new CommandExecutionException(
                                    "Неожиданный конец файла при чтении аргумента");
                        }

                        if (field.isValid(input)) {
                            field.setRawValue(input);
                            continue;
                        }
                        throw new CommandExecutionException(
                                String.format("%s <-- Обнаружен невалидный аргумент", line));
                    }
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
                lineCounter++;
            }
        } catch (CommandExecutionException e) {
            System.out.println(String.format(
                    "Произошла ошибка:\nСтрока: %d\nОшибка: %s",
                    lineCounter, e.getMessage()));
        } catch (IOException e) {
            System.out.println(String.format(
                    "Ошибка ввода-вывода:\nСтрока: %d\nОшибка: %s",
                    lineCounter, e.getMessage()));
        } finally {
            try {
                if (file != null) file.close();
            } catch (IOException e) {
                System.err.println("Не удалось закрыть файл: " + e.getMessage());
            }
        }
        return stopFlag;

    }
}