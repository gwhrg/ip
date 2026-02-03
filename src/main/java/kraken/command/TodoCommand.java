package kraken.command;

import kraken.storage.Storage;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.task.Todo;
import kraken.ui.Ui;

/**
 * Adds a new {@link Todo} task to the task list.
 *
 * <p>This command persists the updated task list and prints a confirmation message.</p>
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a command that adds a todo with the given description.
     *
     * @param description todo description
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Creates a new {@link Todo}, appends it to the task list, persists the list, and reports
     * the updated task count through the UI.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task newTask = new Todo(description);
        tasks.add(newTask);
        storage.save(tasks.asList());
        ui.showTaskAdded(newTask, tasks.size());
    }
}
