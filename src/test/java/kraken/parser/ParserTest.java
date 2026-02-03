package kraken.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import kraken.command.Command;
import kraken.exception.KrakenException;
import kraken.storage.Storage;
import kraken.task.Deadline;
import kraken.task.Event;
import kraken.task.Task;
import kraken.task.TaskList;
import kraken.task.Todo;
import kraken.ui.Ui;

/**
 * Unit tests for {@link Parser}.
 *
 * <p>Uses no-op implementations of {@link Ui} and {@link Storage} to avoid console output and disk
 * writes during execution tests.</p>
 */
public class ParserTest {
    private static final Ui UI = new NoOpUi();
    private static final Storage STORAGE = new NoOpStorage();

    /**
     * Parses and executes a command against the given task list using no-op UI and storage.
     *
     * @param fullCommand full command line to parse
     * @param tasks task list to mutate
     * @throws KrakenException if parsing or execution fails
     */
    private static void execute(String fullCommand, TaskList tasks) throws KrakenException {
        Command command = Parser.parse(fullCommand);
        command.execute(tasks, UI, STORAGE);
    }

    /**
     * Verifies that {@link Parser#parse(String)} rejects {@code null} input.
     */
    @Test
    public void parse_null_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse(null));
        assertTrue(e.getMessage().contains("I don't understand"), e.getMessage());
    }

    /**
     * Verifies that {@link Parser#parse(String)} rejects blank input.
     */
    @Test
    public void parse_blank_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("   "));
        assertTrue(e.getMessage().contains("I don't understand"), e.getMessage());
    }

    /**
     * Verifies that unknown commands are rejected.
     */
    @Test
    public void parse_unknownCommand_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("wat"));
        assertTrue(e.getMessage().contains("I don't understand"), e.getMessage());
    }

    /**
     * Verifies that {@code todo} without a description is rejected.
     */
    @Test
    public void parse_todoMissingDescription_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("todo"));
        assertTrue(e.getMessage().contains("description of a todo cannot be empty"), e.getMessage());
        assertTrue(e.getMessage().contains("Usage: todo"), e.getMessage());
    }

    /**
     * Verifies that {@code deadline} without a {@code /by} marker is rejected.
     */
    @Test
    public void parse_deadlineMissingByMarker_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("deadline return book"));
        assertTrue(e.getMessage().contains("requires a /by"), e.getMessage());
    }

    /**
     * Verifies that {@code deadline} with an empty {@code /by} date is rejected.
     */
    @Test
    public void parse_deadlineMissingByDate_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("deadline return book /by"));
        assertTrue(e.getMessage().contains("/by date"), e.getMessage());
        assertTrue(e.getMessage().contains("cannot be empty"), e.getMessage());
    }

    /**
     * Verifies that {@code deadline} rejects an invalid date.
     */
    @Test
    public void parse_deadlineInvalidByDate_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("deadline return book /by 2019-02-29"));
        assertTrue(e.getMessage().contains("Invalid date/time"), e.getMessage());
    }

    /**
     * Verifies that {@code event} without a {@code /from} marker is rejected.
     */
    @Test
    public void parse_eventMissingFromMarker_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("event meeting /to 2019-12-02 1200"));
        assertTrue(e.getMessage().contains("requires a /from"), e.getMessage());
    }

    /**
     * Verifies that {@code event} without a {@code /to} marker is rejected.
     */
    @Test
    public void parse_eventMissingToMarker_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("event meeting /from 2019-12-02 1100"));
        assertTrue(e.getMessage().contains("requires a /to"), e.getMessage());
    }

    /**
     * Verifies that {@code event} rejects inputs where {@code /to} appears before {@code /from}.
     */
    @Test
    public void parse_eventToBeforeFromMarker_throwsKrakenException() {
        KrakenException e = assertThrows(
                KrakenException.class,
                () -> Parser.parse("event meeting /to 2019-12-02 1200 /from 2019-12-02 1100")
        );
        assertTrue(e.getMessage().contains("/from marker must come before /to"), e.getMessage());
    }

    /**
     * Verifies that {@code event} rejects time ranges where {@code /from} is after {@code /to}.
     */
    @Test
    public void parse_eventFromAfterTo_throwsKrakenException() {
        KrakenException e = assertThrows(
                KrakenException.class,
                () -> Parser.parse("event meeting /from 2019-12-02 1200 /to 2019-12-02 1100")
        );
        assertTrue(e.getMessage().contains("must not be after"), e.getMessage());
    }

    /**
     * Verifies that {@code mark} without an index is rejected.
     */
    @Test
    public void parse_markMissingIndex_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("mark"));
        assertTrue(e.getMessage().contains("Usage: mark"), e.getMessage());
    }

    /**
     * Verifies that {@code mark} rejects non-numeric indices.
     */
    @Test
    public void parse_markNonNumericIndex_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("mark abc"));
        assertTrue(e.getMessage().contains("not a valid task number"), e.getMessage());
        assertTrue(e.getMessage().contains("Usage: mark"), e.getMessage());
    }

    /**
     * Verifies that {@code bye} parses into a command that requests application exit.
     */
    @Test
    public void parse_bye_isExitCommand() throws KrakenException {
        assertTrue(Parser.parse("bye").isExit());
    }

    /**
     * Verifies that executing {@code todo} adds a {@link Todo} with the expected description.
     */
    @Test
    public void execute_todo_addsTodoToTaskList() throws KrakenException {
        TaskList tasks = new TaskList();
        execute("  todo   read book  ", tasks);

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    /**
     * Verifies that a date-only {@code deadline} sets the due time to start-of-day.
     */
    @Test
    public void execute_deadlineDateOnly_setsStartOfDay() throws KrakenException {
        TaskList tasks = new TaskList();
        execute("deadline return book /by 2019-12-02", tasks);

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertInstanceOf(Deadline.class, task);

        Deadline deadline = (Deadline) task;
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
    }

    /**
     * Verifies that executing {@code event} sets the exact parsed start/end date-times.
     */
    @Test
    public void execute_eventDateTime_setsExactFromTo() throws KrakenException {
        TaskList tasks = new TaskList();
        execute("event trip /from 2019-12-02 0900 /to 2019-12-02 1800", tasks);

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertInstanceOf(Event.class, task);

        Event event = (Event) task;
        assertEquals("trip", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), event.getTo());
    }

    /**
     * Verifies that {@code mark} uses 1-based task numbers from user input.
     */
    @Test
    public void execute_mark_marksCorrectTask_oneBasedIndex() throws KrakenException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));
        tasks.add(new Todo("task 2"));

        execute("mark 2", tasks);

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    /**
     * No-op storage used in tests to prevent writing to disk.
     */
    private static class NoOpStorage extends Storage {
        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void save(List<Task> tasks) {
            // Prevent tests from writing to disk.
        }
    }

    /**
     * No-op UI used in tests to prevent writing to stdout.
     */
    private static class NoOpUi extends Ui {
        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showLine() {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showWelcome() {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showBye() {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showError(String message) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showTaskAdded(Task task, int taskCount) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showTaskDeleted(Task task, int taskCount) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showTaskMarked(Task task) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showTaskUnmarked(Task task) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showTaskList(TaskList tasks) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showTasksOnDateHeader(String formattedDate) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showTaskWithIndex(int displayIndex, Task task) {
            // no-op
        }

        /**
         * {@inheritDoc}
         *
         * <p>No-op in tests.</p>
         */
        @Override
        public void showNoTasksFoundOn(String formattedDate) {
            // no-op
        }
    }
}
