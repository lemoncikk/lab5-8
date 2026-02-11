package org.example.Commands;

import org.example.Commands.exceptions.ValidationException;

public class BoolField extends Field<Boolean> {

    protected BoolField(String name, String description, boolean required, Boolean defaultValue) {
        super(name, description, required, defaultValue, Boolean.class);
    }

    @Override
    protected Boolean validate(String rawString) throws ValidationException {
        return null;
    }
}
