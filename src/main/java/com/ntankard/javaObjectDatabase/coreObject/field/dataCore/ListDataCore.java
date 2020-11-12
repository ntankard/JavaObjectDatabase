package com.ntankard.javaObjectDatabase.coreObject.field.dataCore;

import com.ntankard.javaObjectDatabase.coreObject.field.ListDataField;

import java.util.List;

public abstract class ListDataCore<T> extends DataCore<List<T>> {

    /**
     * Add the value to the dataField
     *
     * @param toAdd The value to add
     */
    protected void doAdd(T toAdd) {
        ((ListDataField<T>) getDataField()).addFromDataCore(toAdd);
    }

    /**
     * Remove the value from the dataField
     *
     * @param toRemover The value to remove
     */
    protected void doRemove(T toRemover) {
        ((ListDataField<T>) getDataField()).removeFromDataCore(toRemover);
    }
}
