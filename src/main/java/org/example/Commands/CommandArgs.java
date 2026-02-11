package org.example.Commands;

import java.util.ArrayList;

public class CommandArgs {
    private final ArrayList<Field<?>> fields = new ArrayList<>();
    private final ArrayList<Flag> flags = new ArrayList<>();

    public void addField(Field<?> field) {
        fields.add(field);
    }
    public void addFlag(Flag flag) {
        flags.add(flag);
    }

    public ArrayList<Field<?>> getFields() {
        return fields;
    }

    public ArrayList<Flag> getFlags() {
        return flags;
    }
}
