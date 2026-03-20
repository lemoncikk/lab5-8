package org.example.command;

import org.example.model.MusicBand;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public record CommandResult(String msg, ArrayList<MusicBand> data, boolean stopFlag) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
