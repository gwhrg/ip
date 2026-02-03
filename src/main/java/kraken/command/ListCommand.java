package kraken.command;

import kraken.storage.Storage;
import kraken.task.TaskList;
import kraken.ui.Ui;

public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}

