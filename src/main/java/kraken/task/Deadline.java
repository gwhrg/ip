package kraken.task;

import java.time.LocalDateTime;

import kraken.util.DateTimeUtil;

/**
 * Represents a task that must be done by a specific date/time.
 */
public class Deadline extends Task {

    protected LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description task description
     * @param by due date/time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date/time of this deadline.
     *
     * @return due date/time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the display string for this deadline.
     *
     * @return a user-facing string prefixed with {@code [D]} and the formatted {@code by} date/time
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeUtil.formatForDisplay(by) + ")";
    }
}
