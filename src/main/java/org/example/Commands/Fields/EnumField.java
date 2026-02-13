package org.example.Commands.Fields;

import org.example.Commands.exceptions.ValidationException;

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
        try {
            String norm = rawString.trim().toUpperCase();
            return Enum.valueOf(getType(), norm);
        } catch (IllegalArgumentException _) {
            throw new ValidationException(
                    String.format("Invalid value %s for field '%s'. Allowed values: %s",
                            rawString, getName(), formatAllowedValues()));
        }
    }
}
