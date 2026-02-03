import java.time.LocalDate;

public class OnCommand extends Command {
    private final LocalDate date;

    public OnCommand(LocalDate date) {
        this.date = date;
    }

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

