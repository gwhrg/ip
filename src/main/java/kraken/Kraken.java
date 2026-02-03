package kraken;

import kraken.command.Command;
import kraken.exception.KrakenException;
import kraken.parser.Parser;
import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.Ui;

public class Kraken {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    public Kraken() {
        this.ui = new Ui();
        this.storage = new Storage();
        this.tasks = new TaskList(storage.load());
    }

    public static void main(String[] args) {
        new Kraken().run();
    }

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

