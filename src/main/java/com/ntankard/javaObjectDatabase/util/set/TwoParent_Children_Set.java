package com.ntankard.javaObjectDatabase.util.set;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;
import com.ntankard.javaObjectDatabase.database.TrackingDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TwoParent_Children_Set<T extends DataObject, PrimaryParentType extends DataObject, SecondaryParentType extends DataObject> extends ObjectSet<T> implements DataObject.ChildrenListener<T> {

    /**
     * The DataObject to get from the core
     */
    private final Class<T> tClass;

    /**
     * The core object to extract children from
     */
    private PrimaryParentType primaryParent;

    /**
     * The secondary object to extract children from
     */
    private SecondaryParentType secondaryParent;

    /**
     * Is the set attached to the database and listen for changes?
     */
    private boolean isAttached = false;

    /**
     * The master list of contents of this set. Only used if isAttached is true
     */
    private List<T> list = null;

    /**
     * Constructor
     */
    public TwoParent_Children_Set(Class<T> tClass, PrimaryParentType primaryParent, SecondaryParentType secondaryParent) {
        this(tClass, primaryParent, secondaryParent, null);
    }

    /**
     * Constructor
     */
    public TwoParent_Children_Set(Class<T> tClass, PrimaryParentType primaryParent, SecondaryParentType secondaryParent, SetFilter<T> filter) {
        super(filter);
        this.tClass = tClass;
        this.primaryParent = primaryParent;
        this.secondaryParent = secondaryParent;
    }

    /**
     * Attach this set to the database and maintain a list based on observed changes
     */
    public void attach() {
        if (isAttached) {
            throw new IllegalArgumentException();
        }
        primaryParent.addChildrenListener(this);
        secondaryParent.addChildrenListener(this);
        list = manualGet();
        isAttached = true;
    }

    /**
     * Detach from the database. After this all calls to get will be calculated in full
     */
    public void detach() {
        if (!isAttached) {
            throw new IllegalArgumentException();
        }
        primaryParent.removeChildrenListener(this);
        secondaryParent.removeChildrenListener(this);
        list = null;
        isAttached = false;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<T> get() {
        if (isAttached) {
            if (TrackingDatabase.get().shouldVerifyCalculations()) {
                List<T> reCalculated = manualGet();

                if (reCalculated.size() != list.size()) {
                    throw new IllegalStateException();
                }

                for (T toCheck : list) {
                    if (!reCalculated.contains(toCheck)) {
                        throw new IllegalStateException();
                    }
                }
            }
            return Collections.unmodifiableList(list);
        }
        return manualGet();
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void childAdded(T dataObject) {
        if (tClass.isAssignableFrom(dataObject.getClass())) {
            if (list.contains(dataObject)) {
                throw new IllegalArgumentException();
            }
            if (primaryParent.getChildren(tClass).contains(dataObject)) {
                if (secondaryParent.getChildren(tClass).contains(dataObject)) {
                    if (shouldAdd(dataObject)) {
                        list.add(dataObject);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void childRemoved(T dataObject) {
        if (tClass.isAssignableFrom(dataObject.getClass())) { // TODO check that this is the right way around, also check other uses
            if (primaryParent.getChildren(tClass).contains(dataObject)) {
                if (secondaryParent.getChildren(tClass).contains(dataObject)) {
                    list.remove(dataObject);
                }
            }
        }
    }

    /**
     * Regenerate the list manually fully
     *
     * @return The regenerated list
     */
    private List<T> manualGet() {
        if (primaryParent == null || secondaryParent == null) {
            return new ArrayList<>();
        }

        List<T> toReturn = new ArrayList<>();
        List<T> primary = primaryParent.getChildren(tClass);
        List<T> secondary = secondaryParent.getChildren(tClass);
        for (T t : primary) {
            if (secondary.contains(t)) {
                if (shouldAdd(t)) {
                    toReturn.add(t);
                }
            }
        }

        return toReturn;
    }

    /**
     * Set the core object to extract children from
     *
     * @param primaryParent The core object to extract children from
     */
    public void setPrimaryParent(PrimaryParentType primaryParent) {
        boolean wasAttached = isAttached;
        if (isAttached) {
            detach();
        }
        this.primaryParent = primaryParent;
        if (wasAttached) {
            attach();
        }
    }

    /**
     * Set the secondary object to extract children from
     *
     * @param secondaryParent The secondary object to extract children from
     */
    public void setSecondaryParent(SecondaryParentType secondaryParent) {
        boolean wasAttached = isAttached;
        if (isAttached) {
            detach();
        }
        this.secondaryParent = secondaryParent;
        if (wasAttached) {
            attach();
        }
    }
}
