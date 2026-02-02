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

    public static LocalDateTime parseDateTime(String raw) throws KrakenException {
        String text = (raw == null) ? "" : raw.trim();
        if (text.isEmpty()) {
            throw new KrakenException("Date/time cannot be empty. "
                    + "Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm (e.g., 2019-12-02 1800).");
        }

        // 1) Storage-friendly ISO format (and also acceptable as user input)
        try {
            return LocalDateTime.parse(text, STORAGE_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // try other formats below
        }

        // 2) User date + time formats (space-separated)
        try {
            return LocalDateTime.parse(text, USER_DATE_TIME_ISO);
        } catch (DateTimeParseException ignored) {
            // try next
        }
        try {
            return LocalDateTime.parse(text, USER_DATE_TIME_SLASH);
        } catch (DateTimeParseException ignored) {
            // try date-only formats below
        }

        // 3) Date-only formats (assume start-of-day)
        LocalDate date = tryParseDateOnly(text);
        if (date != null) {
            return date.atStartOfDay();
        }

        throw new KrakenException("Invalid date/time: '" + text + "'. "
                + "Use yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm (e.g., 2019-12-02 1800).");
    }

    public static LocalDate parseDate(String raw) throws KrakenException {
        String text = (raw == null) ? "" : raw.trim();
        if (text.isEmpty()) {
            throw new KrakenException("Date cannot be empty. Use yyyy-MM-dd or d/M/yyyy (e.g., 2019-12-02).");
        }

        LocalDate date = tryParseDateOnly(text);
        if (date != null) {
            return date;
        }

        // Allow users to provide a date-time; we use the date portion.
        try {
            return parseDateTime(text).toLocalDate();
        } catch (KrakenException e) {
            throw new KrakenException("Invalid date: '" + text + "'. Use yyyy-MM-dd or d/M/yyyy (e.g., 2019-12-02).");
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

    private static LocalDate tryParseDateOnly(String text) {
        try {
            return LocalDate.parse(text, USER_DATE_ISO);
        } catch (DateTimeParseException ignored) {
            // try next
        }
        try {
            return LocalDate.parse(text, USER_DATE_SLASH);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}

