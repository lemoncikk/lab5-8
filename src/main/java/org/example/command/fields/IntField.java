package org.example.command.fields;

import org.example.command.exceptions.ValidationException;

public class IntField extends Field<Integer> {
    public IntField(String name, String description, boolean required, Integer defaultValue) {
        super(name, description, required, defaultValue, Integer.class);
    }
    public IntField(String name, String description, boolean required) {
        super(name, description, required, null, Integer.class);
    }

    @Override
    protected Integer validate(String rawString) throws ValidationException {
        if (rawString == null || rawString.isEmpty()) return null;
        try {
            return Integer.parseInt(rawString);
        }catch (NumberFormatException _) {
            throw new ValidationException(String.format("Field '%s' must be integer, got: '%s'", getName(), rawString));
        }
    }
}
