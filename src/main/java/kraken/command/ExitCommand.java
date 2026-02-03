package kraken.command;

import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Terminates the application.
 */
public class ExitCommand extends Command {
    /**
     * {@inheritDoc}
     *
     * <p>Prints the farewell message.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
