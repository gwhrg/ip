package kraken.command;

import kraken.exception.KrakenException;
import kraken.storage.Storage;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Deletes a task by its 0-based index in the task list.
 *
 * <p>This command persists the updated task list and prints a confirmation message.</p>
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the given index.
     *
     * @param taskIndex 0-based index of the task to delete
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Removes the task, persists the list, and reports the updated task count through the UI.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KrakenException {
        Task removedTask = tasks.removeTaskOrThrow(taskIndex);
        storage.save(tasks.asList());
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
