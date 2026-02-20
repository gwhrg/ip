package kraken.ui;

import kraken.task.Task;
import kraken.task.TaskList;

/**
 * A UI implementation that accumulates output into a string, for use by the GUI.
 */
public class GuiUi implements Ui {
    private final StringBuilder out = new StringBuilder();

    private void appendLine(String line) {
        if (out.length() > 0) {
            out.append(System.lineSeparator());
        }
        out.append(line);
    }

    /**
     * Returns the accumulated output and clears the buffer.
     */
    public String consumeOutput() {
        String result = out.toString();
        out.setLength(0);
        return result;
    }

    @Override
    public void showWelcome() {
        appendLine(LINE);
        appendLine(" Hello! I'm Kraken");
        appendLine(" What can I do for you?");
        appendLine(LINE);
    }

    @Override
    public void showBye() {
        appendLine(" Bye. Hope to see you again soon!");
    }

    @Override
    public void showError(String message) {
        appendLine(" OOPS!!! " + message);
    }

    @Override
    public void showTaskAdded(Task task, int taskCount) {
        appendLine(" Got it. I've added this task:");
        appendLine("   " + task);
        appendLine(" Now you have " + taskCount + " tasks in the list.");
    }

    @Override
    public void showTaskDeleted(Task task, int taskCount) {
        appendLine(" Noted. I've removed this task:");
        appendLine("   " + task);
        appendLine(" Now you have " + taskCount + " tasks in the list.");
    }

    @Override
    public void showTaskMarked(Task task) {
        appendLine(" Nice! I've marked this task as done:");
        appendLine("   " + task);
    }

    @Override
    public void showTaskUnmarked(Task task) {
        appendLine(" OK, I've marked this task as not done yet:");
        appendLine("   " + task);
    }

    @Override
    public void showTaskList(TaskList tasks) {
        appendLine(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendLine(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    @Override
    public void showMatchingTasksHeader() {
        appendLine(" Here are the matching tasks in your list:");
    }

    @Override
    public void showNoMatchingTasksFound() {
        appendLine(" No matching tasks found.");
    }

    @Override
    public void showTasksOnDateHeader(String formattedDate) {
        appendLine(" Here are the tasks on " + formattedDate + ":");
    }

    @Override
    public void showTaskWithIndex(int displayIndex, Task task) {
        appendLine(" " + displayIndex + "." + task);
    }

    @Override
    public void showNoTasksFoundOn(String formattedDate) {
        appendLine(" No tasks found on " + formattedDate + ".");
    }
}

