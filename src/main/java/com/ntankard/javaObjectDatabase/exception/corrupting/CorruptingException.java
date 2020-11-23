package com.ntankard.javaObjectDatabase.exception.corrupting;

import com.ntankard.javaObjectDatabase.database.Database;

/**
 * An exception indicating that the database is corrupt in an unrecoverable way. This can be as a result of the action
 * that raised this exception or if corruption was detected as part of an unrelated operation
 *
 * @author Nicholas Tankard
 */
public class CorruptingException extends RuntimeException {

    /**
     * The database that was corrupted
     */
    private final Database database;

    /**
     * @param database The database that was corrupted
     * @see RuntimeException#RuntimeException()
     */
    public CorruptingException(Database database) {
        super();
        this.database = database;
        if (database != null) {
            database.notifyCorruptingException(this);
        }
    }

    /**
     * @param database The database that was corrupted
     * @see RuntimeException#RuntimeException(String message)
     */
    public CorruptingException(Database database, String message) {
        super(message);
        this.database = database;
        if (database != null) {
            database.notifyCorruptingException(this);
        }
    }

    /**
     * @param database The database that was corrupted
     * @see RuntimeException#RuntimeException(String message, Throwable cause)
     */
    public CorruptingException(Database database, String message, Throwable cause) {
        super(message, cause);
        this.database = database;
        if (database != null) {
            database.notifyCorruptingException(this);
        }
    }

    /**
     * @param database The database that was corrupted
     * @see RuntimeException#RuntimeException(Throwable cause)
     */
    public CorruptingException(Database database, Throwable cause) {
        super(cause);
        this.database = database;
        if (database != null) {
            database.notifyCorruptingException(this);
        }
    }

    /**
     * Get the database that was corrupted
     *
     * @return The database that was corrupted
     */
    public Database getDatabase() {
        return database;
    }
}
