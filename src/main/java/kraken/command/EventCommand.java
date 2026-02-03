package kraken.command;

import java.time.LocalDateTime;

import kraken.storage.Storage;
import kraken.task.Event;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Adds a new {@link Event} task to the task list.
 *
 * <p>This command persists the updated task list and prints a confirmation message.</p>
 */
public class EventCommand extends Command {
    private final String description;
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates a command that adds an event task.
     *
     * @param description event description
     * @param from start date/time
     * @param to end date/time
     */
    public EventCommand(String description, LocalDateTime from, LocalDateTime to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Creates a new {@link Event}, appends it to the task list, persists the list, and reports
     * the updated task count through the UI.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
        storage.save(tasks.asList());
        ui.showTaskAdded(newTask, tasks.size());
    }
}

