package org.example.Commands.Fields;

import org.example.Commands.exceptions.ValidationException;

public class StringField extends Field<String> {

    public StringField(String name, String description, boolean required, String defaultValue) {
        super(name, description, required, defaultValue, String.class);
    }
    public StringField(String name, String description, boolean required) {
        super(name, description, required, null, String.class);
    }

    @Override
    protected String validate(String rawString) throws ValidationException {
        return rawString;
    }
}
