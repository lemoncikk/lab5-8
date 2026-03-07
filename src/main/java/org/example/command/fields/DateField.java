package org.example.command.fields;

import org.example.command.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateField extends Field<ZonedDateTime> {
    public DateField(String name, String description, boolean required, ZonedDateTime defaultValue) {
        super(name, description, required, defaultValue, ZonedDateTime.class);
    }

    public DateField(String name, String description, boolean required) {
        super(name, description, required, null, ZonedDateTime.class);
    }
    public String formatAllowedValues() {
        return "YYYY-MM-DD, например 2006-06-17";
    }

    @Override
    protected ZonedDateTime validate(String rawString) throws ValidationException {
        if (rawString == null || rawString.isEmpty()) return null;
        try {
            LocalDate date = LocalDate.parse(rawString.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay(ZoneId.systemDefault());
        }catch (DateTimeParseException e) {
            throw new ValidationException(
                    String.format("Field '%s' must be in format %s, got: '%s'",
                            formatAllowedValues(), getName(), rawString));
        }

    }
}
