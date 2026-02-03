package kraken.task;

/**
 * Represents a basic task with a description and completion state.
 *
 * <p>Subclasses can extend this type to add additional fields (e.g., due dates or time ranges).</p>
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description task description shown to the user
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return {@code true} if completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the icon used in string representations of this task.
     *
     * @return {@code "X"} if done, otherwise a single space {@code " "}
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns a display string for this task.
     *
     * <p>The base implementation produces a string in the form {@code "[<status>] <description>"}.
     * Subclasses typically prefix their own type indicator (e.g., {@code [T]}, {@code [D]}).</p>
     *
     * @return a user-facing task string
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}

