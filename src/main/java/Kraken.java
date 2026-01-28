import java.util.Scanner;

public class Kraken {
    public static void main(String[] args) {
        String[] tasks = new String[100];
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
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + ". " + tasks[i]);
                    }
                    System.out.println("____________________________________________________________");
                } else {
                    tasks[taskCount] = trimmedInput;
                    taskCount++;
                    System.out.println("____________________________________________________________");
                    System.out.println(" added: " + trimmedInput);
                    System.out.println("____________________________________________________________");
                }
            }
        }
    }
}

