package kraken.ui;

import java.util.Scanner;

import kraken.task.Task;
import kraken.task.TaskList;

/**
 * Handles all interactions with the user (input/output).
 */
public class Ui {
    public static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Kraken");
        System.out.println(" What can I do for you?");
        showLine();
    }

    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void showError(String message) {
        System.out.println(" OOPS!!! " + message);
    }

    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    public void showTasksOnDateHeader(String formattedDate) {
        System.out.println(" Here are the tasks on " + formattedDate + ":");
    }

    public void showTaskWithIndex(int displayIndex, Task task) {
        System.out.println(" " + displayIndex + "." + task);
    }

    public void showNoTasksFoundOn(String formattedDate) {
        System.out.println(" No tasks found on " + formattedDate + ".");
    }
}

