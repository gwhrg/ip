import java.time.LocalDateTime;

public class EventCommand extends Command {
    private final String description;
    private final LocalDateTime from;
    private final LocalDateTime to;

    public EventCommand(String description, LocalDateTime from, LocalDateTime to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
        storage.save(tasks.asList());
        ui.showTaskAdded(newTask, tasks.size());
    }
}

