package kraken.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import kraken.exception.KrakenException;
import kraken.task.Deadline;
import kraken.task.Event;
import kraken.task.Task;
import kraken.task.Todo;
import kraken.util.DateTimeUtil;

/**
 * Handles loading/saving Kraken tasks from/to disk using an OS-independent relative path.
 *
 * File format (one task per line, fields separated by " | "):
 * - Todo:     T | 0/1 | <description>
 * - Deadline: D | 0/1 | <description> | <by (ISO date-time, e.g., 2019-12-02T18:00)>
 * - Event:    E | 0/1 | <description> | <from (ISO date-time)> | <to (ISO date-time)>
 */
public class Storage {
    private static final String DELIMITER = " | ";
    private static final String SPLIT_REGEX = "\\s\\|\\s";
    private final Path dataFile;

    /**
     * Creates a {@code Storage} instance that persists to {@code data/kraken.txt}.
     */
    public Storage() {
        this.dataFile = Paths.get("data", "kraken.txt");
    }

    /**
     * Creates a {@code Storage} instance that persists to the given file path.
     *
     * @param dataFile path to the save file
     */
    public Storage(Path dataFile) {
        this.dataFile = Objects.requireNonNull(dataFile);
    }

    /**
     * Loads tasks from disk.
     *
     * - If the file doesn't exist, returns an empty list.
     * - If some lines are corrupt, skips those lines and continues.
     * - On IO errors, prints a warning to stderr and returns what was loaded so far (or empty).
     *
     * @return tasks loaded from disk (possibly empty)
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(dataFile)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Warning: Unable to load tasks from '" + dataFile + "': " + e.getMessage());
            return tasks;
        }

        for (String rawLine : lines) {
            if (rawLine == null) {
                continue;
            }
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            parseLine(line).ifPresent(tasks::add);
        }

        return tasks;
    }

    /**
     * Saves tasks to disk.
     *
     * Save is silent on stdout; on IO errors it prints a warning to stderr and continues.
     *
     * @param tasks tasks to persist
     */
    public void save(List<Task> tasks) {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = tasks.stream().map(this::serialize).filter(Objects::nonNull).toList();

            Files.write(
                    dataFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            System.err.println("Warning: Unable to save tasks to '" + dataFile + "': " + e.getMessage());
        }
    }

    /**
     * Serializes a task into a single line suitable for persistence.
     *
     * @param task task to serialize
     * @return a single-line representation, or {@code null} if the task type is unknown
     */
    private String serialize(Task task) {
        int doneFlag = task.isDone() ? 1 : 0;

        if (task instanceof Todo) {
            return "T" + DELIMITER + doneFlag + DELIMITER + task.getDescription();
        }

        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return "D" + DELIMITER + doneFlag + DELIMITER + d.getDescription()
                    + DELIMITER + DateTimeUtil.formatForStorage(d.getBy());
        }

        if (task instanceof Event) {
            Event e = (Event) task;
            return "E" + DELIMITER + doneFlag + DELIMITER + e.getDescription()
                    + DELIMITER + DateTimeUtil.formatForStorage(e.getFrom())
                    + DELIMITER + DateTimeUtil.formatForStorage(e.getTo());
        }

        // Unknown task type; skip persisting it to avoid corrupting the save file.
        System.err.println("Warning: Skipping unknown task type during save: " + task.getClass().getSimpleName());
        return null;
    }

    /**
     * Parses a single persisted line into a {@link Task}.
     *
     * <p>If the line is corrupt (wrong number of fields, invalid dates, etc.), a warning is printed
     * and {@link Optional#empty()} is returned.</p>
     *
     * @param line persisted line (already trimmed and non-empty)
     * @return an {@link Optional} containing the parsed task, or empty if the line is corrupt
     */
    private Optional<Task> parseLine(String line) {
        String[] parts = line.split(SPLIT_REGEX, -1);

        if (parts.length < 3) {
            warnCorruptLine(line);
            return Optional.empty();
        }

        String type = parts[0].trim();
        String doneStr = parts[1].trim();
        Boolean isDone = parseDoneFlag(doneStr);
        if (isDone == null) {
            warnCorruptLine(line);
            return Optional.empty();
        }

        switch (type) {
        case "T":
            if (parts.length != 3) {
                warnCorruptLine(line);
                return Optional.empty();
            }
            return createTask(new Todo(parts[2].trim()), isDone, line);
        case "D":
            if (parts.length != 4) {
                warnCorruptLine(line);
                return Optional.empty();
            }
            try {
                return createTask(new Deadline(parts[2].trim(), DateTimeUtil.parseStorageDateTime(parts[3].trim())), isDone, line);
            } catch (KrakenException e) {
                warnCorruptLine(line);
                return Optional.empty();
            }
        case "E":
            if (parts.length != 5) {
                warnCorruptLine(line);
                return Optional.empty();
            }
            try {
                return createTask(
                        new Event(
                                parts[2].trim(),
                                DateTimeUtil.parseStorageDateTime(parts[3].trim()),
                                DateTimeUtil.parseStorageDateTime(parts[4].trim())
                        ),
                        isDone,
                        line
                );
            } catch (KrakenException e) {
                warnCorruptLine(line);
                return Optional.empty();
            }
        default:
            warnCorruptLine(line);
            return Optional.empty();
        }
    }

    /**
     * Validates a newly created task and applies its completion state.
     *
     * <p>If validation fails, a warning is printed and {@link Optional#empty()} is returned.</p>
     *
     * @param task the task instance created from parsed fields
     * @param isDone whether the task should be marked done
     * @param originalLine original persisted line (used for warning output)
     * @return an {@link Optional} containing the validated task, or empty if invalid
     */
    private Optional<Task> createTask(Task task, boolean isDone, String originalLine) {
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            warnCorruptLine(originalLine);
            return Optional.empty();
        }

        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            if (d.getBy() == null) {
                warnCorruptLine(originalLine);
                return Optional.empty();
            }
        }

        if (task instanceof Event) {
            Event e = (Event) task;
            if (e.getFrom() == null || e.getTo() == null || e.getFrom().isAfter(e.getTo())) {
                warnCorruptLine(originalLine);
                return Optional.empty();
            }
        }

        if (isDone) {
            task.markAsDone();
        }
        return Optional.of(task);
    }

    /**
     * Parses the persisted done flag.
     *
     * @param doneStr done flag field from storage
     * @return {@code Boolean.TRUE} for {@code "1"}, {@code Boolean.FALSE} for {@code "0"}, or
     *         {@code null} if invalid
     */
    private Boolean parseDoneFlag(String doneStr) {
        if ("1".equals(doneStr)) {
            return Boolean.TRUE;
        }
        if ("0".equals(doneStr)) {
            return Boolean.FALSE;
        }
        return null;
    }

    /**
     * Prints a warning for a corrupt persisted line.
     *
     * @param line the corrupt line content
     */
    private void warnCorruptLine(String line) {
        System.err.println("Warning: Skipping corrupt line in '" + dataFile + "': " + line);
    }
}
