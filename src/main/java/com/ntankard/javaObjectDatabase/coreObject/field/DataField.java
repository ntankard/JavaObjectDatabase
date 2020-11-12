package com.ntankard.javaObjectDatabase.coreObject.field;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;
import com.ntankard.javaObjectDatabase.coreObject.field.filter.FieldFilter;
import com.ntankard.javaObjectDatabase.coreObject.field.listener.FieldChangeListener;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.DataCore;

import java.util.ArrayList;
import java.util.List;

import static com.ntankard.javaObjectDatabase.coreObject.field.DataField_Schema.SourceMode.DERIVED;
import static com.ntankard.javaObjectDatabase.coreObject.field.DataField_Schema.SourceMode.VIRTUAL_DERIVED;
import static com.ntankard.javaObjectDatabase.coreObject.field.DataField.NewFieldState.*;


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
    private DataCore<FieldType> dataCore = null;

    // Field State -----------------------------------------------------------------------------------------------------

    public enum NewFieldState {
        N_ALL_FIELDS_FINISHED,  // All fields are finished and grouped into a container
        N_ATTACHED_TO_OBJECT,   // The field is attached to its container
        N_INITIALIZED,          // The field is fully setup and the initial value has been set
        N_ACTIVE,               // The object the field is attached to is in the database and in active use
        N_REMOVED               // The object this field is attached to has been removed, it can not be added again and should have all ties cut
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

        this.state = N_ALL_FIELDS_FINISHED;

        this.notifyParentListener = (field, oldValue, newValue) -> {
            if (DataObject.class.isAssignableFrom(field.dataFieldSchema.getType())) {
                if (field.dataFieldSchema.isTellParent()) {
                    if (field.getState().equals(N_ACTIVE)) {
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
     * Call to allow a data core to start receiving values
     */
    public void allowValue() {
        this.state = N_ATTACHED_TO_OBJECT;
        if (dataFieldSchema.getDataCore_factory() != null) {
            this.dataCore = dataFieldSchema.getDataCore_factory().createCore(this);
            this.dataCore.attachToField(this);
            this.dataCore.startInitialSet();
        }
    }

    /**
     * Add this field to the database along with its container object
     */
    public void add() {
        if (!getState().equals(N_INITIALIZED))
            throw new IllegalStateException("The field has not been configured, added or had its initial value set yet");

        if (!doFilterCheck(value, oldValue))
            throw new IllegalArgumentException("The field has been initially set but with an invalid value");
        this.state = N_ACTIVE;
    }

    /**
     * Notify all linked objects
     */
    public void forceNotify() {
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
    public void remove() {
        if (!getState().equals(N_ACTIVE))
            throw new IllegalStateException("The field has not been configured, added or had its initial value set yet");

        if (fieldChangeListeners.size() > 1 || (fieldChangeListeners.size() == 1 && !fieldChangeListeners.contains(notifyParentListener)))
            throw new IllegalStateException("Trying to delete and object that has change listeners attached");

        if (!dataFieldSchema.getDependantFields().isEmpty())
            throw new IllegalStateException("Trying to remove a field that still has dependant fields attached");

        // Unlink this object from others in the database
        this.removeChangeListener(notifyParentListener);
        if (DataObject.class.isAssignableFrom(dataFieldSchema.getType())) {
            if (dataFieldSchema.isTellParent()) {
                if (get() != null) {
                    ((DataObject) get()).notifyChildUnLink(getContainer());
                }
            }
        }

        // Detach the data core
        if (dataCore != null) {
            dataCore.detachFromField(this);
            dataCore = null;
        }

        this.state = N_REMOVED;
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
        if (!state.equals(N_INITIALIZED) && !state.equals(N_ACTIVE))
            throw new IllegalStateException("Cant get the value until its initially set");

        return value;
    }

    /**
     * Set the field value and perform what ever actions are required from the user or file
     *
     * @param toSet The value to set
     */
    public void set(FieldType toSet) {
        if (!dataFieldSchema.getCanEdit() && !state.equals(N_ATTACHED_TO_OBJECT))
            throw new IllegalStateException("This field can only be set once");

        if (dataFieldSchema.getSourceMode().equals(VIRTUAL_DERIVED)) {
            dataFieldSchema.getSetterFunction().set(toSet, getContainer());
            return;
        }

        if (dataFieldSchema.getSourceMode().equals(DERIVED))
            throw new IllegalStateException("Trying to set a field that is controlled by a data core");


        set_impl(toSet);
    }

    /**
     * Set the field value and perform what ever actions are required from a dataCore
     *
     * @param toSet The value to set
     */
    public void setFromDataCore(FieldType toSet) {
        if (dataFieldSchema.getSourceMode().equals(DataField_Schema.SourceMode.DIRECT))
            throw new IllegalStateException("A dataCore is trying to set a field controlled by the user");

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
        if (!state.equals(N_ACTIVE) && !state.equals(N_ATTACHED_TO_OBJECT) && !getState().equals(N_INITIALIZED))
            throw new IllegalStateException("Wrong state for setting a value");

        if (value instanceof DataObject) {
            if (!((DataObject) value).isAllValid())
                throw new IllegalStateException("Can not set an object that is not all valid");
        }

        set_preCheck(value);
        set_set(value);

        if (state.equals(N_ATTACHED_TO_OBJECT)) {
            this.state = N_INITIALIZED;
        }

        set_postSet();
    }

    /**
     * Validate the value to set
     *
     * @param toCheck The value to set
     */
    private void set_preCheck(FieldType toCheck) {
        if (state.equals(N_ACTIVE)) {
            if (!doFilterCheck(toCheck, value)) {
                throw new IllegalArgumentException("Attempting to set a invalid value");
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
    private void set_postSet() {
        getFieldChangeListeners().forEach(fieldChangeListener -> fieldChangeListener.valueChanged(this, oldValue, value));
    }

    /**
     * Check the value against the attached filters
     *
     * @param toCheck The value to check
     * @return True if the value is valid against all filters
     */
    @SuppressWarnings({"rawtypes", "unchecked", "BooleanMethodIsAlwaysInverted"})
    public boolean doFilterCheck(FieldType toCheck, FieldType pastValue) {
        for (FieldFilter filter : dataFieldSchema.getFilters()) {
            if (!filter.isValid(toCheck, pastValue, this.getContainer())) {
                return false;
            }
        }
        return true;
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

    public DataCore<FieldType> getDataCore() {
        return dataCore;
    }

    public boolean hasValidValue() {
        return getState().equals(N_INITIALIZED) || getState().equals(N_ACTIVE);
    }

    // Change Listeners ------------------------------------------------------------------------------------------------

    public List<FieldChangeListener<FieldType>> getFieldChangeListeners() {
        return fieldChangeListeners;
    }
}
