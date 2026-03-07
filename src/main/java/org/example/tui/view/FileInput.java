package org.example.tui.view;

import com.williamcallahan.tui4j.compat.bubbles.textarea.Textarea;
import com.williamcallahan.tui4j.compat.bubbletea.*;
import com.williamcallahan.tui4j.compat.lipgloss.PlacementDecorator;
import com.williamcallahan.tui4j.compat.lipgloss.Position;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.border.StandardBorder;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import com.williamcallahan.tui4j.compat.lipgloss.join.VerticalJoinDecorator;
import org.example.tui.SetSaveFileMessage;

public class FileInput implements Model {
    private final Style warningBoxStyle;
    private final Style warningMessageStyle;
    private final Model previuseModel;
    private final Textarea textArea;

    public FileInput(Model previuseModel) {
        this.previuseModel = previuseModel;
        warningBoxStyle = Style.newStyle()
                .width(10)
                .padding(5)
                .margin(3)
                .border(StandardBorder.RoundedBorder)
                .borderBackground(Color.color("#013220"))
                .background(Color.color("#013220"));
        warningMessageStyle = Style.newStyle()
                .foreground(Color.color("#000"));
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
                case "enter" -> UpdateResult.from(this.check());
                default -> UpdateResult.from(this);
            };
        }
        return UpdateResult.from(this, taCommand);
    }

    private Model check() {
        if (textArea.value().isEmpty()) {
            return this;
        }
        previuseModel.update(new SetSaveFileMessage(textArea.value()));
        return previuseModel;
    }

    private Model quit() {
        return previuseModel;
    }

    @Override
    public String view() {
        return PlacementDecorator.place(
                7,
                3,
                Position.Center,
                Position.Center,
                VerticalJoinDecorator.joinVertical(
                        Position.Center,
                        warningBoxStyle.render(
                                VerticalJoinDecorator.joinVertical(
                                        Position.Center,
                                        warningMessageStyle.render("Введите путь до файла:"),
                                        "",
                                        textArea.view()
                                )
                        )
                )
        );
    }
}

