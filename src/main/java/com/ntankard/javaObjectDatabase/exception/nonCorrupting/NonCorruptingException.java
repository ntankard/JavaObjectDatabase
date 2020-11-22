package com.ntankard.javaObjectDatabase.exception.nonCorrupting;

/**
 * An exception that guarantees that the database is still intact after it is thrown. Either because the error caused no
 * corruption or because it was rolled back
 *
 * @author Nicholas Tankard
 */
public class NonCorruptingException extends RuntimeException {

    /**
     * @see RuntimeException#RuntimeException()
     */
    public NonCorruptingException() {
    }

    /**
     * @see RuntimeException#RuntimeException(String message)
     */
    public NonCorruptingException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String message, Throwable cause)
     */
    public NonCorruptingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable cause)
     */
    public NonCorruptingException(Throwable cause) {
        super(cause);
    }
}
