package org.example.tui.view;

import com.williamcallahan.tui4j.compat.bubbletea.*;
import com.williamcallahan.tui4j.compat.lipgloss.PlacementDecorator;
import com.williamcallahan.tui4j.compat.lipgloss.Position;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.border.StandardBorder;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import com.williamcallahan.tui4j.compat.lipgloss.join.VerticalJoinDecorator;
import org.example.model.MusicBand;
import org.example.tui.DeleteBandMessage;

public class DeleteScreen implements Model {
    private final Style warningBoxStyle;
    private final Style warningMessageStyle;
    private final MusicBand mb;
    private final Model previuseModel;

    public DeleteScreen(MusicBand mb, Model previuseModel) {
        this.previuseModel = previuseModel;
        this.mb = mb;
        warningBoxStyle = Style.newStyle()
                .width(10)
                .padding(5)
                .margin(3)
                .border(StandardBorder.RoundedBorder)
                .borderBackground(Color.color("#ff0000"))
                .background(Color.color("#ff0000"));
        warningMessageStyle = Style.newStyle()
                .foreground(Color.color("#fff"))
                .blink(true);
    }

    @Override
    public Command init() {
        return null;
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage keyPressMessage) {
            return switch (keyPressMessage.key()) {

                case "y", "Y", "н", "Н" -> UpdateResult.from(quit());

                default -> UpdateResult.from(previuseModel);
            };
        }
        return UpdateResult.from(this);
    }

    private Model quit() {
        previuseModel.update(new DeleteBandMessage(mb.getId()));
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
                                        warningMessageStyle.render("Вы уверены что хотите удалить эту группу?"),
                                        "",
                                        "“%s”".formatted(mb.getName() == null ? "Названия нет" : mb.getName())
                                )
                        )
                )
        );
    }
}
