public class TodoCommand extends Command {
    private final String description;

    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task newTask = new Todo(description);
        tasks.add(newTask);
        storage.save(tasks.asList());
        ui.showTaskAdded(newTask, tasks.size());
    }
}

