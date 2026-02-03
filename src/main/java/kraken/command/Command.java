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
     * Executes this command.
     *
     * @param tasks task list to operate on
     * @param ui UI used for user-facing output
     * @param storage storage used for persistence (commands may call {@link Storage#save})
     * @throws KrakenException if the command cannot be executed (e.g., invalid task index)
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws KrakenException;

    /**
     * Whether this command should terminate the application.
     *
     * @return {@code true} if the application should exit after executing this command
     */
    public boolean isExit() {
        return false;
    }
}

