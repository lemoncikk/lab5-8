package org.example.tui;

import com.williamcallahan.tui4j.compat.bubbletea.Message;

public class DeleteBandMessage implements Message {
    private final int id;

    public DeleteBandMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
