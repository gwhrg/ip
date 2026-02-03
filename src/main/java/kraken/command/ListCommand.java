package kraken.command;

import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Displays the current task list.
 */
public class ListCommand extends Command {
    /**
     * {@inheritDoc}
     *
     * <p>This command does not modify tasks or persist anything; it only prints the list.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}

