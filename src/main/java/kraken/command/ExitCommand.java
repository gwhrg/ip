package kraken.command;

import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.Ui;

public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}

