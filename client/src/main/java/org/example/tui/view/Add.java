package org.example.tui.view;

import com.williamcallahan.tui4j.compat.bubbles.textarea.Textarea;
import com.williamcallahan.tui4j.compat.bubbletea.*;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import lombok.extern.log4j.Log4j2;
import org.example.Controller;
import org.example.command.CommandArgs;
import org.example.command.fields.Field;
import org.example.exceptions.AppException;
import org.example.tui.UpdateListMessage;

import java.util.ArrayList;
import java.util.HashMap;
@Log4j2
public class Add implements Model {
    private final static Style SELECTION = Style.newStyle().foreground(Color.color("205"));
    private final static Style WRONG = Style.newStyle().foreground(Color.color("208"));
    private final static String ADD_COMMAND = "add";
    private final Model previousModel;
    private final HashMap<String, String> fieldsStrings = new HashMap<>();
    private final ArrayList<Field<?>> fields;
    private boolean editMode = false;

    private int cursor;
    private final Textarea textArea;
    private boolean wrongField = false;
    private final Controller ctrl;
    private final CommandArgs args;

    public Add(Model previousModel, Controller ctrl) {
        this.ctrl = ctrl;
        this.previousModel = previousModel;
        try {
            this.args = ctrl.getCommandModel(ADD_COMMAND);
        } catch (AppException e) {
            log.error("Error trying get command model: ", e);
            throw e;
        }
        this.fields = args.getFields();
        textArea = new Textarea();
        textArea.focus();
        textArea.setPrompt("┃ ");
        textArea.setCharLimit(280);
        textArea.setWidth(30);
        textArea.setHeight(1);
        textArea.setShowLineNumbers(false);
    }

    @Override
    public Command init() {
        return null;
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        UpdateResult<? extends Model> taResult = textArea.update(msg);
        Command taCommand = taResult.command();
        if (msg instanceof KeyPressMessage keyPressMessage) {
            return switch (keyPressMessage.key()) {
                case "up" -> UpdateResult.from(this.moveUp());
                case "down" -> UpdateResult.from(this.moveDown());
                case "enter" -> UpdateResult.from(this.set());
                case "s", "S", "ы", "Ы" -> UpdateResult.from(this.save());
                case "q", "Q", "й", "Й" -> UpdateResult.from(this.quit());

                default -> UpdateResult.from(this);
            };
        }
        return UpdateResult.from(this, taCommand);
    }

    private Model quit() {
        if (editMode) {
            return this;
        }
        previousModel.update(new UpdateListMessage());
        return previousModel;
    }


    private Model moveUp() {
        if (editMode) editMode = false;
        if (cursor - 1 < 0) {
            cursor = fields.size() - 1;
            return this;
        }
        cursor--;
        return this;
    }

    private Model moveDown() {
        if (editMode) editMode = false;
        if (cursor + 1 >= fields.size()) {
            cursor = 0;
            return this;
        }
        cursor++;
        return this;
    }

    private Model save() {
        if (editMode) return this;
        for (var i : fields) {
            if (!fieldsStrings.containsKey(i.getName())) {
                return new InfoMessage(this, "Для добавления нового поля необходимо заполнить поле:%s".formatted(i.getName()));
            }
            if (i.getValue() == null) {
                i.setRawValue(fieldsStrings.get(i.getName()));
            }
        }
        try {
            ctrl.handle(ADD_COMMAND, args);
            return quit();
        } catch (Exception e) {
            log.error("Save error: ", e);
        }
        return this;
    }

    private Model set() {
        if(!editMode) {
            editMode = true;
            textArea.reset();
            return this;
        }
        String value = textArea.value().trim();
        if(fields.get(cursor).isValid(value)) {
            try {
                fieldsStrings.put(fields.get(cursor).getName(), value);
            } catch (Exception e) {
                log.error("");
            }
            textArea.reset();
            editMode = false;
            this.moveDown();
            return this;
        }
        textArea.reset();
        wrongField = true;
        return this;
    }

    @Override
    public String view() {
        var buffer = new StringBuilder();
        buffer.append("Заполните поля:\n\n");
        for (int i = 0; i < fields.size(); i++) {
            var argField = fields.get(i);
            if (i == cursor) {
                buffer.append(SELECTION.render(argField.getName())).append("\n")
                        .append(SELECTION.render(argField.getDescription())).append("\n");
                buffer.append(SELECTION.render("Например: ", argField.formatAllowedValues())).append("\n");
                if (fieldsStrings.containsKey(argField.getName())) {
                    buffer.append(SELECTION.render(fieldsStrings.get(argField.getName()))).append("\n");
                }
                if(editMode) {
                    if (wrongField) {
                        buffer.append(WRONG.render(argField.getName())).append("\n")
                                .append(textArea.view())
                                .append("\n")
                                .append("Неверно заполненное поле, попробуйте ещё раз")
                                .append("\n\n");
                        wrongField = false;
                        continue;
                    }
                    buffer.append(textArea.view());
                }
                buffer.append("\n\n");
            } else {
                buffer.append(argField.getName())
                        .append("\n")
                        .append(argField.getDescription())
                        .append("\n");
                buffer.append("Например: ").append(argField.formatAllowedValues()).append("\n");
                if (fieldsStrings.containsKey(argField.getName())){
                    buffer.append(fieldsStrings.get(argField.getName()));
                }
                buffer.append("\n\n");
            }
        }
        buffer.append("\n(Нажмите q чтобы выйти)\n");

        return new ScrollView(buffer, cursor).toString();
    }
}