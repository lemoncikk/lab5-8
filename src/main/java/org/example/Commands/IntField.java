package org.example.Commands;

import org.example.Commands.exceptions.ValidationException;

public class IntField extends Field<Integer> {
    public IntField(String name, String description, boolean required, Integer defaultValue) {
        super(name, description, required, defaultValue, Integer.class);
    }

    @Override
    protected Integer validate(String rawString) throws ValidationException {
        try {
            return Integer.parseInt(rawString);
        }catch (NumberFormatException _) {
            throw new ValidationException("Expected integer number");
        }
    }
}
