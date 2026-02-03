package kraken;

import kraken.command.Command;
import kraken.exception.KrakenException;
import kraken.parser.Parser;
import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Entry point for the Kraken task manager chatbot.
 *
 * <p>The application reads commands from standard input, parses them into {@link Command} objects,
 * executes them, and persists task changes via {@link Storage}.</p>
 */
public class Kraken {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Constructs a {@code Kraken} instance with the default UI and storage.
     *
     * <p>Tasks are loaded from disk on startup.</p>
     */
    public Kraken() {
        this.ui = new Ui();
        this.storage = new Storage();
        this.tasks = new TaskList(storage.load());
    }

    /**
     * Launches Kraken.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new Kraken().run();
    }

    /**
     * Runs the main command-processing loop.
     *
     * <p>Shows the welcome message, then repeatedly reads a command, executes it, and prints a
     * separator line. The loop terminates when an exit command is executed or when there is no more
     * input.</p>
     */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            try {
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (KrakenException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }
}

