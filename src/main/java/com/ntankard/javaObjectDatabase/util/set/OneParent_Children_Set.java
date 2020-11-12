package com.ntankard.javaObjectDatabase.util.set;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;

import java.util.ArrayList;
import java.util.List;

public class OneParent_Children_Set<T extends DataObject, ParentType extends DataObject> extends ObjectSet<T> {

    /**
     * The DataObject to get from the core
     */
    private final Class<T> tClass;

    /**
     * The core object to extract children from
     */
    protected ParentType parent;

    /**
     * Constructor
     */
    public OneParent_Children_Set(Class<T> tClass, ParentType parent) {
        this(tClass, parent, null);
    }

    /**
     * Constructor
     */
    public OneParent_Children_Set(Class<T> tClass, ParentType parent, SetFilter<T> filter) {
        super(filter);
        this.tClass = tClass;
        this.parent = parent;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<T> get() {
        List<T> toReturn = new ArrayList<>();
        if (parent != null) {
            for (T dataObject : parent.getChildren(tClass)) {
                if (shouldAdd(dataObject)) {
                    toReturn.add(dataObject);
                }
            }
        }
        return toReturn;
    }

    /**
     * Set the core object to extract children from
     *
     * @param parent The core object to extract children from
     */
    public void setParent(ParentType parent) {
        this.parent = parent;
    }
}
