package kraken.ui;

import java.util.Scanner;

import kraken.task.Task;
import kraken.task.TaskList;

/**
 * Handles all interactions with the user (input/output).
 */
public class Ui {
    /**
     * Horizontal separator line used between UI sections.
     */
    public static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a UI that reads user input from {@link System#in}.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether there is another line of user input available.
     *
     * @return {@code true} if a subsequent call to {@link #readCommand()} will succeed
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next full command line from the user.
     *
     * @return the next input line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints a horizontal separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints the welcome message.
     */
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Kraken");
        System.out.println(" What can I do for you?");
        showLine();
    }

    /**
     * Prints the farewell message.
     */
    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Prints an error message to the user.
     *
     * @param message error details to display
     */
    public void showError(String message) {
        System.out.println(" OOPS!!! " + message);
    }

    /**
     * Prints a confirmation message after adding a task.
     *
     * @param task the task that was added
     * @param taskCount total number of tasks after adding
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints a confirmation message after deleting a task.
     *
     * @param task the task that was removed
     * @param taskCount total number of tasks after deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints a confirmation message after marking a task as done.
     *
     * @param task the task that was marked
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /**
     * Prints a confirmation message after marking a task as not done.
     *
     * @param task the task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /**
     * Prints the current list of tasks.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints a header for tasks that occur on the given date.
     *
     * @param formattedDate date string already formatted for display
     */
    public void showTasksOnDateHeader(String formattedDate) {
        System.out.println(" Here are the tasks on " + formattedDate + ":");
    }

    /**
     * Prints a single task line with a 1-based display index.
     *
     * @param displayIndex 1-based index shown to the user
     * @param task task to display
     */
    public void showTaskWithIndex(int displayIndex, Task task) {
        System.out.println(" " + displayIndex + "." + task);
    }

    /**
     * Prints a message indicating that no tasks were found on the given date.
     *
     * @param formattedDate date string already formatted for display
     */
    public void showNoTasksFoundOn(String formattedDate) {
        System.out.println(" No tasks found on " + formattedDate + ".");
    }
}
