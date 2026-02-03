/**
 * Parses user input into executable commands.
 */
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Parser {
    private static final String UNKNOWN_COMMAND_MESSAGE = "I don't understand that command. "
            + "Try: todo, deadline, event, list, on, mark, unmark, delete, bye";

    public static Command parse(String fullCommand) throws KrakenException {
        String trimmed = (fullCommand == null) ? "" : fullCommand.trim();

        if (trimmed.isEmpty()) {
            throw new KrakenException(UNKNOWN_COMMAND_MESSAGE);
        }

        String[] parts = trimmed.split("\\s+", 2);
        String commandWord = parts[0];
        String args = (parts.length < 2) ? "" : parts[1];

        switch (commandWord) {
        case "bye":
            return new ExitCommand();
        case "list":
            return new ListCommand();
        case "todo":
            return parseTodo(args);
        case "deadline":
            return parseDeadline(args);
        case "event":
            return parseEvent(args);
        case "on":
            return parseOn(args);
        case "mark":
            return parseMark(args);
        case "unmark":
            return parseUnmark(args);
        case "delete":
            return parseDelete(args);
        default:
            throw new KrakenException(UNKNOWN_COMMAND_MESSAGE);
        }

    }

    private static Command parseTodo(String args) throws KrakenException {
        String description = (args == null) ? "" : args.trim();
        if (description.isEmpty()) {
            throw new KrakenException("The description of a todo cannot be empty. "
                    + "Usage: todo <description>");
        }
        return new TodoCommand(description);
    }

    private static Command parseDeadline(String args) throws KrakenException {
        String remainder = (args == null) ? "" : args.trim();

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

        LocalDateTime byDateTime = DateTimeUtil.parseUserDateTime(by);
        return new DeadlineCommand(description, byDateTime);
    }

    private static Command parseEvent(String args) throws KrakenException {
        String remainder = (args == null) ? "" : args.trim();

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

        LocalDateTime fromDateTime = DateTimeUtil.parseUserDateTime(from);
        LocalDateTime toDateTime = DateTimeUtil.parseUserDateTime(to);
        if (fromDateTime.isAfter(toDateTime)) {
            throw new KrakenException("The /from date/time must not be after /to. "
                    + "Usage: event <description> /from <start> /to <end>");
        }

        return new EventCommand(description, fromDateTime, toDateTime);
    }

    private static Command parseOn(String args) throws KrakenException {
        String remainder = (args == null) ? "" : args.trim();

        if (remainder.isEmpty()) {
            throw new KrakenException("Please specify a date. Usage: on <date>");
        }

        LocalDate date = DateTimeUtil.parseUserDate(remainder);
        return new OnCommand(date);
    }

    private static Command parseMark(String args) throws KrakenException {
        String indexStr = (args == null) ? "" : args.trim();
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

        return new MarkCommand(taskIndex);
    }

    private static Command parseUnmark(String args) throws KrakenException {
        String indexStr = (args == null) ? "" : args.trim();
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

        return new UnmarkCommand(taskIndex);
    }

    private static Command parseDelete(String args) throws KrakenException {
        String indexStr = (args == null) ? "" : args.trim();
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

        return new DeleteCommand(taskIndex);
    }
}
