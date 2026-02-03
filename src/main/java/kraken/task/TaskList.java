package kraken.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kraken.exception.KrakenException;

/**
 * Encapsulates the task list and operations on it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list initialized with the given tasks.
     *
     * <p>The provided list is copied into an internal mutable list.</p>
     *
     * @param tasks initial tasks to populate the list with
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(Objects.requireNonNull(tasks));
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given 0-based index.
     *
     * @param index 0-based index
     * @return the task at {@code index}
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the task at the given 0-based index, throwing a user-friendly exception if invalid.
     *
     * @param index 0-based index
     * @return the task at {@code index}
     * @throws KrakenException if {@code index} is out of bounds
     */
    public Task getTaskOrThrow(int index) throws KrakenException {
        if (index < 0 || index >= tasks.size()) {
            throw new KrakenException("Task number " + (index + 1) + " does not exist. "
                    + "You have " + tasks.size() + " task(s) in your list.");
        }
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 0-based index.
     *
     * @param index 0-based index
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Removes and returns the task at the given 0-based index, throwing a user-friendly exception if invalid.
     *
     * @param index 0-based index
     * @return the removed task
     * @throws KrakenException if {@code index} is out of bounds
     */
    public Task removeTaskOrThrow(int index) throws KrakenException {
        getTaskOrThrow(index);
        return tasks.remove(index);
    }

    /**
     * Returns the underlying list for persistence.
     *
     * <p>This returns the internal mutable list; modifications will affect this {@code TaskList}.</p>
     *
     * @return the underlying task list
     */
    public List<Task> asList() {
        return tasks;
    }
}

