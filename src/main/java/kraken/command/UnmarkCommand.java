package kraken.command;

import kraken.exception.KrakenException;
import kraken.storage.Storage;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Marks a task as not done by its 0-based index in the task list.
 *
 * <p>This command persists the updated task list and prints a confirmation message.</p>
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given index as not done.
     *
     * @param taskIndex 0-based index of the task to unmark
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves the task, marks it not done, persists the list, and reports the change through
     * the UI.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KrakenException {
        Task task = tasks.getTaskOrThrow(taskIndex);
        task.markAsNotDone();
        storage.save(tasks.asList());
        ui.showTaskUnmarked(task);
    }
}

