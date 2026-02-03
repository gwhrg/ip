package kraken.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;

import kraken.command.Command;
import kraken.command.DeadlineCommand;
import kraken.command.DeleteCommand;
import kraken.command.EventCommand;
import kraken.command.ExitCommand;
import kraken.command.FindCommand;
import kraken.command.ListCommand;
import kraken.command.MarkCommand;
import kraken.command.OnCommand;
import kraken.command.TodoCommand;
import kraken.command.UnmarkCommand;
import kraken.exception.KrakenException;
import kraken.util.DateTimeUtil;

/**
 * Parses user input into executable commands.
 */
public class Parser {
    private static final String UNKNOWN_COMMAND_MESSAGE = "I don't understand that command. "
            + "Try: todo, deadline, event, list, find, on, mark, unmark, delete, bye";

    /**
     * Parses the given user input into an executable {@link Command}.
     *
     * <p>The first token is treated as the command word; the remainder (if any) is passed to a
     * command-specific parser.</p>
     *
     * @param fullCommand full user input line (may be {@code null})
     * @return a concrete {@link Command} instance
     * @throws KrakenException if the input is blank, unknown, or fails validation
     */
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
        case "find":
            return parseFind(args);
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

    /**
     * Parses arguments for the {@code find} command.
     *
     * @param args raw arguments after the command word
     * @return a {@link FindCommand}
     * @throws KrakenException if the keyword is missing/blank
     */
    private static Command parseFind(String args) throws KrakenException {
        String keyword = (args == null) ? "" : args.trim();
        if (keyword.isEmpty()) {
            throw new KrakenException("Please specify a keyword. Usage: find <keyword>");
        }
        return new FindCommand(keyword);
    }

    /**
     * Parses arguments for the {@code todo} command.
     *
     * @param args raw arguments after the command word
     * @return a {@link TodoCommand}
     * @throws KrakenException if the description is missing/blank
     */
    private static Command parseTodo(String args) throws KrakenException {
        String description = (args == null) ? "" : args.trim();
        if (description.isEmpty()) {
            throw new KrakenException("The description of a todo cannot be empty. "
                    + "Usage: todo <description>");
        }
        return new TodoCommand(description);
    }

    /**
     * Parses arguments for the {@code deadline} command.
     *
     * <p>Expected format: {@code deadline <description> /by <date>}.</p>
     *
     * @param args raw arguments after the command word
     * @return a {@link DeadlineCommand}
     * @throws KrakenException if required fields are missing or the date/time is invalid
     */
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

    /**
     * Parses arguments for the {@code event} command.
     *
     * <p>Expected format: {@code event <description> /from <start> /to <end>}.</p>
     *
     * @param args raw arguments after the command word
     * @return an {@link EventCommand}
     * @throws KrakenException if required fields are missing or the time range is invalid
     */
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

    /**
     * Parses arguments for the {@code on} command.
     *
     * @param args raw arguments after the command word
     * @return an {@link OnCommand}
     * @throws KrakenException if the date is missing or invalid
     */
    private static Command parseOn(String args) throws KrakenException {
        String remainder = (args == null) ? "" : args.trim();

        if (remainder.isEmpty()) {
            throw new KrakenException("Please specify a date. Usage: on <date>");
        }

        LocalDate date = DateTimeUtil.parseUserDate(remainder);
        return new OnCommand(date);
    }

    /**
     * Parses arguments for the {@code mark} command.
     *
     * <p>The task number provided by the user is 1-based; this method converts it to a 0-based
     * index for internal use.</p>
     *
     * @param args raw arguments after the command word
     * @return a {@link MarkCommand}
     * @throws KrakenException if the index is missing or not a valid integer
     */
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

    /**
     * Parses arguments for the {@code unmark} command.
     *
     * <p>The task number provided by the user is 1-based; this method converts it to a 0-based
     * index for internal use.</p>
     *
     * @param args raw arguments after the command word
     * @return an {@link UnmarkCommand}
     * @throws KrakenException if the index is missing or not a valid integer
     */
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

    /**
     * Parses arguments for the {@code delete} command.
     *
     * <p>The task number provided by the user is 1-based; this method converts it to a 0-based
     * index for internal use.</p>
     *
     * @param args raw arguments after the command word
     * @return a {@link DeleteCommand}
     * @throws KrakenException if the index is missing or not a valid integer
     */
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
