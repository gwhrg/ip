package kraken;

import kraken.command.Command;
import kraken.exception.KrakenException;
import kraken.parser.Parser;
import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.GuiUi;

/**
 * The Kraken task manager chatbot engine.
 */
public class Kraken {
    private final Storage storage;
    private final TaskList tasks;
    private boolean shouldExit;

    /**
     * Constructs a {@code Kraken} instance with default storage.
     *
     * <p>Tasks are loaded from disk on startup.</p>
     */
    public Kraken() {
        this.storage = new Storage();
        this.tasks = new TaskList(storage.load());
        this.shouldExit = false;
    }

    /**
     * Returns the welcome message shown at startup.
     */
    public String getWelcomeMessage() {
        GuiUi ui = new GuiUi();
        ui.showWelcome();
        return ui.consumeOutput();
    }

    /**
     * Generates a response for the user's input.
     *
     * @param input user input line
     * @return Kraken's response text
     */
    public String getResponse(String input) {
        GuiUi ui = new GuiUi();
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            shouldExit = command.isExit();
        } catch (KrakenException e) {
            shouldExit = false;
            ui.showError(e.getMessage());
        }
        return ui.consumeOutput();
    }

    /**
     * Whether the application should exit after the last processed input.
     */
    public boolean isExit() {
        return shouldExit;
    }
}
