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

