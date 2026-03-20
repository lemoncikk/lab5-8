package org.example.command;

import java.io.Serial;
import java.io.Serializable;

public class Flag implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String shortName;
    private final String longName;
    private final String description;
    private boolean on = false;

    public Flag(String name, String longName, String description) {
        this.shortName = name;
        this.longName = longName;
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public String getLongName() {
        return longName;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
