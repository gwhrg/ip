import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the task list and operations on it.
 */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(Objects.requireNonNull(tasks));
    }

    public int size() {
        return tasks.size();
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public Task getTaskOrThrow(int index) throws KrakenException {
        if (index < 0 || index >= tasks.size()) {
            throw new KrakenException("Task number " + (index + 1) + " does not exist. "
                    + "You have " + tasks.size() + " task(s) in your list.");
        }
        return tasks.get(index);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task removeTaskOrThrow(int index) throws KrakenException {
        getTaskOrThrow(index);
        return tasks.remove(index);
    }

    /**
     * Returns the underlying list for persistence.
     */
    public List<Task> asList() {
        return tasks;
    }
}

