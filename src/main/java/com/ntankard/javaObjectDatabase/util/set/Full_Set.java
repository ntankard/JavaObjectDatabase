package com.ntankard.javaObjectDatabase.util.set;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.Database;

import java.util.ArrayList;
import java.util.List;

public class Full_Set<T extends DataObject> extends ObjectSet<T> {

    /**
     * The DataObject to get from the database
     */
    protected Class<T> tClass;

    /**
     * Core database
     */
    private final Database database;

    /**
     * Constructor
     */
    public Full_Set(Database database, Class<T> tClass, SetFilter<T> filter) {
        super(filter);
        this.tClass = tClass;
        this.database = database;
    }

    /**
     * Constructor
     */
    public Full_Set(Database database, Class<T> tClass) {
        super(null);
        this.database = database;
        this.tClass = tClass;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<T> get() {
        List<T> toReturn = new ArrayList<>();
        for (T dataObject : database.get(tClass)) {
            if (shouldAdd(dataObject)) {
                toReturn.add(dataObject);
            }
        }
        return toReturn;
    }
}
