package com.ntankard.javaObjectDatabase.util.set;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

import java.util.List;

public class Single_OneParent_Children_Set<T extends DataObject, ParentType extends DataObject> extends OneParent_Children_Set<T, ParentType> {

    /**
     * Constructor
     */
    public Single_OneParent_Children_Set(Class<T> tClass, ParentType parent) {
        super(tClass, parent);
    }

    /**
     * Constructor
     */
    public Single_OneParent_Children_Set(Class<T> tClass, ParentType parent, SetFilter<T> filter) {
        super(tClass, parent, filter);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<T> get() {
        List<T> toReturn = super.get();
        if (toReturn.size() != 1) {
            throw new IllegalStateException("The wrong number of items exist for this set");
        }
        return toReturn;
    }

    /**
     * Get the single item in the set
     *
     * @return The single item in the set
     */
    public T getItem() {
        return get().get(0);
    }
}
