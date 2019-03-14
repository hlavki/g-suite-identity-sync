package eu.hlavki.identity.services.push.config.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class XMLDateTimeAdapter {

    private static final Logger log = LoggerFactory.getLogger(XMLDateTimeAdapter.class);

    private final static DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());

    private XMLDateTimeAdapter() {
    }

    public static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s, ISO_DATE);
        } catch (DateTimeParseException e) {
            log.warn("Invalid ISO date format {}", s);
            return null;
        }
    }

    public static String printDate(LocalDate date) {
        return date.format(ISO_DATE);
    }

    public static ZonedDateTime parseDateTime(String s) {
        try {
            return ZonedDateTime.parse(s, FORMAT);
        } catch (DateTimeParseException e) {
            log.warn("Invalid ISO date-time format {}", s);
            return null;
        }
    }

    public static String printDateTime(ZonedDateTime time) {
        return time.format(ISO_INSTANT);
    }
}
