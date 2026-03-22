package org.example.command.fields;

import org.example.exceptions.ValidationException;

import java.io.Serial;
import java.io.Serializable;

public class StringField extends Field<String> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
    @Override
    public String formatAllowedValues() {
        return "Пу пу пу";
    }
}
