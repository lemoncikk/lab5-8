package org.example.tui;

import com.williamcallahan.tui4j.compat.bubbletea.Message;

public class SetSaveFileMessage implements Message {
    private final String file;
    public SetSaveFileMessage(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }
}
