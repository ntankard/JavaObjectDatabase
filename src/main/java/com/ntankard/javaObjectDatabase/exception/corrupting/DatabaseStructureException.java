package com.ntankard.javaObjectDatabase.exception.corrupting;

import com.ntankard.javaObjectDatabase.database.Database;

/**
 * An Exception indicating that the Database schema is invalid in some way
 *
 * @author Nicholas Tankard
 */
public class DatabaseStructureException extends CorruptingException {

    /**
     * @see CorruptingException#CorruptingException(Database database)
     */
    public DatabaseStructureException(Database database) {
        super(database);
    }

    /**
     * @see CorruptingException#CorruptingException(Database database, String message)
     */
    public DatabaseStructureException(Database database, String message) {
        super(database, message);
    }

    /**
     * @see CorruptingException#CorruptingException(Database database, String message, Throwable cause)
     */
    public DatabaseStructureException(Database database, String message, Throwable cause) {
        super(database, message, cause);
    }

    /**
     * @see CorruptingException#CorruptingException(Database database, Throwable cause)
     */
    public DatabaseStructureException(Database database, Throwable cause) {
        super(database, cause);
    }
}
