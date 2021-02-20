package com.ntankard.javaObjectDatabase.dataField;

import com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore;
import com.ntankard.javaObjectDatabase.dataField.listener.FieldChangeListener;
import com.ntankard.javaObjectDatabase.dataField.validator.FieldValidator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.exception.nonCorrupting.NonCorruptingException;

import java.util.ArrayList;
import java.util.List;

import static com.ntankard.javaObjectDatabase.dataField.DataField.NewFieldState.*;
import static com.ntankard.javaObjectDatabase.dataField.DataField_Schema.SourceMode.DERIVED;
import static com.ntankard.javaObjectDatabase.dataField.DataField_Schema.SourceMode.VIRTUAL_DERIVED;

/**
 * A field is an container for a single piece of data in a DataObject. The DataField is responsible for contains the
 * data, filtering it as needed and linking it to other fields in the system as needed.
 *
 * @param <FieldType> The type of data in the field
 * @author Nicholas Tankard
 */
public class DataField<FieldType> {

    // Fixed properties ------------------------------------------------------------------------------------------------

    /**
     * The structure of this DataField_Instance
     */
    private final DataField_Schema<FieldType> dataFieldSchema;

    /**
     * The class that contains this field
     */
    private final DataObject container;

    /**
     * Listener to register children and parents
     */
    private final FieldChangeListener<FieldType> notifyParentListener;

    // Setup properties ------------------------------------------------------------------------------------------------

    /**
     * The engine to control the data
     */
    private DataCore<FieldType, ?> dataCore = null;

    // Field State -----------------------------------------------------------------------------------------------------

    /**
     * The possible states of the fields, tied heavily to the states of the DataObject
     */
    public enum NewFieldState {
        UNDER_CONSTRUCTION, // The field has been constructed but not connected to other object yet
        READY_FOR_VALUE,    // All fields in the DataObject are constructed. This field is attached to all required other fields and is ready to have its value set
        READY_TO_ADD,       // The field has a valid value and is now valid itself but its attached DataObjects is not fully ready yet (other fields are still being set)
        ACTIVE,             // The object the field is attached to is in the database and in active use
        REMOVED,            // The object this field is attached to has been removed, it can not be added again and should have all ties cut
    }

    /**
     * The current state of the field
     */
    protected NewFieldState state;

    // Value of the field ----------------------------------------------------------------------------------------------

    /**
     * The current value of the field
     */
    protected FieldType value = null;

    /**
     * The most recent previous value
     */
    private FieldType oldValue = null;

    // Change Listeners ------------------------------------------------------------------------------------------------

    /**
     * Objects to be notified when data changes
     */
    private final List<FieldChangeListener<FieldType>> fieldChangeListeners = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public DataField(DataField_Schema<FieldType> dataFieldSchema, DataObject container) {
        this.dataFieldSchema = dataFieldSchema;
        this.container = container;
        this.state = UNDER_CONSTRUCTION;
        this.notifyParentListener = (field, oldValue, newValue) -> {
            if (DataObject.class.isAssignableFrom(field.dataFieldSchema.getType())) {
                if (field.dataFieldSchema.isTellParent()) {
                    if (field.getState().equals(ACTIVE)) {
                        if (oldValue != null) {
                            ((DataObject) oldValue).notifyChildUnLink(field.getContainer());
                        }
                        if (newValue != null) {
                            ((DataObject) newValue).notifyChildLink(field.getContainer());
                        }
                    }
                }
            }
        };

        addChangeListener(notifyParentListener);
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Setup #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The Field can now be attached to other fields in the same DataObject. If a DataCore is provide it will be created
     */
    public void linkWithingDataObject() {
        if (!getState().equals(UNDER_CONSTRUCTION))
            throw new NonCorruptingException("linkWithingDataObject can only be called once at the start of the Fields lifecycle");
        this.state = READY_FOR_VALUE;
        if (dataFieldSchema.getDataCore_schema() != null) {
            this.dataCore = dataFieldSchema.getDataCore_schema().createCore(this);
        }
    }

    /**
     * The Field can no register its presence as a child of parent object. It will also enable this being done
     * automatically from now on
     */
    public void linkToDataBase() {
        if (!getState().equals(READY_TO_ADD))
            throw new NonCorruptingException("The field has not been configured, added or had its initial value set yet");

        this.state = ACTIVE;

        if (DataObject.class.isAssignableFrom(dataFieldSchema.getType())) {
            if (dataFieldSchema.isTellParent()) {
                if (value != null) {
                    ((DataObject) value).notifyChildLink(getContainer());
                }
            }
        }
    }

    /**
     * Remove this field from the database
     */
    // TODO
    public void remove() {
        if (!getState().equals(ACTIVE))
            throw new NonCorruptingException("The field has not been configured, added or had its initial value set yet");

        // Soft unlink the object from the database
        this.removeChangeListener(notifyParentListener);
        if (DataObject.class.isAssignableFrom(dataFieldSchema.getType())) {
            if (dataFieldSchema.isTellParent()) {
                if (get() != null) {
                    ((DataObject) get()).notifyChildUnLink(getContainer());
                }
            }
        }

        // Check that nothing else links to this field. If it dose, undo the change and throw
        if (fieldChangeListeners.size() != 0) {
            this.addChangeListener(notifyParentListener);
            if (DataObject.class.isAssignableFrom(dataFieldSchema.getType())) {
                if (dataFieldSchema.isTellParent()) {
                    if (get() != null) {
                        ((DataObject) get()).notifyChildLink(getContainer());
                    }
                }
            }
            throw new NonCorruptingException("This field has change listeners attached that are not for internal use, and not part of children lists. Can not be removed");
        }

        // Detach the data core
        if (dataCore != null) {
            dataCore.detachFromField();
            dataCore = null;
        }

        this.state = REMOVED;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Change Listener #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Add a new change listener to get called when a value changes
     *
     * @param fieldChangeListener The FieldChangeListener to add
     */
    public void addChangeListener(FieldChangeListener<FieldType> fieldChangeListener) {
        this.fieldChangeListeners.add(fieldChangeListener);
    }

    /**
     * Remove a change listener
     *
     * @param fieldChangeListener The FieldChangeListener to remove
     */
    public void removeChangeListener(FieldChangeListener<FieldType> fieldChangeListener) {
        this.fieldChangeListeners.remove(fieldChangeListener);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Value Access ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the current value
     *
     * @return The current value
     */
    public FieldType get() {
        if (!hasValidValue())
            throw new NonCorruptingException("Cant get the value until its initially set");

        return value;
    }

    /**
     * Set the field value and perform what ever actions are required from the user or file
     *
     * @param toSet The value to set
     */
    public void set(FieldType toSet) {
        if (!dataFieldSchema.getCanEdit() && !state.equals(READY_FOR_VALUE))
            throw new NonCorruptingException("This field can only be set once");

        if (dataFieldSchema.getSourceMode().equals(DERIVED))
            throw new NonCorruptingException("Trying to set a field that is controlled by a data core");

        if (dataFieldSchema.getSourceMode().equals(VIRTUAL_DERIVED)) {
            dataFieldSchema.getSetterFunction().set(toSet, getContainer());
            return;
        }

        // TODO catch and rethrow as corrupting
        set_impl(toSet);
    }

    /**
     * Set the field value and perform what ever actions are required from a dataCore
     *
     * @param toSet The value to set
     */
    public void setFromDataCore(FieldType toSet) {
        if (dataFieldSchema.getSourceMode().equals(DataField_Schema.SourceMode.DIRECT))
            throw new NonCorruptingException("A dataCore is trying to set a field controlled by the user");

        set_impl(toSet);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Setter Impl ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Set the field value and perform what ever actions are required
     *
     * @param value The value to set
     */
    protected void set_impl(FieldType value) {
        if (!state.equals(ACTIVE) && !state.equals(READY_FOR_VALUE) && !getState().equals(READY_TO_ADD))
            throw new NonCorruptingException("Wrong state for setting a value");

        if (value == this.value && value != null) {
            return;
        }

        set_preCheck(value);
        set_set(value);

        if (state.equals(READY_FOR_VALUE)) {
            this.state = READY_TO_ADD;
        }

        set_postSet();
    }

    /**
     * Validate the value to set
     *
     * @param toCheck The value to set
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void set_preCheck(FieldType toCheck) {
        for (FieldValidator validator : dataFieldSchema.getValidators()) {
            if (!validator.isValid(toCheck, value, this.getContainer())) {
                throw new NonCorruptingException("Validator failed: " + validator.getValidatorDetails());
            }
        }
    }

    /**
     * Set the value
     *
     * @param value The value to set
     */
    private void set_set(FieldType value) {
        this.oldValue = this.value;
        this.value = value;
    }

    /**
     * Perform what ever actions are required after setting a new value (register, notify ect)
     */
    protected void set_postSet() {
        getFieldChangeListeners().forEach(fieldChangeListener -> fieldChangeListener.valueChanged(this, oldValue, value));
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    // Fixed properties ------------------------------------------------------------------------------------------------

    public DataObject getContainer() {
        return container;
    }

    public DataField_Schema<FieldType> getDataFieldSchema() {
        return dataFieldSchema;
    }

    // Field State -----------------------------------------------------------------------------------------------------s

    public NewFieldState getState() {
        return state;
    }

    // Setup properties ------------------------------------------------------------------------------------------------

    public DataCore<FieldType, ?> getDataCore() {
        return dataCore;
    }

    public boolean hasValidValue() {
        return getState().equals(READY_TO_ADD) || getState().equals(ACTIVE);
    }

    // Change Listeners ------------------------------------------------------------------------------------------------

    public List<FieldChangeListener<FieldType>> getFieldChangeListeners() {
        return fieldChangeListeners;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Object Methods #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return "DataField{" +
                "dataFieldSchema=" + ((dataFieldSchema == null) ? "null" : dataFieldSchema) +
                ", container=" + ((container == null) ? "null" : container) +
                ", state=" + state +
                ", value=" + ((value == null) ? "null" : value) +
                '}';
    }
}
