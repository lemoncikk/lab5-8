package org.example.command.fields;

import org.example.exceptions.ValidationException;

import java.io.Serial;
import java.io.Serializable;

public class BoolField extends Field<Boolean> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected BoolField(String name, String description, boolean required, Boolean defaultValue) {
        super(name, description, required, defaultValue, Boolean.class);
    }
    protected BoolField(String name, String description, boolean required) {
        super(name, description, required, false, Boolean.class);
    }

    public String formatAllowedValues() {
        return "true/false, yes/no, 1/0, on/off, да/нет, д/н";
    }

    @Override
    protected Boolean validate(String rawString) throws ValidationException {
        String normalized = rawString.trim().toLowerCase();
        if (normalized.equals("true") || normalized.equals("yes") || normalized.equals("1") || normalized.equals("on")) {
            return true;
        }
        if (normalized.equals("false") || normalized.equals("no") || normalized.equals("0") || normalized.equals("off")) {
            return false;
        }
        throw new ValidationException(
                String.format("Field '%s' must be a boolean value (%s), got: '%s'",
                        formatAllowedValues(), getName(), rawString));
    }
}
