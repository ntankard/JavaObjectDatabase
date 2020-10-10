package com.ntankard.javaObjectDatabase.util;

import com.ntankard.javaObjectDatabase.util.SetFilter;

import java.util.List;

public abstract class ObjectSet<T> {

    /**
     * A filter to apply, can be null
     */
    private SetFilter<T> filter;

    /**
     * Constructor
     */
    public ObjectSet(SetFilter<T> filter) {
        this.filter = filter;
    }

    /**
     * Get the set of objects
     *
     * @return A set of daa objects
     */
    public abstract List<T> get();

    /**
     * Should this object be added to the set based on any available filters?
     *
     * @param t The object to test
     * @return True if it should be added to the set
     */
    public boolean shouldAdd(T t) {
        if (filter != null) {
            return filter.shouldAdd(t);
        }
        return true;
    }
}
