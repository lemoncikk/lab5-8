package org.example.command.fields;

import org.example.exceptions.ValidationException;

public class LongField extends Field<Long> {

    public LongField(String name, String description, boolean required, Long defaultValue) {
        super(name, description, required, defaultValue, Long.class);
    }
    public LongField(String name, String description, boolean required) {
        super(name, description, required, null, Long.class);
    }

    @Override
    protected Long validate(String rawString) throws ValidationException {
        if (rawString == null || rawString.isEmpty()) return null;
        try {
            return Long.parseLong(rawString);
        }catch (NumberFormatException e) {
            throw new ValidationException(String.format("Field '%s' must be integer, got: '%s'", getName(), rawString));
        }
    }
    @Override
    public String formatAllowedValues() {
        return "1";
    }
}
