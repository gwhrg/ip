package kraken.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import kraken.exception.KrakenException;

/**
 * Utility helpers for parsing and formatting dates/times.
 * This class contains only static helpers.
 *
 * Supported user inputs:
 * - yyyy-MM-dd
 * - d/M/yyyy
 * - yyyy-MM-dd HHmm
 * - d/M/yyyy HHmm
 *
 * Storage uses ISO local date-time (e.g., 2019-12-02T18:00).
 */
public class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d uuuu HHmm");
    private static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter USER_DATE_ISO =
            DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter USER_DATE_SLASH =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter USER_DATE_TIME_ISO =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter USER_DATE_TIME_SLASH =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter[] USER_DATE_TIME_FORMATTERS = new DateTimeFormatter[] {
            USER_DATE_TIME_ISO,
            USER_DATE_TIME_SLASH
    };

    private static final DateTimeFormatter[] USER_DATE_FORMATTERS = new DateTimeFormatter[] {
            USER_DATE_ISO,
            USER_DATE_SLASH
    };

    /**
     * Parses a date/time from a user command string.
     *
     * <p>Rejects ISO date-time values containing {@code 'T'} (e.g., {@code 2019-12-02T18:00}).</p>
     *
     * @param raw raw user input
     * @return parsed date/time (date-only inputs default to start of day)
     * @throws KrakenException if the input is blank or cannot be parsed
     */
    public static LocalDateTime parseUserDateTime(String raw) throws KrakenException {
        String text = (raw == null) ? "" : raw.trim();
        if (text.isEmpty()) {
            throw new KrakenException("Date/time cannot be empty. "
                    + "Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm (e.g., 2019-12-02 1800).");
        }

        LocalDateTime dateTime = tryParseLocalDateTime(text, USER_DATE_TIME_FORMATTERS);
        if (dateTime != null) {
            return dateTime;
        }

        LocalDate date = tryParseLocalDate(text, USER_DATE_FORMATTERS);
        if (date != null) {
            return date.atStartOfDay();
        }

        throw new KrakenException("Invalid date/time: '" + text + "'. "
                + "Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm (e.g., 2019-12-02 1800).");
    }

    /**
     * Parses a date from a user command.
     *
     * Accepts yyyy-MM-dd or d/M/yyyy. Also accepts user date-time formats and uses the date portion.
     *
     * @param raw raw user input
     * @return parsed date
     * @throws KrakenException if the input is blank or cannot be parsed
     */
    public static LocalDate parseUserDate(String raw) throws KrakenException {
        String text = (raw == null) ? "" : raw.trim();
        if (text.isEmpty()) {
            throw new KrakenException("Date cannot be empty. Use yyyy-MM-dd or d/M/yyyy (e.g., 2019-12-02).");
        }

        LocalDate date = tryParseLocalDate(text, USER_DATE_FORMATTERS);
        if (date != null) {
            return date;
        }

        // Allow users to provide a date-time in the supported user formats; we use the date portion.
        try {
            return parseUserDateTime(text).toLocalDate();
        } catch (KrakenException e) {
            throw new KrakenException("Invalid date: '" + text + "'. Use yyyy-MM-dd or d/M/yyyy (e.g., 2019-12-02).");
        }
    }

    /**
     * Parses date/time from storage (ISO local date-time, e.g., 2019-12-02T18:00).
     *
     * @param raw raw stored date/time value
     * @return parsed date/time
     * @throws KrakenException if the value is blank or invalid
     */
    public static LocalDateTime parseStorageDateTime(String raw) throws KrakenException {
        String text = (raw == null) ? "" : raw.trim();
        if (text.isEmpty()) {
            throw new KrakenException("Stored date/time cannot be empty.");
        }

        try {
            return LocalDateTime.parse(text, STORAGE_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new KrakenException("Invalid stored date/time: '" + text + "'.");
        }
    }

    /**
     * Formats a date/time for user-facing display.
     *
     * <p>If the time is exactly midnight, only the date portion is shown.</p>
     *
     * @param dateTime date/time to format
     * @return formatted display string, or an empty string if {@code dateTime} is {@code null}
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        if (isMidnight(dateTime)) {
            return dateTime.toLocalDate().format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    /**
     * Formats a date/time for persistence.
     *
     * @param dateTime date/time to format
     * @return ISO local date-time string, or an empty string if {@code dateTime} is {@code null}
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(STORAGE_DATE_TIME);
    }

    /**
     * Returns whether the given date/time is exactly midnight (00:00:00.000000000).
     *
     * @param dateTime date/time to check
     * @return {@code true} if {@code dateTime} is midnight
     */
    public static boolean isMidnight(LocalDateTime dateTime) {
        return dateTime != null
                && dateTime.getHour() == 0
                && dateTime.getMinute() == 0
                && dateTime.getSecond() == 0
                && dateTime.getNano() == 0;
    }

    /**
     * Tries to parse the given text as a {@link LocalDateTime} using the provided formatters.
     *
     * @param text input to parse
     * @param formatters candidate formatters to try in order
     * @return parsed date/time, or {@code null} if parsing fails for all formatters
     */
    private static LocalDateTime tryParseLocalDateTime(String text, DateTimeFormatter[] formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }
        return null;
    }

    /**
     * Tries to parse the given text as a {@link LocalDate} using the provided formatters.
     *
     * @param text input to parse
     * @param formatters candidate formatters to try in order
     * @return parsed date, or {@code null} if parsing fails for all formatters
     */
    private static LocalDate tryParseLocalDate(String text, DateTimeFormatter[] formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }
        return null;
    }
}

