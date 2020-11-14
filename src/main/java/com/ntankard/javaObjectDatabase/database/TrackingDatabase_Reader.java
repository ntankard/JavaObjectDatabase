package com.ntankard.javaObjectDatabase.database;

import java.util.Map;

public class TrackingDatabase_Reader {

    /**
     * Read all files for the database from the latest save folder
     *
     * @param corePath The path that files are located in
     */
    public static TrackingDatabase read(TrackingDatabase_Schema schema, String corePath, Map<String, String> nameMap) {
        TrackingDatabase_Reader_Read reader = new TrackingDatabase_Reader_Read();
        TrackingDatabase database = new TrackingDatabase(schema, reader);
        reader.read(database, corePath, nameMap);
        return database;
    }

    /**
     * Save the database to a new directory
     *
     * @param corePath The directory to put the folder
     */
    public static void save(TrackingDatabase trackingDatabase, String corePath) {
        TrackingDatabase_Reader_Save.save(trackingDatabase, corePath);
    }
}
