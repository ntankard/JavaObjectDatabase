package com.ntankard.javaObjectDatabase.Database.SubContainers;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

import java.util.HashMap;
import java.util.Map;

public abstract class Container<K, V> {

    // Main container
    protected Map<K, V> container = new HashMap<>();

    /**
     * Add an element to the container
     *
     * @param dataObject The object to add
     */
    public abstract void add(DataObject dataObject);

    /**
     * Remove an element from the container
     *
     * @param dataObject The object to remove
     */
    public abstract void remove(DataObject dataObject);

    /**
     * Throws an exception if an object is not safe to delete
     *
     * @param dataObject The object to check
     */
    protected void checkCanDelete(DataObject dataObject) {
        if (dataObject.getChildren().size() != 0) {
            throw new RuntimeException("Deleting an object with dependencies");
        }
    }
}
