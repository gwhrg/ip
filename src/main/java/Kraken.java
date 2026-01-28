import java.util.Scanner;

public class Kraken {
    public static void main(String[] args) {
        Task[] tasks = new Task[100];
        int taskCount = 0;

        try (Scanner in = new Scanner(System.in)) {
            System.out.println("____________________________________________________________");
            System.out.println(" Hello! I'm Kraken");
            System.out.println(" What can I do for you?");
            System.out.println("____________________________________________________________");

            while (in.hasNextLine()) {
                String userInput = in.nextLine();
                String trimmedInput = userInput.trim();

                if (trimmedInput.equals("bye")) {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println("____________________________________________________________");
                    return;
                } else if (trimmedInput.equals("list")) {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + ".[" + tasks[i].getStatusIcon() + "] " + tasks[i].description);
                    }
                    System.out.println("____________________________________________________________");
                } else if (trimmedInput.startsWith("mark ")) {
                    int taskIndex = Integer.parseInt(trimmedInput.substring(5).trim()) - 1;
                    tasks[taskIndex].markAsDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   [" + tasks[taskIndex].getStatusIcon() + "] " + tasks[taskIndex].description);
                    System.out.println("____________________________________________________________");
                } else if (trimmedInput.startsWith("unmark ")) {
                    int taskIndex = Integer.parseInt(trimmedInput.substring(7).trim()) - 1;
                    tasks[taskIndex].markAsNotDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   [" + tasks[taskIndex].getStatusIcon() + "] " + tasks[taskIndex].description);
                    System.out.println("____________________________________________________________");
                } else {
                    tasks[taskCount] = new Task(trimmedInput);
                    taskCount++;
                    System.out.println("____________________________________________________________");
                    System.out.println(" added: " + trimmedInput);
                    System.out.println("____________________________________________________________");
                }
            }
        }
    }
}

