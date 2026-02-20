package kraken.ui;

import kraken.task.Task;
import kraken.task.TaskList;

/**
 * Output surface used by commands to communicate with the user.
 */
public interface Ui {
    /**
     * Horizontal separator line used between UI sections.
     */
    String LINE = "____________________________________________________________";

    void showWelcome();

    void showBye();

    void showError(String message);

    void showTaskAdded(Task task, int taskCount);

    void showTaskDeleted(Task task, int taskCount);

    void showTaskMarked(Task task);

    void showTaskUnmarked(Task task);

    void showTaskList(TaskList tasks);

    void showMatchingTasksHeader();

    void showNoMatchingTasksFound();

    void showTasksOnDateHeader(String formattedDate);

    void showTaskWithIndex(int displayIndex, Task task);

    void showNoTasksFoundOn(String formattedDate);
}
