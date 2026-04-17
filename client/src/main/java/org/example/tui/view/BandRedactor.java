package org.example.tui.view;

import com.williamcallahan.tui4j.compat.bubbletea.*;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.example.Controller;
import org.example.command.CommandArgs;
import org.example.exceptions.AppException;
import org.example.model.MusicBand;
import com.williamcallahan.tui4j.compat.bubbles.textarea.Textarea;
import org.example.tui.UpdateListMessage;

import java.util.ArrayList;
@Slf4j
public class BandRedactor implements Model {
    private final static Style SELECTION = Style.newStyle().foreground(Color.color("205"));
    private final static Style WRONG = Style.newStyle().foreground(Color.color("208"));
    private final static String UPDATE_COMMAND = "update";
    private final Model previuseModel;
    private final Controller ctrl;
    private final MusicBand mb;
    private Textarea textArea;
    private final ArrayList<String> fieldsStrings;
    private final ArrayList<org.example.command.fields.Field<?>> fields;
    private int cursor;
    private boolean EditMod = false;
    private boolean wrongField = false;
    private final CommandArgs args;
    private boolean error = false;
    private String errorMsg;

    public BandRedactor(Controller ctrl, MusicBand mb, Model previuseModel) {
        this.ctrl = ctrl;
        this.mb = mb;
        this.previuseModel = previuseModel;
        this.fieldsStrings = mb.getFieldsStrings();
        try {
            this.args = ctrl.getCommandModel(UPDATE_COMMAND);
        } catch (AppException e) {
            log.error("Error trying get command model: ", e);
            throw e;
        }
        fields = args.getFields();
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
                // "up" and "k" keys move cursor up
                case "up" -> UpdateResult.from(this.moveUp());

                // "down" and "j" keys move cursor down
                case "down" -> UpdateResult.from(this.moveDown());

                case "enter" -> UpdateResult.from(this.EditMode());

                case "s", "S", "ы", "Ы" -> UpdateResult.from(this.save());

                // "q" exits the program
                case "q", "Q", "й", "Й" -> UpdateResult.from(this.quit());
                default -> UpdateResult.from(this);
            };
        }
        return UpdateResult.from(this, taCommand);
    }

    private Model save() {
        if (EditMod) return this;
        try {
            for (int i = 0; i < fieldsStrings.size(); i++) {
                fields.get(i).setRawValue(fieldsStrings.get(i));
            }
            ctrl.handle(UPDATE_COMMAND, args);
        } catch (AppException e) {
            log.error("Ошибка", e);
            error = true;
            errorMsg = e.getMessage();
        }
        return this;
    }

    private Model quit() {
        if (EditMod) {
            EditMod = false;
            textArea.reset();
            return this;
        }
        previuseModel.update(new UpdateListMessage());
        return previuseModel;
    }

    private Model EditMode() {
        if (cursor == 0) return this;
        if (!EditMod) {
            EditMod = true;
            textArea.reset();
            return this;
        }
        String value = textArea.value().trim();
        if(fields.get(cursor).isValid(value)) {
            fieldsStrings.set(cursor, value);
            textArea.reset();
            EditMod = false;
            return this;
        }
        wrongField = true;
        return this;
    }

    private Model moveUp() {
        if (EditMod || wrongField) {
            EditMod = false;
            wrongField = false;
            textArea.reset();
        }
        if (cursor - 1 < 0) {
            cursor = fieldsStrings.size() - 1;
            return this;
        }
        cursor--;
        return this;
    }

    private Model moveDown() {
        if (EditMod || wrongField) {
            EditMod = false;
            wrongField = false;
            textArea.reset();
        }
        if (cursor + 1 >= fieldsStrings.size()) {
            cursor = 0;
            return this;
        }
        cursor++;
        return this;
    }

    @Override
    public String view() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < fieldsStrings.size(); i++) {
            if (i == cursor) {
                if (EditMod){
                    textArea.setPlaceholder(fieldsStrings.get(i));
                    if (wrongField) {
                        buffer.append(WRONG.render(fields.get(i).getName())).append("\n")
                                .append(textArea.view())
                                .append("\n")
                                .append("Неверно заполненное поле, попробуйте ещё раз")
                                .append("\n\n");
                        wrongField = false;
                        continue;
                    }

                    buffer.append(SELECTION.render(fields.get(i).getName())).append("\n")
                            .append(textArea.view()).append("\n\n");
                    continue;
                }
                buffer.append(SELECTION.render(fields.get(i).getName()))
                        .append("\n")
                        .append(SELECTION.render(fieldsStrings.get(i))).append("\n");
                continue;
            }
            buffer.append(fields.get(i).getName())
                    .append("\n")
                    .append(fieldsStrings.get(i)).append("\n");


        }
        if (error) buffer.append(WRONG.render("В процессе работы произошла ошибка!\n", errorMsg, "\n"));
        buffer.append("\n(Нажмите q чтобы вернуться назад, s чтобы сохранить отредактированные данные)");
        return new ScrollView(buffer, cursor).toString();
    }
}
