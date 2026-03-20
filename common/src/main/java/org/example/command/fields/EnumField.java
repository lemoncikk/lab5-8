package org.example.command.fields;

import org.example.exceptions.ValidationException;
import org.example.model.enumUtils.EnumUtils;
import org.example.model.enumUtils.LabelledEnum;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnumField<T extends Enum<T>> extends Field<T> {

    public EnumField(String name, String description, boolean required, T defaultValue, Class<T> type) {
        super(name, description, required, defaultValue, type);
    }
    public EnumField(String name, String description, boolean required, Class<T> type) {
        super(name, description, required, null, type);
    }

    public String formatAllowedValues() {
        return Arrays.stream(getType().getEnumConstants())
                .map(enumConst -> {
                    String name = enumConst.name().toLowerCase();
                    if (enumConst instanceof LabelledEnum labelled) {
                        return String.format("%s (или \"%s\")", name, labelled.getLabel());
                    }
                    return name;
                })
                .collect(Collectors.joining(", "));
    }

    @Override
    protected T validate(String rawString) throws ValidationException {
        if (rawString == null || rawString.isEmpty()) return null;
        try {
            String norm = rawString.trim().toUpperCase();
            return Enum.valueOf(getType(), norm);
        } catch (IllegalArgumentException e) {

        }
        if (LabelledEnum.class.isAssignableFrom(getType())) {
            Optional<LabelledEnum> byLabel = EnumUtils.fromLabel(getType(), rawString.trim());
            if (byLabel.isPresent()) {
                return (T) byLabel.get();
            }
        }

        throw new ValidationException(
                String.format("Invalid value %s for field '%s'. Allowed values: %s",
                        rawString, getName(), formatAllowedValues()));
    }
}
