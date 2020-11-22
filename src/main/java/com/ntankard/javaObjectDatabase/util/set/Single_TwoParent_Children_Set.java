package com.ntankard.javaObjectDatabase.util.set;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

import java.util.List;

public class Single_TwoParent_Children_Set<T extends DataObject, PrimaryParentType extends DataObject, SecondaryParentType extends DataObject> extends TwoParent_Children_Set<T, PrimaryParentType, SecondaryParentType> {

    /**
     * Constructor
     */
    public Single_TwoParent_Children_Set(Class<T> tClass, PrimaryParentType primaryParent, SecondaryParentType secondaryParent) {
        super(tClass, primaryParent, secondaryParent);
    }

    /**
     * Constructor
     */
    public Single_TwoParent_Children_Set(Class<T> tClass, PrimaryParentType primaryParent, SecondaryParentType secondaryParent, SetFilter<T> filter) {
        super(tClass, primaryParent, secondaryParent, filter);
    }

    /**
     * @inheritDoc
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
