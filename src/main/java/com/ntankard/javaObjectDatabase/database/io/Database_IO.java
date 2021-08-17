package com.ntankard.javaObjectDatabase.database.io;

import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.database.Database_Schema;

import java.util.Map;

public class Database_IO {

    /**
     * Read all files for the database from the latest save folder
     *
     * @param rootPackageName The root package all classes are in
     * @param corePath        The path that files are located in
     */
    public static Database read(Database_Schema schema, String rootPackageName, String corePath, Map<String, String> nameMap) {
        Database_IO_Reader reader = new Database_IO_Reader();
        Database database = new Database(schema, reader);
        reader.read(database, rootPackageName, corePath, nameMap);
        return database;
    }

    /**
     * Save the database to a new directory
     *
     * @param corePath The directory to put the folder
     */
    public static void save(Database database, String corePath) {
        Database_IO_Writer.save(database, corePath);
    }
}
