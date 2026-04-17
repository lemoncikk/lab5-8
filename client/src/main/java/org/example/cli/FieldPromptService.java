package org.example.cli;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.example.command.fields.DateField;
import org.example.command.fields.EnumField;
import org.example.command.fields.Field;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class FieldPromptService {
    private final BufferedReader ui;

    public FieldPromptService(BufferedReader ui) {
        this.ui = ui;
    }

    public boolean promptForField(Field field) throws IOException {
        printPrompt(field);

        String input = ui.readLine();
        if (input == null || input.trim().equalsIgnoreCase("exit")) {
            System.out.println("Выполнение команды прервано.");
            return false;
        }

        if (!field.isRequired() && input.trim().isEmpty()) {
            return true;
        }

        if (field.isValid(input)) {
            field.setRawValue(input);
            return true;
        }

        System.out.println("Ошибка ввода, неверный тип аргумента. Попробуйте ещё раз!");
        log.debug("Invalid argument: {}", input);
        return true; // вызывающий код должен повторить запрос
    }

    private void printPrompt(Field<?> field) {
        if (field instanceof EnumField<?> enumField) {
            System.out.println(String.format(
                    "Выберите один из вариантов:\n%s",
                    enumField.formatAllowedValues()));
            return;
        }
        if (field instanceof DateField dateField) {
            System.out.println(String.format(
                    "Введите дату\nФормат: %s",
                    dateField.formatAllowedValues()));
            return;
        }
        System.out.println(String.format(
                "Введите: %s (%s)",
                field.getName(), field.getDescription()));

    }
}