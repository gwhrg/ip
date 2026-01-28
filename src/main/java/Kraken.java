import java.util.Scanner;

public class Kraken {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Task[] tasks = new Task[100];
        int taskCount = 0;

        try (Scanner in = new Scanner(System.in)) {
            System.out.println(LINE);
            System.out.println(" Hello! I'm Kraken");
            System.out.println(" What can I do for you?");
            System.out.println(LINE);

            while (in.hasNextLine()) {
                String trimmedInput = in.nextLine().trim();

                if (trimmedInput.equals("bye")) {
                    System.out.println(LINE);
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    return;
                } else if (trimmedInput.equals("list")) {
                    System.out.println(LINE);
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i]);
                    }
                    System.out.println(LINE);
                } else if (trimmedInput.startsWith("mark ")) {
                    int taskIndex = Integer.parseInt(trimmedInput.substring("mark".length()).trim()) - 1;
                    tasks[taskIndex].markAsDone();
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[taskIndex]);
                    System.out.println(LINE);
                } else if (trimmedInput.startsWith("unmark ")) {
                    int taskIndex = Integer.parseInt(trimmedInput.substring("unmark".length()).trim()) - 1;
                    tasks[taskIndex].markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[taskIndex]);
                    System.out.println(LINE);
                } else if (trimmedInput.startsWith("todo ")) {
                    String description = trimmedInput.substring("todo".length()).trim();
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                } else if (trimmedInput.startsWith("deadline ")) {
                    String remainder = trimmedInput.substring("deadline".length()).trim();
                    String byMarker = "/by";
                    int byIndex = remainder.indexOf(byMarker);
                    String description = remainder.substring(0, byIndex).trim();
                    String by = remainder.substring(byIndex + byMarker.length()).trim();
                    tasks[taskCount] = new Deadline(description, by);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                } else if (trimmedInput.startsWith("event ")) {
                    String remainder = trimmedInput.substring("event".length()).trim();
                    String fromMarker = "/from";
                    String toMarker = "/to";
                    int fromIndex = remainder.indexOf(fromMarker);
                    int toIndex = remainder.indexOf(toMarker, fromIndex + fromMarker.length());
                    String description = remainder.substring(0, fromIndex).trim();
                    String from = remainder.substring(fromIndex + fromMarker.length(), toIndex).trim();
                    String to = remainder.substring(toIndex + toMarker.length()).trim();
                    tasks[taskCount] = new Event(description, from, to);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                } else {
                    tasks[taskCount] = new Todo(trimmedInput);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                }
            }
        }
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }
}

