import java.util.Scanner;

public class Kraken {
    public static void main(String[] args) {
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
                }

                System.out.println("____________________________________________________________");
                System.out.println(" " + userInput);
                System.out.println("____________________________________________________________");
            }
        }
    }
}

