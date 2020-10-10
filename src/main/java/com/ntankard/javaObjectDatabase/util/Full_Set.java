package com.ntankard.javaObjectDatabase.util;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.Database.TrackingDatabase;

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
