package eu.hlavki.identity.services.push.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import java.time.format.DateTimeParseException;
import org.slf4j.LoggerFactory;

final class XMLDateTimeAdapter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(XMLDateTimeAdapter.class);

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

    public static LocalDateTime parseDateTime(String s) {
        try {
            return LocalDateTime.parse(s, ISO_INSTANT);
        } catch (DateTimeParseException e) {
            log.warn("Invalid ISO date-time format {}", s);
            return null;
        }
    }

    public static String printDateTime(LocalDateTime time) {
        return time.format(ISO_INSTANT);
    }
}
