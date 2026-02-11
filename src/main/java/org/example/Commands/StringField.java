package org.example.Commands;

import org.example.Commands.exceptions.ValidationException;

public class StringField extends Field<String> {

    public StringField(String name, String description, boolean required, String defaultValue) {
        super(name, description, required, defaultValue, String.class);
    }

    @Override
    protected String validate(String rawString) throws ValidationException {
        return rawString;
    }
}
