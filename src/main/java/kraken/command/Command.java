package kraken.command;

import kraken.exception.KrakenException;
import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Executes the command.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws KrakenException;

    /**
     * Whether this command should terminate the application.
     */
    public boolean isExit() {
        return false;
    }
}

