package kraken.command;

import java.time.LocalDate;

import kraken.storage.Storage;
import kraken.task.Deadline;
import kraken.task.Event;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.ui.Ui;
import kraken.util.DateTimeUtil;

/**
 * Lists tasks that occur on a specific date.
 *
 * <p>Deadlines match when their {@code by} date equals the requested date. Events match when the
 * requested date falls within their inclusive {@code from}/{@code to} date range.</p>
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that lists tasks occurring on the given date.
     *
     * @param date date to filter by
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Prints a header for the requested date, then lists matching tasks (if any). If no tasks
     * match, a \"no tasks found\" message is printed.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String formattedDate = DateTimeUtil.formatForDisplay(date.atStartOfDay());
        ui.showTasksOnDateHeader(formattedDate);

        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            if (task instanceof Deadline) {
                Deadline d = (Deadline) task;
                if (d.getBy() != null && d.getBy().toLocalDate().equals(date)) {
                    ui.showTaskWithIndex(i + 1, task);
                    found = true;
                }
                continue;
            }

            if (task instanceof Event) {
                Event e = (Event) task;
                if (e.getFrom() != null && e.getTo() != null) {
                    LocalDate start = e.getFrom().toLocalDate();
                    LocalDate end = e.getTo().toLocalDate();
                    boolean isOnDate = !date.isBefore(start) && !date.isAfter(end);
                    if (isOnDate) {
                        ui.showTaskWithIndex(i + 1, task);
                        found = true;
                    }
                }
            }
        }

        if (!found) {
            ui.showNoTasksFoundOn(formattedDate);
        }
    }
}

