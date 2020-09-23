package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.ListDataCore;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;
import com.ntankard.javaObjectDatabase.util.SetFilter;

import java.util.ArrayList;
import java.util.List;

public class Children_ListDataCore<T extends DataObject> extends ListDataCore<T> {

    /**
     * The type of object stored in this set
     */
    private final Class<T> tClass;

    /**
     * The object to manages each of the parents
     */
    private final ParentAccess<?, T>[] parents;

    /**
     * A filter to apply, can be null
     */
    private final SetFilter<T> filter;

    /**
     * Constructor
     */
    @SafeVarargs
    public Children_ListDataCore(Class<T> aClass, ParentAccess<?, T>... parents) {
        this(aClass, null, parents);
    }

    /**
     * Constructor
     */
    @SafeVarargs
    public Children_ListDataCore(Class<T> aClass, SetFilter<T> filter, ParentAccess<?, T>... parents) {
        this.tClass = aClass;
        this.filter = filter;
        this.parents = parents;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void startInitialSet() {
        for (ParentAccess<?, T> parentAccess : parents) {
            parentAccess.lookForParent(this);
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void detachFromField(DataField<List<T>> field) {
        for (ParentAccess<?, T> parentAccess : parents) {
            parentAccess.remove();
        }
        super.detachFromField(field);
    }

    /**
     * Called by one of the ParentAccess when it finds it parents. If all the other ParentAccess have there parents as
     * well we can link
     *
     * @param source The ParentAccess that called this method, passed so not checked twice
     */
    private void notifyParentAccessReady(ParentAccess<?, T> source) {
        if (getDataField().getState().equals(DataField.NewFieldState.N_ATTACHED_TO_OBJECT)) {
            for (ParentAccess<?, T> parentAccess : parents) {
                if (parentAccess != source) {
                    if (!parentAccess.isReadyToLinkToParent()) {
                        return;
                    }
                }
            }
            doSet(new ArrayList<>());
            for (ParentAccess<?, T> parentAccess : parents) {
                parentAccess.linkToParent();
            }
        } else {
            throw new IllegalStateException("It should not be possible for this to be called in any other state");
        }
    }

    /**
     * Called when one of the parents has a new candidate object to add.
     *
     * @param dataObject The object to add
     * @param isInitial  Is this being called during initial setup (multiple adds will be ignored instead of throwing an error)
     * @param source     The source of this call
     */
    private void childAdded(T dataObject, ParentAccess<?, T> source, boolean isInitial) {
        if (doAllAgree(dataObject, source)) {
            if (isInitial && getDataField().get().contains(dataObject)) {
                return;
            }
            doAdd(dataObject);
        }
    }

    /**
     * Called when one of the parents has a new candidate object to remove.
     *
     * @param dataObject The object to remove
     * @param source     The source of this call
     */
    private void childRemoved(T dataObject, ParentAccess<?, T> source) {
        if (doAllAgree(dataObject, source)) {
            doRemove(dataObject);
        }
    }

    /**
     * Check that all parents and the filter agree that this value is valid (except for the source)
     *
     * @param dataObject The object to check
     * @param source     The source of the candidate
     * @return True if all agree
     */
    private boolean doAllAgree(T dataObject, ParentAccess<?, T> source) {
        // Are we ready for values?
        if (!getDataField().hasValidValue()) {
            return false;
        }

        // Do the other parents think this is a valid value as well?
        for (ParentAccess<?, T> parentAccess : parents) {
            if (parentAccess != source) {
                if (!parentAccess.isValidValue(tClass, dataObject)) {
                    return false;
                }
            }
        }

        // Dose this object pass the filter
        if (filter != null) {
            return filter.shouldAdd(dataObject);
        }

        return true;
    }

    /**
     * Get the type of object stored in this set
     *
     * @return The type of object stored in this set
     */
    private Class<T> getTClass() {
        return tClass;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## ParentAccess ##################################################
    //------------------------------------------------------------------------------------------------------------------


    public static class ParentAccess<ParentType extends DataObject, T extends DataObject> implements FieldChangeListener<ParentType>, DataObject.ChildrenListener<T> {

        /**
         * The field that will contain the parent
         */
        private final DataField<ParentType> parentField;

        /**
         * The parent object
         */
        private DataObject parent = null;

        /**
         * The Children_ListDataCore to call when objects are found
         */
        private Children_ListDataCore<T> owner;

        /**
         * Constructor
         */
        public ParentAccess(DataField<ParentType> parentField) {
            this.parentField = parentField;
        }

        /**
         * Called when it is clear to attach the change listener to the parentField
         *
         * @param owner The Children_ListDataCore to call when objects are found
         */
        private void lookForParent(Children_ListDataCore<T> owner) {
            this.owner = owner;

            if (this.parentField.isCanBeNull())
                throw new IllegalArgumentException("The parent field can not allow null values");

            this.parentField.addChangeListener(this);
            if (parentField.hasValidValue()) {
                valueChanged(parentField, null, parentField.get());
            }
        }

        /**
         * Called when we should start listening to the parent for new children
         */
        private void linkToParent() {
            if (!isReadyToLinkToParent())
                throw new IllegalStateException("Trying to link a parent before the parents has been provided");


            parent.addChildrenListener(this);
            for (DataObject dataObject : parent.getChildren()) {
                childAdded(dataObject, true);
            }
        }

        /**
         * The DataField that the DataCore this is attached to is being removed from the database. Release all listeners
         */
        private void remove() {
            if (parent.getChildrenListeners().contains(this)) {
                for (DataObject dataObject : parent.getChildren()) {
                    childRemoved(dataObject);
                }
                parent.removeChildrenListener(this);
            }

            parentField.removeChangeListener(this);
            this.parent = null;
            this.owner = null;
        }

        /**
         * Checks if the parent is knows and if we are ready to start looking for children
         *
         * @return True if the parent is known
         */
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean isReadyToLinkToParent() {
            return parent != null;
        }

        /**
         * Check if this value can be added to the list according to this parent
         *
         * @param aClass     The type of object we are looking for
         * @param dataObject The object to check
         * @return True if this value is a child of this parent
         */
        private boolean isValidValue(Class<T> aClass, T dataObject) {
            return parent.getChildren(aClass).contains(dataObject);
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void valueChanged(DataField<ParentType> field, ParentType oldValue, ParentType newValue) {
            if (owner == null)
                throw new RuntimeException("The listener has been attached before the field this has been linked to the core");

            if (newValue == null) {
                throw new UnsupportedOperationException(); // Dose not currently support a change in the parent
            }
            if (parent != null) {
                throw new UnsupportedOperationException(); // Dose not currently support a change in the parent
            }
            parent = newValue;
            owner.notifyParentAccessReady(this);
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void childAdded(DataObject dataObject) {
            childAdded(dataObject, false);
        }

        /**
         * Notify the list that this parent has a new child
         *
         * @param dataObject The new child
         * @param isInitial  True is this was called during the initial setup
         */
        @SuppressWarnings("unchecked")
        public void childAdded(DataObject dataObject, boolean isInitial) {
            if (owner.getTClass().isAssignableFrom(dataObject.getClass())) {
                owner.childAdded((T) dataObject, this, isInitial);
            }
        }

        /**
         * {@inheritDoc
         */
        @SuppressWarnings("unchecked")
        @Override
        public void childRemoved(DataObject dataObject) {
            if (owner.getTClass().isAssignableFrom(dataObject.getClass())) {
                owner.childRemoved((T) dataObject, this);
            }
        }
    }
}
