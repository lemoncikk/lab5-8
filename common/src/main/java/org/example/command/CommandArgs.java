package org.example.command;

import org.example.command.fields.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class CommandArgs implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final ArrayList<Field<?>> fields = new ArrayList<>();
    private final ArrayList<Flag> flags = new ArrayList<>();

    public CommandArgs addField(Field<?> field) {
        fields.add(field);
        return this;
    }
    public CommandArgs addFlag(Flag flag) {
        flags.add(flag);
        return this;
    }

    public ArrayList<Field<?>> getFields() {
        return fields;
    }

    public ArrayList<Flag> getFlags() {
        return flags;
    }
}
