package org.example.tui.view;

import com.williamcallahan.tui4j.compat.bubbletea.*;
import com.williamcallahan.tui4j.compat.lipgloss.PlacementDecorator;
import com.williamcallahan.tui4j.compat.lipgloss.Position;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.border.StandardBorder;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;

public class InfoMessage implements Model {
    private final Style boxStyle;
    private final Style textStyle;
    private final Model previuseModel;
    private final String[] messages;

    public InfoMessage(Model previuseModel, String... messages) {
        this.messages = messages;
        this.previuseModel = previuseModel;
        boxStyle = Style.newStyle()
                .width(10)
                .padding(5)
                .margin(3)
                .border(StandardBorder.RoundedBorder)
                .borderBackground(Color.color("#013220"))
                .background(Color.color("#013220"));
        textStyle = Style.newStyle()
                .foreground(Color.color("#000"));
    }

    @Override
    public Command init() {
        return null;
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage) {
            return UpdateResult.from(previuseModel);
        }
        return UpdateResult.from(this);
    }

    private Model quit() {
        return previuseModel;
    }

    @Override
    public String view() {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < messages.length; i++) {
            content.append(messages[i]);
            if (i < messages.length - 1) {
                content.append("\n");
            }
        }

        // 2. Рендерим текст и упаковываем в рамку
        String renderedText = textStyle.render(content.toString());
        String boxedContent = boxStyle.render(renderedText);

        // 3. Центрируем на экране
        return PlacementDecorator.place(
                0, 0,
                Position.Center,
                Position.Center,
                boxedContent
        );
    }
}
