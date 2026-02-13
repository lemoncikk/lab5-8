package org.example.Commands;

import org.example.Commands.Fields.Field;

import java.util.ArrayList;

public class CommandArgs {
    private final ArrayList<Field<?>> fields = new ArrayList<>();
    private final ArrayList<Flag> flags = new ArrayList<>();

    public CommandArgs addField(Field<?> field) {
        fields.add(field);
        return this;
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
