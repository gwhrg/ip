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

public class ParserTest {
    private static final Ui UI = new NoOpUi();
    private static final Storage STORAGE = new NoOpStorage();

    private static void execute(String fullCommand, TaskList tasks) throws KrakenException {
        Command command = Parser.parse(fullCommand);
        command.execute(tasks, UI, STORAGE);
    }

    @Test
    public void parse_null_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse(null));
        assertTrue(e.getMessage().contains("I don't understand"), e.getMessage());
    }

    @Test
    public void parse_blank_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("   "));
        assertTrue(e.getMessage().contains("I don't understand"), e.getMessage());
    }

    @Test
    public void parse_unknownCommand_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("wat"));
        assertTrue(e.getMessage().contains("I don't understand"), e.getMessage());
    }

    @Test
    public void parse_todoMissingDescription_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("todo"));
        assertTrue(e.getMessage().contains("description of a todo cannot be empty"), e.getMessage());
        assertTrue(e.getMessage().contains("Usage: todo"), e.getMessage());
    }

    @Test
    public void parse_deadlineMissingByMarker_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("deadline return book"));
        assertTrue(e.getMessage().contains("requires a /by"), e.getMessage());
    }

    @Test
    public void parse_deadlineMissingByDate_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("deadline return book /by"));
        assertTrue(e.getMessage().contains("/by date"), e.getMessage());
        assertTrue(e.getMessage().contains("cannot be empty"), e.getMessage());
    }

    @Test
    public void parse_deadlineInvalidByDate_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("deadline return book /by 2019-02-29"));
        assertTrue(e.getMessage().contains("Invalid date/time"), e.getMessage());
    }

    @Test
    public void parse_eventMissingFromMarker_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("event meeting /to 2019-12-02 1200"));
        assertTrue(e.getMessage().contains("requires a /from"), e.getMessage());
    }

    @Test
    public void parse_eventMissingToMarker_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("event meeting /from 2019-12-02 1100"));
        assertTrue(e.getMessage().contains("requires a /to"), e.getMessage());
    }

    @Test
    public void parse_eventToBeforeFromMarker_throwsKrakenException() {
        KrakenException e = assertThrows(
                KrakenException.class,
                () -> Parser.parse("event meeting /to 2019-12-02 1200 /from 2019-12-02 1100")
        );
        assertTrue(e.getMessage().contains("/from marker must come before /to"), e.getMessage());
    }

    @Test
    public void parse_eventFromAfterTo_throwsKrakenException() {
        KrakenException e = assertThrows(
                KrakenException.class,
                () -> Parser.parse("event meeting /from 2019-12-02 1200 /to 2019-12-02 1100")
        );
        assertTrue(e.getMessage().contains("must not be after"), e.getMessage());
    }

    @Test
    public void parse_markMissingIndex_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("mark"));
        assertTrue(e.getMessage().contains("Usage: mark"), e.getMessage());
    }

    @Test
    public void parse_markNonNumericIndex_throwsKrakenException() {
        KrakenException e = assertThrows(KrakenException.class, () -> Parser.parse("mark abc"));
        assertTrue(e.getMessage().contains("not a valid task number"), e.getMessage());
        assertTrue(e.getMessage().contains("Usage: mark"), e.getMessage());
    }

    @Test
    public void parse_bye_isExitCommand() throws KrakenException {
        assertTrue(Parser.parse("bye").isExit());
    }

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

    @Test
    public void execute_mark_marksCorrectTask_oneBasedIndex() throws KrakenException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));
        tasks.add(new Todo("task 2"));

        execute("mark 2", tasks);

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    private static class NoOpStorage extends Storage {
        @Override
        public void save(List<Task> tasks) {
            // Prevent tests from writing to disk.
        }
    }

    private static class NoOpUi extends Ui {
        @Override
        public void showLine() {
            // no-op
        }

        @Override
        public void showWelcome() {
            // no-op
        }

        @Override
        public void showBye() {
            // no-op
        }

        @Override
        public void showError(String message) {
            // no-op
        }

        @Override
        public void showTaskAdded(Task task, int taskCount) {
            // no-op
        }

        @Override
        public void showTaskDeleted(Task task, int taskCount) {
            // no-op
        }

        @Override
        public void showTaskMarked(Task task) {
            // no-op
        }

        @Override
        public void showTaskUnmarked(Task task) {
            // no-op
        }

        @Override
        public void showTaskList(TaskList tasks) {
            // no-op
        }

        @Override
        public void showTasksOnDateHeader(String formattedDate) {
            // no-op
        }

        @Override
        public void showTaskWithIndex(int displayIndex, Task task) {
            // no-op
        }

        @Override
        public void showNoTasksFoundOn(String formattedDate) {
            // no-op
        }
    }
}

