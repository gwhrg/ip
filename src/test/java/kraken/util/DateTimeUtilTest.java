package kraken.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import kraken.exception.KrakenException;

public class DateTimeUtilTest {
    @Test
    public void parseUserDateTime_null_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> DateTimeUtil.parseUserDateTime(null));
        assertTrue(e.getMessage().contains("cannot be empty"), e.getMessage());
    }

    @Test
    public void parseUserDateTime_blank_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> DateTimeUtil.parseUserDateTime("   "));
        assertTrue(e.getMessage().contains("cannot be empty"), e.getMessage());
    }

    @Test
    public void parseUserDateTime_isoDateOnly_returnsStartOfDay() throws KrakenException {
        LocalDateTime actual = DateTimeUtil.parseUserDateTime("2019-12-02");
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), actual);
    }

    @Test
    public void parseUserDateTime_slashDateOnly_returnsStartOfDay() throws KrakenException {
        LocalDateTime actual = DateTimeUtil.parseUserDateTime("2/12/2019");
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), actual);
    }

    @Test
    public void parseUserDateTime_isoDateTime_parsesHourMinute() throws KrakenException {
        LocalDateTime actual = DateTimeUtil.parseUserDateTime("2019-12-02 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), actual);
    }

    @Test
    public void parseUserDateTime_slashDateTime_parsesHourMinute() throws KrakenException {
        LocalDateTime actual = DateTimeUtil.parseUserDateTime("2/12/2019 0900");
        assertEquals(LocalDateTime.of(2019, 12, 2, 9, 0), actual);
    }

    @Test
    public void parseUserDateTime_trimsWhitespace() throws KrakenException {
        LocalDateTime actual = DateTimeUtil.parseUserDateTime("   2019-12-02 1800   ");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), actual);
    }

    @Test
    public void parseUserDateTime_invalidDate_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> DateTimeUtil.parseUserDateTime("2019-02-29"));
        assertTrue(e.getMessage().contains("Invalid date/time"), e.getMessage());
    }

    @Test
    public void parseUserDateTime_invalidTime_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> DateTimeUtil.parseUserDateTime("2019-12-02 2460"));
        assertTrue(e.getMessage().contains("Invalid date/time"), e.getMessage());
    }

    @Test
    public void parseUserDateTime_isoWithT_rejected() {
        KrakenException e = assertThrows(KrakenException.class, () -> DateTimeUtil.parseUserDateTime("2019-12-02T18:00"));
        assertTrue(e.getMessage().contains("Invalid date/time"), e.getMessage());
    }
}

