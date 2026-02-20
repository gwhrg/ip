package kraken.ui;

import java.util.Scanner;

import kraken.task.Task;
import kraken.task.TaskList;

/**
 * A text-based UI that prints to stdout and reads from stdin.
 *
 * <p>This is kept as an optional alternative to the JavaFX GUI.</p>
 */
public class TextUi implements Ui {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns whether there is another line of user input available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next full command line from the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    @Override
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Kraken");
        System.out.println(" What can I do for you?");
        showLine();
    }

    @Override
    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    @Override
    public void showError(String message) {
        System.out.println(" OOPS!!! " + message);
    }

    @Override
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    @Override
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    @Override
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    @Override
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    @Override
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    @Override
    public void showMatchingTasksHeader() {
        System.out.println(" Here are the matching tasks in your list:");
    }

    @Override
    public void showNoMatchingTasksFound() {
        System.out.println(" No matching tasks found.");
    }

    @Override
    public void showTasksOnDateHeader(String formattedDate) {
        System.out.println(" Here are the tasks on " + formattedDate + ":");
    }

    @Override
    public void showTaskWithIndex(int displayIndex, Task task) {
        System.out.println(" " + displayIndex + "." + task);
    }

    @Override
    public void showNoTasksFoundOn(String formattedDate) {
        System.out.println(" No tasks found on " + formattedDate + ".");
    }
}

