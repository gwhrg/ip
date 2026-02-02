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
import java.util.stream.Collectors;

/**
 * Handles loading/saving Kraken tasks from/to disk using an OS-independent relative path.
 *
 * File format (one task per line, fields separated by " | "):
 * - Todo:     T | 0/1 | <description>
 * - Deadline: D | 0/1 | <description> | <by>
 * - Event:    E | 0/1 | <description> | <from> | <to>
 */
public class Storage {
    private static final String DELIMITER = " | ";
    private static final String SPLIT_REGEX = "\\s\\|\\s";
    private final Path dataFile;

    public Storage() {
        this.dataFile = Paths.get("data", "kraken.txt");
    }

    public Storage(Path dataFile) {
        this.dataFile = Objects.requireNonNull(dataFile);
    }

    /**
     * Loads tasks from disk.
     *
     * - If the file doesn't exist, returns an empty list.
     * - If some lines are corrupt, skips those lines and continues.
     * - On IO errors, prints a warning to stderr and returns what was loaded so far (or empty).
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();

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
     */
    public void save(ArrayList<Task> tasks) {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = tasks.stream()
                    .map(this::serialize)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

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

    private String serialize(Task task) {
        int doneFlag = task.isDone() ? 1 : 0;

        if (task instanceof Todo) {
            return "T" + DELIMITER + doneFlag + DELIMITER + task.getDescription();
        }

        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return "D" + DELIMITER + doneFlag + DELIMITER + d.getDescription() + DELIMITER + d.getBy();
        }

        if (task instanceof Event) {
            Event e = (Event) task;
            return "E" + DELIMITER + doneFlag + DELIMITER + e.getDescription() + DELIMITER + e.getFrom() + DELIMITER + e.getTo();
        }

        // Unknown task type; skip persisting it to avoid corrupting the save file.
        System.err.println("Warning: Skipping unknown task type during save: " + task.getClass().getSimpleName());
        return null;
    }

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
            return createTask(new Deadline(parts[2].trim(), parts[3].trim()), isDone, line);
        case "E":
            if (parts.length != 5) {
                warnCorruptLine(line);
                return Optional.empty();
            }
            return createTask(new Event(parts[2].trim(), parts[3].trim(), parts[4].trim()), isDone, line);
        default:
            warnCorruptLine(line);
            return Optional.empty();
        }
    }

    private Optional<Task> createTask(Task task, boolean isDone, String originalLine) {
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            warnCorruptLine(originalLine);
            return Optional.empty();
        }

        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            if (d.getBy() == null || d.getBy().trim().isEmpty()) {
                warnCorruptLine(originalLine);
                return Optional.empty();
            }
        }

        if (task instanceof Event) {
            Event e = (Event) task;
            if (e.getFrom() == null || e.getFrom().trim().isEmpty() || e.getTo() == null || e.getTo().trim().isEmpty()) {
                warnCorruptLine(originalLine);
                return Optional.empty();
            }
        }

        if (isDone) {
            task.markAsDone();
        }
        return Optional.of(task);
    }

    private Boolean parseDoneFlag(String doneStr) {
        if ("1".equals(doneStr)) {
            return Boolean.TRUE;
        }
        if ("0".equals(doneStr)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private void warnCorruptLine(String line) {
        System.err.println("Warning: Skipping corrupt line in '" + dataFile + "': " + line);
    }
}
