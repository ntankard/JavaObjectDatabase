package com.ntankard.javaObjectDatabase.database;

import java.util.Map;

public class Database_IO {

    /**
     * Read all files for the database from the latest save folder
     *
     * @param corePath The path that files are located in
     */
    public static Database read(Database_Schema schema, String corePath, Map<String, String> nameMap) {
        Database_IO_Reader reader = new Database_IO_Reader();
        Database database = new Database(schema, reader);
        reader.read(database, corePath, nameMap);
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
