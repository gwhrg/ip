package kraken.command;

import java.time.LocalDateTime;

import kraken.storage.Storage;
import kraken.task.Deadline;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

public class DeadlineCommand extends Command {
    private final String description;
    private final LocalDateTime by;

    public DeadlineCommand(String description, LocalDateTime by) {
        this.description = description;
        this.by = by;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task newTask = new Deadline(description, by);
        tasks.add(newTask);
        storage.save(tasks.asList());
        ui.showTaskAdded(newTask, tasks.size());
    }
}

