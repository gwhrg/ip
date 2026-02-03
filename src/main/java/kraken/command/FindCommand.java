package kraken.command;

import java.util.Locale;

import kraken.storage.Storage;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;

/**
 * Finds tasks whose descriptions contain a given keyword.
 *
 * <p>This command does not modify the task list and does not persist anything.</p>
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword keyword to search for (expected to be non-blank)
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Prints a header and then prints all tasks whose descriptions contain the keyword.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasksHeader();

        String needle = keyword.toLowerCase(Locale.ROOT);
        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String description = task.getDescription();
            if (description != null && description.toLowerCase(Locale.ROOT).contains(needle)) {
                ui.showTaskWithIndex(i + 1, task);
                found = true;
            }
        }

        if (!found) {
            ui.showNoMatchingTasksFound();
        }
    }
}
