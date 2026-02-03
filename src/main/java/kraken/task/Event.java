package kraken.task;

import java.time.LocalDateTime;

import kraken.util.DateTimeUtil;

/**
 * Represents an event task that occurs over a start/end date-time range.
 */
public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description task description
     * @param from start date/time
     * @param to end date/time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date/time of this event.
     *
     * @return start date/time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end date/time of this event.
     *
     * @return end date/time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the display string for this event.
     *
     * @return a user-facing string prefixed with {@code [E]} and the formatted time range
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimeUtil.formatForDisplay(from)
                + " to: " + DateTimeUtil.formatForDisplay(to) + ")";
    }
}

