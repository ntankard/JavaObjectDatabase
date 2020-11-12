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
     * Constructor
     */
    public Full_Set(Class<T> tClass) {
        super(null);
        this.tClass = tClass;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<T> get() {
        return TrackingDatabase.get().get(tClass);
    }
}
