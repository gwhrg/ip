package kraken.command;

import java.time.LocalDateTime;

import kraken.storage.Storage;
import kraken.task.Deadline;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Adds a new {@link Deadline} task to the task list.
 *
 * <p>This command persists the updated task list and prints a confirmation message.</p>
 */
public class DeadlineCommand extends Command {
    private final String description;
    private final LocalDateTime by;

    /**
     * Creates a command that adds a deadline task.
     *
     * @param description deadline description
     * @param by due date/time
     */
    public DeadlineCommand(String description, LocalDateTime by) {
        this.description = description;
        this.by = by;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Creates a new {@link Deadline}, appends it to the task list, persists the list, and
     * reports the updated task count through the UI.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task newTask = new Deadline(description, by);
        tasks.add(newTask);
        storage.save(tasks.asList());
        ui.showTaskAdded(newTask, tasks.size());
    }
}
