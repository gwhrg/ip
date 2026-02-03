package kraken.exception;

/**
 * Represents a Kraken-specific exception.
 */
public class KrakenException extends Exception {
    /**
     * Creates a {@code KrakenException} with the given message.
     *
     * @param message exception message
     */
    public KrakenException(String message) {
        super(message);
    }
}
