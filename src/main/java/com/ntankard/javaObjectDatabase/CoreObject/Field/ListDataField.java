package com.ntankard.javaObjectDatabase.CoreObject.Field;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;

import java.util.Collections;
import java.util.List;

import static com.ntankard.javaObjectDatabase.CoreObject.Field.DataField.NewFieldState.*;

public class ListDataField<T> extends DataField<List<T>> {


    /**
     * Constructor
     */
    @SuppressWarnings("unchecked")
    public ListDataField(String name, Class<? extends List<T>> type) {
        super(name, (Class<List<T>>) type, false);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void setCanEdit(Boolean canEdit) {
        throw new IllegalStateException("A list data field can not be edited");
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void setDataCore(DataCore<List<T>> dataCore) {
        if (!ListDataCore.class.isAssignableFrom(dataCore.getClass())) {
            throw new IllegalArgumentException("A ListDataField can only take a ListDataCore");
        }
        super.setDataCore(dataCore);
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
        if (!getState().equals(N_ATTACHED_TO_OBJECT))
            throw new IllegalStateException("This field can only be set once");

        if (value == null) {
            throw new IllegalArgumentException("List can never be null");
        }

        this.value = value;
        this.state = N_INITIALIZED;
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
        if (getSourceMode().equals(SourceMode.DERIVED))
            throw new IllegalStateException("Trying to set a field that is controlled by a data core");

        if (!getState().equals(N_INITIALIZED) || !getState().equals(N_ACTIVE))
            throw new IllegalStateException("This field can only be set once");

        addImpl(toAdd);
    }

    /**
     * Remove a value from the core list from the user
     *
     * @param toRemove The value to remove
     */
    public void remove(T toRemove) {
        if (getSourceMode().equals(SourceMode.DERIVED))
            throw new IllegalStateException("Trying to set a field that is controlled by a data core");

        if (!getState().equals(N_INITIALIZED) || !getState().equals(N_ACTIVE))
            throw new IllegalStateException("This field can only be set once");

        removeImpl(toRemove);
    }

    /**
     * Add a new value to the core list from a data core
     *
     * @param toAdd The value to be added
     */
    void addFromDataCore(T toAdd) {
        if (getSourceMode().equals(SourceMode.DIRECT))
            throw new IllegalStateException("A dataCore is trying to set a field controlled by the user");

        addImpl(toAdd);
    }

    /**
     * Remove a value from the core list from a data core
     *
     * @param toRemove The value to remove
     */
    void removeFromDataCore(T toRemove) {
        if (getSourceMode().equals(SourceMode.DIRECT))
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
        if (!getState().equals(N_ACTIVE) && !getState().equals(N_ATTACHED_TO_OBJECT) && !getState().equals(N_INITIALIZED))
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
        if (!getState().equals(N_ACTIVE) && !getState().equals(N_ATTACHED_TO_OBJECT))
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
