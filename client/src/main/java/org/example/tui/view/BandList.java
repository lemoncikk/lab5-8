package org.example.tui.view;

import com.williamcallahan.tui4j.compat.bubbletea.*;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import lombok.extern.log4j.Log4j2;
import org.example.Controller;
import org.example.model.MusicBand;
import org.example.tui.DeleteBandMessage;
import org.example.tui.SetSaveFileMessage;
import org.example.tui.UpdateListMessage;

import java.util.List;

@Log4j2
public class BandList implements Model {
    private final static Style SELECTION = Style.newStyle().foreground(Color.color("205"));
    private final static Style WRONG = Style.newStyle().foreground(Color.color("208"));
    private final static String REMOVE_COMMAND = "remove_by_id";
    private final static String SAVE_COMMAND = "save";
    private final static String FILTER_COMMAND = "filter_starts_with_name";
    private final static String REVERSE_COMMAND = "reorder";
    private final static int ADD_BUTTON = 0;
    private final static String[] HELP_INFO = {"q - выйти",
            "↓/↑ - перейти к нужному элементу",
            "s - задать путь для сохранения",
            "r - пересортировать хранилище",
            "enter - выбрать элемент",
            "n - добавить новый элемент"};

    private final Controller ctrl;
    private List<MusicBand> BandList;
    private final String[] extraFields = {"Добавить новое поле"};
    private String file;

    private boolean error = false;
    private String errorMsg;

    private int cursor;

    public BandList(Controller ctrl) {
        BandList = ctrl.handle("show", null).data();
        this.ctrl = ctrl;
    }

    @Override
    public Command init() {
        return null;
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage keyPressMessage) {
            if (BandList == null) return UpdateResult.from(this);
            return switch (keyPressMessage.key()) {
                // "up" and "k" keys move cursor up
                case "up" -> UpdateResult.from(this.moveUp());

                // "down" and "j" keys move cursor down
                case "down" -> UpdateResult.from(this.moveDown());

                // "enter" selects the current item
                case "enter" -> UpdateResult.from(this.makeChoice());

                case "delete" -> UpdateResult.from(this.delete());

                case "s", "S", "ы", "Ы" -> UpdateResult.from(new FileInput(this));

                case "r", "R", "к", "К" -> UpdateResult.from(this.reverse(), UpdateListMessage::new);

                case "n", "N", "т", "Т" -> UpdateResult.from(new Add(this, ctrl), UpdateListMessage::new);

                case "h", "H", "р", "Р" -> UpdateResult.from(new InfoMessage(this, HELP_INFO));

                // "q" exits the program
                case "q", "Q", "й", "Й" -> UpdateResult.from(this.quit(), QuitMessage::new);

                default -> UpdateResult.from(this);
            };
        }
        if (msg instanceof UpdateListMessage) {
            BandList = ctrl.handle("show", null).data();
        }
        if (msg instanceof DeleteBandMessage message) {
            try {
                var args = ctrl.getCommandModel(REMOVE_COMMAND);
                args.getFields().get(0).setRawValue(String.valueOf(message.getId()));
                ctrl.handle(REMOVE_COMMAND, args);
            } catch (Exception e) {
                log.error("Save error: ", e);
                error = true;
            }
            return UpdateResult.from(this, UpdateListMessage::new);
        }
        if (msg instanceof SetSaveFileMessage setSaveFileMessage) {
            file = setSaveFileMessage.getFile();
        }

        return UpdateResult.from(this);
    }

    private Model reverse() {
        ctrl.handle(REVERSE_COMMAND, null);
        return this;
    }

    private Model quit() {
        try {
            var ar = ctrl.getCommandModel(SAVE_COMMAND);
            ar.getFields().get(0).setRawValue(file);
            ctrl.handle(SAVE_COMMAND, ar);
        } catch (Exception e) {
            log.error("Quit error: ", e);
        }
        return this;
    }

    private Model delete() {
        if (BandList == null || cursor >= BandList.size()) return this;
        return new DeleteScreen(BandList.get(cursor), this);
    }

    private Model moveUp() {
        if (cursor - 1 < 0) {
            cursor = BandList.size() + extraFields.length - 1;
            return this;
        }
        cursor--;
        return this;
    }

    private Model moveDown() {
        if (cursor + 1 >= BandList.size() + extraFields.length) {
            cursor = 0;
            return this;
        }
        cursor++;
        return this;
    }

    private Model makeChoice() {
        for (int index = 0; index < BandList.size() ; index++) {
            if (index == cursor) {
                return new BandRedactor(ctrl, BandList.get(index), this);
            }
        }
        if (cursor == BandList.size()+ADD_BUTTON) {
            return new Add(this, ctrl);
        }
        return this;
    }

    private String showBandShort(MusicBand mb) {
        return String.format("ID: %d\nНазвание: %s", mb.getId(), mb.getName());
    }
    @Override
    public String view() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Все хранящиеся группы\n\n");

        if (BandList == null) {
            buffer.append("Список пуст");
            return buffer.toString();
        }

        for (int index = 0; index < BandList.size(); index++) {
            if (cursor == index) {
                buffer.append(SELECTION.render(showBandShort(BandList.get(index))));
            } else {
                buffer.append(showBandShort(BandList.get(index)));
            }
            buffer.append("\n");
        }

        for (int index = 0; index < extraFields.length; index++) {
            if (cursor == index + BandList.size()) {
                buffer.append(SELECTION.render(extraFields[index]));
                continue;
            }
            buffer.append(extraFields[index]);
        }
        buffer.append("\n(Нажмите q чтобы выйти, h - вывести информационную справку)\n");
        if (error) {
            buffer.append(WRONG.render("Произошла ошибка"));
        }

        return new ScrollView(buffer, cursor).toString();
    }
}