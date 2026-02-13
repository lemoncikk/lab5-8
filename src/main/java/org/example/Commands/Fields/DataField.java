package org.example.Commands.Fields;

import org.example.Commands.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DataField extends Field<ZonedDateTime> {
    public DataField(String name, String description, boolean required, ZonedDateTime defaultValue) {
        super(name, description, required, defaultValue, ZonedDateTime.class);
    }

    public DataField(String name, String description, boolean required) {
        super(name, description, required, null, ZonedDateTime.class);
    }
    public String formatDataTimeFormat() {
        return "YYYY-MM-DD, например 2006-06-17";
    }

    @Override
    protected ZonedDateTime validate(String rawString) throws ValidationException {
        try {
            LocalDate date = LocalDate.parse(rawString.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay(ZoneId.systemDefault());
        }catch (DateTimeParseException e) {
            throw new ValidationException(
                    String.format("Field '%s' must be in format %s, got: '%s'",
                            formatDataTimeFormat(), getName(), rawString));
        }

    }
}
