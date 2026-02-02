import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Utility helpers for parsing and formatting dates/times.
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
     * Parses date/time from a user command (rejects ISO date-time with 'T', e.g., 2019-12-02T18:00).
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

    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        if (isMidnight(dateTime)) {
            return dateTime.toLocalDate().format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME);
    }

    public static String formatForStorage(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(STORAGE_DATE_TIME);
    }

    public static boolean isMidnight(LocalDateTime dateTime) {
        return dateTime != null
                && dateTime.getHour() == 0
                && dateTime.getMinute() == 0
                && dateTime.getSecond() == 0
                && dateTime.getNano() == 0;
    }

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

