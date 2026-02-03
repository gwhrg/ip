package kraken.task;

/**
 * Represents a to-do task without any date/time fields.
 */
public class Todo extends Task {
    /**
     * Creates a {@code Todo} with the given description.
     *
     * @param description task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the display string for this todo.
     *
     * @return a user-facing string prefixed with {@code [T]}
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

