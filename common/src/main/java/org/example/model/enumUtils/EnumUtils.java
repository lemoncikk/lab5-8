package org.example.model.enumUtils;

import java.util.Arrays;
import java.util.Optional;

public class EnumUtils {
    public static Optional<LabelledEnum> fromLabel(Class<? extends Enum<?>> enumType, String label) {
        if (label == null || label.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedInput = label.trim().toLowerCase();

        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> e instanceof LabelledEnum)
                .map(e -> (LabelledEnum) e)
                .filter(e -> e.getLabel().toLowerCase().equals(normalizedInput))
                .findFirst();
    }
}
