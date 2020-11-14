package com.ntankard.javaObjectDatabase.util.set;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;
import com.ntankard.javaObjectDatabase.database.TrackingDatabase;

import java.util.List;

public class Full_Set<T extends DataObject> extends ObjectSet<T> {

    /**
     * The DataObject to get from the database
     */
    protected Class<T> tClass;

    /**
     * Core database
     */
    private final TrackingDatabase trackingDatabase;

    /**
     * Constructor
     */
    public Full_Set(TrackingDatabase trackingDatabase, Class<T> tClass) {
        super(null);
        this.trackingDatabase = trackingDatabase;
        this.tClass = tClass;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<T> get() {
        return trackingDatabase.get(tClass);
    }
}
