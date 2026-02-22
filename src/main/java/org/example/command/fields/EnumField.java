package org.example.command.fields;

import org.example.command.exceptions.ValidationException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumField<T extends Enum<T>> extends Field<T> {

    public EnumField(String name, String description, boolean required, T defaultValue, Class<T> type) {
        super(name, description, required, defaultValue, type);
    }
    public EnumField(String name, String description, boolean required, Class<T> type) {
        super(name, description, required, null, type);
    }

    public String formatAllowedValues() {
        return Arrays.stream(getType().getEnumConstants()).toList().stream()
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
    }

    @Override
    protected T validate(String rawString) throws ValidationException {
        if (rawString == null || rawString.isEmpty()) return null;
        try {
            String norm = rawString.trim().toUpperCase();
            return Enum.valueOf(getType(), norm);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    String.format("Invalid value %s for field '%s'. Allowed values: %s",
                            rawString, getName(), formatAllowedValues()));
        }
    }
}
