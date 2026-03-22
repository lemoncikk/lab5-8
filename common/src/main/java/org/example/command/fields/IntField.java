package org.example.command.fields;

import org.example.exceptions.ValidationException;

import java.io.Serial;
import java.io.Serializable;

public class IntField extends Field<Integer> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
        }catch (NumberFormatException e) {
            throw new ValidationException(String.format("Field '%s' must be integer, got: '%s'", getName(), rawString));
        }
    }

    @Override
    public String formatAllowedValues() {
        return "1";
    }
}
