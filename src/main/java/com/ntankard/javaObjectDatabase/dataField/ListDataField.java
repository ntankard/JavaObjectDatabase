package com.ntankard.javaObjectDatabase.dataField;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataField.listener.FieldChangeListener;

import java.util.Collections;
import java.util.List;

public class ListDataField<T> extends DataField<List<T>> {

    /**
     * Constructor
     */
    public ListDataField(DataField_Schema<List<T>> dataFieldSchema, DataObject container) {
        super(dataFieldSchema,container);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Value Access ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc
     */
    @Override
    public List<T> get() {
        List<T> toReturn = super.get();
        if (toReturn == null) {
            throw new RuntimeException("The list is null when it shouldn't be");
        }
        return Collections.unmodifiableList(toReturn);
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected void set_impl(List<T> value) {
        if (!getState().equals(NewFieldState.N_ATTACHED_TO_OBJECT))
            throw new IllegalStateException("This field can only be set once");

        if (value == null) {
            throw new IllegalArgumentException("List can never be null");
        }

        this.value = value;
        this.state = NewFieldState.N_INITIALIZED;
        for (FieldChangeListener<List<T>> fieldChangeListener : getFieldChangeListeners()) {
            fieldChangeListener.valueChanged(this, null, null);
        }
    }

    /**
     * Add a new value to the core list from the user
     *
     * @param toAdd The value to be added
     */
    public void add(T toAdd) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DERIVED))
            throw new IllegalStateException("Trying to set a field that is controlled by a data core");

        if (!getState().equals(NewFieldState.N_INITIALIZED) || !getState().equals(NewFieldState.N_ACTIVE))
            throw new IllegalStateException("This field can only be set once");

        addImpl(toAdd);
    }

    /**
     * Remove a value from the core list from the user
     *
     * @param toRemove The value to remove
     */
    public void remove(T toRemove) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DERIVED))
            throw new IllegalStateException("Trying to set a field that is controlled by a data core");

        if (!getState().equals(NewFieldState.N_INITIALIZED) || !getState().equals(NewFieldState.N_ACTIVE))
            throw new IllegalStateException("This field can only be set once");

        removeImpl(toRemove);
    }

    /**
     * Add a new value to the core list from a data core
     *
     * @param toAdd The value to be added
     */
    public void addFromDataCore(T toAdd) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DIRECT))
            throw new IllegalStateException("A dataCore is trying to set a field controlled by the user");

        addImpl(toAdd);
    }

    /**
     * Remove a value from the core list from a data core
     *
     * @param toRemove The value to remove
     */
    public void removeFromDataCore(T toRemove) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DIRECT))
            throw new IllegalStateException("A dataCore is trying to set a field controlled by the user");

        removeImpl(toRemove);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Add/Remove Impl #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Perform the action of adding a value
     *
     * @param toAdd The value to add
     */
    private void addImpl(T toAdd) {
        if (!getState().equals(NewFieldState.N_ACTIVE) && !getState().equals(NewFieldState.N_ATTACHED_TO_OBJECT) && !getState().equals(NewFieldState.N_INITIALIZED))
            throw new IllegalStateException("Wrong state for setting a value");

        if (value.contains(toAdd)) {
            throw new IllegalArgumentException("Trying to add a value that has already been added");
        }

        if (toAdd instanceof DataObject) {
            if (!((DataObject) toAdd).isAllValid()) {
                throw new IllegalStateException("Can not set an object that is not all valid");
            }
        }

        if (!doFilterCheck(Collections.singletonList(toAdd), value)) {
            throw new IllegalArgumentException("Attempting to set a invalid value");
        }

        if (!value.contains(toAdd)) {
            value.add(toAdd);
        }

        for (FieldChangeListener<List<T>> fieldChangeListener : getFieldChangeListeners()) {
            fieldChangeListener.valueChanged(this, null, Collections.singletonList(toAdd));
        }
    }

    /**
     * Perform the action of removing a value
     *
     * @param toRemove The value to remove
     */
    private void removeImpl(T toRemove) {
        if (!getState().equals(NewFieldState.N_ACTIVE) && !getState().equals(NewFieldState.N_ATTACHED_TO_OBJECT))
            throw new IllegalStateException("Wrong state for setting a value");

        if (!value.contains(toRemove)) {
            throw new IllegalArgumentException("Trying to remove a value that was never added");
        }

        value.remove(toRemove);

        for (FieldChangeListener<List<T>> fieldChangeListener : getFieldChangeListeners()) {
            fieldChangeListener.valueChanged(this, Collections.singletonList(toRemove), null);
        }
    }
}
