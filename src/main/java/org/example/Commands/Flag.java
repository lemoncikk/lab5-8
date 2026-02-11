package org.example.Commands;

import java.util.Optional;

public abstract class Flag {
    private final String shortName;
    private final String longName;
    private final String description;

    Flag(String name, String longName, String description) {
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
}
