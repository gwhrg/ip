package kraken.command;

import kraken.exception.KrakenException;
import kraken.storage.Storage;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

public class UnmarkCommand extends Command {
    private final int taskIndex;

    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KrakenException {
        Task task = tasks.getTaskOrThrow(taskIndex);
        task.markAsNotDone();
        storage.save(tasks.asList());
        ui.showTaskUnmarked(task);
    }
}

