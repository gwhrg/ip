public class MarkCommand extends Command {
    private final int taskIndex;

    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KrakenException {
        Task task = tasks.getTaskOrThrow(taskIndex);
        task.markAsDone();
        storage.save(tasks.asList());
        ui.showTaskMarked(task);
    }
}

