package org.example.command.fields;

import org.example.command.exceptions.ValidationException;

public class StringField extends Field<String> {

    public StringField(String name, String description, boolean required, String defaultValue) {
        super(name, description, required, defaultValue, String.class);
    }
    public StringField(String name, String description, boolean required) {
        super(name, description, required, null, String.class);
    }
    public StringField(String name, String description) {
        super(name, description, false, null, String.class);
    }

    @Override
    protected String validate(String rawString) throws ValidationException {
        if (rawString == null || rawString.isEmpty()) return null;
        return rawString;
    }
}
