package com.ntankard.javaObjectDatabase.database;

import java.util.Map;

public class TrackingDatabase_Reader {

    /**
     * Read all files for the database from the latest save folder
     *
     * @param corePath The path that files are located in
     */
    public static void read(String corePath, Map<String, String> nameMap) {
        TrackingDatabase_Reader_Read.read(corePath, nameMap);
    }

    /**
     * Save the database to a new directory
     *
     * @param corePath The directory to put the folder
     */
    public static void save(String corePath) {
        TrackingDatabase_Reader_Save.save(corePath);
    }
}
