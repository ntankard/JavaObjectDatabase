package com.ntankard.javaObjectDatabase.testUtil.testDatabases;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.database.Database_Schema;
import com.ntankard.javaObjectDatabase.database.io.Database_IO_Reader;

import java.util.List;

/**
 * A utility for creating test databases
 *
 * @author Nicholas Tankard
 */
public class DatabaseFactory {

    /**
     * Create a blank database only containing the classes provided
     *
     * @param solidClasses The Types of classes to add
     * @return The blank database
     */
    public static Database getEmptyDatabase(List<Class<? extends DataObject>> solidClasses) {
        Database_Schema databaseSchema = new Database_Schema(solidClasses);
        Database database = new Database(databaseSchema, new Database_IO_Reader());
        database.setIDFloor(0);
        return database;
    }
}
