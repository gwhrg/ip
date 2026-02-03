package kraken.command;

import kraken.exception.KrakenException;
import kraken.storage.Storage;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

public class DeleteCommand extends Command {
    private final int taskIndex;

    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KrakenException {
        Task removedTask = tasks.removeTaskOrThrow(taskIndex);
        storage.save(tasks.asList());
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}

