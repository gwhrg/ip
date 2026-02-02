import java.util.ArrayList;
import java.util.Scanner;

public class Kraken {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Storage storage = new Storage();
        ArrayList<Task> tasks = storage.load();

        try (Scanner in = new Scanner(System.in)) {
            System.out.println(LINE);
            System.out.println(" Hello! I'm Kraken");
            System.out.println(" What can I do for you?");
            System.out.println(LINE);

            while (in.hasNextLine()) {
                String trimmedInput = in.nextLine().trim();

                try {
                    if (trimmedInput.equals("bye")) {
                        System.out.println(LINE);
                        System.out.println(" Bye. Hope to see you again soon!");
                        System.out.println(LINE);
                        return;
                    } else if (trimmedInput.equals("list")) {
                        System.out.println(LINE);
                        System.out.println(" Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println(" " + (i + 1) + "." + tasks.get(i));
                        }
                        System.out.println(LINE);
                    } else if (trimmedInput.equals("mark") || trimmedInput.startsWith("mark ")) {
                        handleMark(trimmedInput, tasks, storage);
                    } else if (trimmedInput.equals("unmark") || trimmedInput.startsWith("unmark ")) {
                        handleUnmark(trimmedInput, tasks, storage);
                    } else if (trimmedInput.equals("todo") || trimmedInput.startsWith("todo ")) {
                        handleTodo(trimmedInput, tasks, storage);
                    } else if (trimmedInput.equals("deadline") || trimmedInput.startsWith("deadline ")) {
                        handleDeadline(trimmedInput, tasks, storage);
                    } else if (trimmedInput.equals("event") || trimmedInput.startsWith("event ")) {
                        handleEvent(trimmedInput, tasks, storage);
                    } else if (trimmedInput.equals("delete") || trimmedInput.startsWith("delete ")) {
                        handleDelete(trimmedInput, tasks, storage);
                    } else {
                        throw new KrakenException("I don't understand that command. "
                                + "Try: todo, deadline, event, list, mark, unmark, delete, bye");
                    }
                } catch (KrakenException e) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! " + e.getMessage());
                    System.out.println(LINE);
                }
            }
        }
    }

    private static void handleTodo(String input, ArrayList<Task> tasks, Storage storage) throws KrakenException {
        String description;
        if (input.equals("todo")) {
            description = "";
        } else {
            description = input.substring("todo ".length()).trim();
        }

        if (description.isEmpty()) {
            throw new KrakenException("The description of a todo cannot be empty. "
                    + "Usage: todo <description>");
        }

        Task newTask = new Todo(description);
        tasks.add(newTask);
        storage.save(tasks);
        printAddedTask(newTask, tasks.size());
    }

    private static void handleDeadline(String input, ArrayList<Task> tasks, Storage storage) throws KrakenException {
        String remainder;
        if (input.equals("deadline")) {
            remainder = "";
        } else {
            remainder = input.substring("deadline ".length()).trim();
        }

        if (remainder.isEmpty()) {
            throw new KrakenException("The description of a deadline cannot be empty. "
                    + "Usage: deadline <description> /by <date>");
        }

        String byMarker = "/by";
        int byIndex = remainder.indexOf(byMarker);

        if (byIndex == -1) {
            throw new KrakenException("A deadline requires a /by date. "
                    + "Usage: deadline <description> /by <date>");
        }

        String description = remainder.substring(0, byIndex).trim();
        String by = remainder.substring(byIndex + byMarker.length()).trim();

        if (description.isEmpty()) {
            throw new KrakenException("The description of a deadline cannot be empty. "
                    + "Usage: deadline <description> /by <date>");
        }

        if (by.isEmpty()) {
            throw new KrakenException("The /by date of a deadline cannot be empty. "
                    + "Usage: deadline <description> /by <date>");
        }

        Task newTask = new Deadline(description, by);
        tasks.add(newTask);
        storage.save(tasks);
        printAddedTask(newTask, tasks.size());
    }

    private static void handleEvent(String input, ArrayList<Task> tasks, Storage storage) throws KrakenException {
        String remainder;
        if (input.equals("event")) {
            remainder = "";
        } else {
            remainder = input.substring("event ".length()).trim();
        }

        if (remainder.isEmpty()) {
            throw new KrakenException("The description of an event cannot be empty. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        String fromMarker = "/from";
        String toMarker = "/to";
        int fromIndex = remainder.indexOf(fromMarker);
        int toIndex = remainder.indexOf(toMarker);

        if (fromIndex == -1) {
            throw new KrakenException("An event requires a /from time. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        if (toIndex == -1) {
            throw new KrakenException("An event requires a /to time. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        if (toIndex < fromIndex) {
            throw new KrakenException("The /from marker must come before /to. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        String description = remainder.substring(0, fromIndex).trim();
        String from = remainder.substring(fromIndex + fromMarker.length(), toIndex).trim();
        String to = remainder.substring(toIndex + toMarker.length()).trim();

        if (description.isEmpty()) {
            throw new KrakenException("The description of an event cannot be empty. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        if (from.isEmpty()) {
            throw new KrakenException("The /from time of an event cannot be empty. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        if (to.isEmpty()) {
            throw new KrakenException("The /to time of an event cannot be empty. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
        storage.save(tasks);
        printAddedTask(newTask, tasks.size());
    }

    private static void handleMark(String input, ArrayList<Task> tasks, Storage storage) throws KrakenException {
        String indexStr;
        if (input.equals("mark")) {
            indexStr = "";
        } else {
            indexStr = input.substring("mark ".length()).trim();
        }

        if (indexStr.isEmpty()) {
            throw new KrakenException("Please specify which task to mark. "
                    + "Usage: mark <task number>");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(indexStr) - 1;
        } catch (NumberFormatException e) {
            throw new KrakenException("'" + indexStr + "' is not a valid task number. "
                    + "Usage: mark <task number>");
        }

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new KrakenException("Task number " + (taskIndex + 1) + " does not exist. "
                    + "You have " + tasks.size() + " task(s) in your list.");
        }

        tasks.get(taskIndex).markAsDone();
        storage.save(tasks);
        System.out.println(LINE);
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + tasks.get(taskIndex));
        System.out.println(LINE);
    }

    private static void handleUnmark(String input, ArrayList<Task> tasks, Storage storage) throws KrakenException {
        String indexStr;
        if (input.equals("unmark")) {
            indexStr = "";
        } else {
            indexStr = input.substring("unmark ".length()).trim();
        }

        if (indexStr.isEmpty()) {
            throw new KrakenException("Please specify which task to unmark. "
                    + "Usage: unmark <task number>");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(indexStr) - 1;
        } catch (NumberFormatException e) {
            throw new KrakenException("'" + indexStr + "' is not a valid task number. "
                    + "Usage: unmark <task number>");
        }

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new KrakenException("Task number " + (taskIndex + 1) + " does not exist. "
                    + "You have " + tasks.size() + " task(s) in your list.");
        }

        tasks.get(taskIndex).markAsNotDone();
        storage.save(tasks);
        System.out.println(LINE);
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + tasks.get(taskIndex));
        System.out.println(LINE);
    }

    private static void handleDelete(String input, ArrayList<Task> tasks, Storage storage) throws KrakenException {
        String indexStr;
        if (input.equals("delete")) {
            indexStr = "";
        } else {
            indexStr = input.substring("delete ".length()).trim();
        }

        if (indexStr.isEmpty()) {
            throw new KrakenException("Please specify which task to delete. "
                    + "Usage: delete <task number>");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(indexStr) - 1;
        } catch (NumberFormatException e) {
            throw new KrakenException("'" + indexStr + "' is not a valid task number. "
                    + "Usage: delete <task number>");
        }

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new KrakenException("Task number " + (taskIndex + 1) + " does not exist. "
                    + "You have " + tasks.size() + " task(s) in your list.");
        }

        Task removedTask = tasks.remove(taskIndex);
        storage.save(tasks);
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removedTask);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(LINE);
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }
}
