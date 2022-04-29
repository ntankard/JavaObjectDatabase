package com.ntankard.javaObjectDatabase.dataField;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;
import com.ntankard.javaObjectDatabase.exception.nonCorrupting.NonCorruptingException;

import java.util.Collections;
import java.util.List;

/**
 * A DataField specifically for list type objects. A list can be used in a regular DataField but this object makes some
 * behavioural changes. The main change is in the listener system. When items are added to removed from the list they are
 * sent on the change listener. A list of added items  will be sent in the new field of the change listener and the list
 * of removed object will be sent in the old value field of the change listener. If a normal DataField is used the change
 * listener will not be fired at all if the list is interacted with
 *
 * @param <ListContentType> The type of data in the list that the field contains
 * @author Nicholas Tankard
 * @see DataField
 */
public class ListDataField<ListContentType> extends DataField<List<ListContentType>> {

    /**
     * Constructor
     */
    public ListDataField(DataField_Schema<List<ListContentType>> dataFieldSchema, DataObject container) {
        super(dataFieldSchema, container);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Value Access ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    @Override
    public List<ListContentType> get() {
        List<ListContentType> toReturn = super.get();
        if (toReturn == null) {
            throw new CorruptingException(getContainer().getTrackingDatabase(), "The list is null when it shouldn't be");
        }
        return Collections.unmodifiableList(toReturn);
    }

    /**
     * Add a new value to the core list from the user
     *
     * @param toAdd The value to be added
     */
    public void add(ListContentType toAdd) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DERIVED))
            throw new NonCorruptingException("Trying to set a field that is controlled by a data core");

        if (!hasValidValue())
            throw new NonCorruptingException("The field has not been initially set yet");

        addImpl(toAdd);
    }

    /**
     * Add a new value to the core list from a data core
     *
     * @param toAdd The value to be added
     */
    public void addFromDataCore(ListContentType toAdd) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DIRECT))
            throw new NonCorruptingException("A dataCore is trying to set a field controlled by the user");

        addImpl(toAdd);
    }


    /**
     * Remove a value from the core list from the user
     *
     * @param toRemove The value to remove
     */
    public void remove(ListContentType toRemove) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DERIVED))
            throw new NonCorruptingException("Trying to set a field that is controlled by a data core");

        if (!hasValidValue())
            throw new NonCorruptingException("The field has not been initially set yet");

        removeImpl(toRemove);
    }

    /**
     * Remove a value from the core list from a data core
     *
     * @param toRemove The value to remove
     */
    public void removeFromDataCore(ListContentType toRemove) {
        if (getDataFieldSchema().getSourceMode().equals(DataField_Schema.SourceMode.DIRECT))
            throw new NonCorruptingException("A dataCore is trying to set a field controlled by the user");

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
    private void addImpl(ListContentType toAdd) {
        if (!getState().equals(NewFieldState.ACTIVE) && !getState().equals(NewFieldState.READY_FOR_VALUE) && !getState().equals(NewFieldState.READY_TO_ADD))
            throw new NonCorruptingException("Wrong state for adding a value");

        if (value.contains(toAdd))
            throw new NonCorruptingException("Trying to add a value that has already been added");

        set_preCheck(Collections.singletonList(toAdd));
        value.add(toAdd);

        for (FieldChangeListener<List<ListContentType>> fieldChangeListener : getFieldChangeListeners()) {
            fieldChangeListener.valueChanged(this, null, Collections.singletonList(toAdd));
        }
    }

    /**
     * Perform the action of removing a value
     *
     * @param toRemove The value to remove
     */
    private void removeImpl(ListContentType toRemove) {
        if (!getState().equals(NewFieldState.ACTIVE) && !getState().equals(NewFieldState.READY_FOR_VALUE) && !getState().equals(NewFieldState.READY_TO_ADD))
            throw new NonCorruptingException("Wrong state for removing a value");

        // TODO review this
        //if (!value.contains(toRemove))
        //throw new NonCorruptingException("Trying to remove a value that has never been added");

        value.remove(toRemove);

        for (FieldChangeListener<List<ListContentType>> fieldChangeListener : getFieldChangeListeners()) {
            fieldChangeListener.valueChanged(this, Collections.singletonList(toRemove), null);
        }
    }
}
