package com.ntankard.javaObjectDatabase.database.subContainers;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

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
}
