package org.example.model;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public ZonedDateTime unmarshal(String v) throws Exception {
        return ZonedDateTime.parse(v, FORMATTER);
    }

    @Override
    public String marshal(ZonedDateTime v) throws Exception {
        return v.format(FORMATTER);
    }
}
