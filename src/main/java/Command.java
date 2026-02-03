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

