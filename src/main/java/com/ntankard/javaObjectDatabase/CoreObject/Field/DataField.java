package com.ntankard.javaObjectDatabase.CoreObject.Field;

import com.ntankard.javaObjectDatabase.CoreObject.Field.Filter.FieldFilter;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Filter.Null_FieldFilter;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Properties.Display_Properties;
import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.Derived_DataCore;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ntankard.javaObjectDatabase.CoreObject.Field.DataField.NewFieldState.*;
import static com.ntankard.javaObjectDatabase.CoreObject.Field.DataField.SourceMode.*;

public class DataField<T> {

    // Core Data -------------------------------------------------------------------------------------------------------

    /**
     * The name of the Field. This must be unique and is used to identify and save the field
     */
    private final String identifierName;

    /**
     * The data type of the field (same as T)
     */
    private final Class<T> type;

    /**
     * Can the field be null?
     */
    private final boolean canBeNull;

    /**
     * Listener to register children and parents
     */
    private final FieldChangeListener<T> notifyParentListener;

    /**
     * The name to be displayed to the user, can be anything.
     */
    private final String displayName;

    // Field State -----------------------------------------------------------------------------------------------------

    public enum NewFieldState {
        N_BUILDING,             // Configuring the field
        N_ALL_FIELDS_FINISHED,  // All fields are finished and grouped into a container
        N_ATTACHED_TO_OBJECT,   // The field is attached to its container
        N_INITIALIZED,          // The field is fully setup and the initial value has been set
        N_ACTIVE,               // The object the field is attached to is in the database and in active use
        N_REMOVED               // The object this field is attached to has been removed, it can not be added again and should have all ties cut
    }

    public enum SourceMode {
        DIRECT,         // The field is controlled by the user
        DERIVED,        // The is set by other fields directly or indirectly
        VIRTUAL_DERIVED // The field is controlled another field but has a setter that performs an external function
    }

    /**
     * The mode the field is operating in
     */
    private SourceMode sourceMode;

    /**
     * The current state of the field
     */
    protected NewFieldState state;

    // Behavior Configuration Data -------------------------------------------------------------------------------------

    /**
     * A list of fields this one depends on (must be part of the same container as this one)
     */
    private final List<String> dependantFields = new ArrayList<>();

    /**
     * The fillers used to check the data
     */
    private final List<FieldFilter<T, ?>> filters = new ArrayList<>();

    /**
     * The engine to control the data
     */
    private DataCore<T> dataCore = null;

    /**
     * The properties to use when displaying the data
     */
    private final Display_Properties displayProperties = new Display_Properties();

    /**
     * The source of valid values that be used to set this field
     */
    private Method source = null;

    /**
     * If the field is operating in DIRECT mode, can the user change the value?
     */
    private boolean canEdit = false;

    /**
     * Should the parent be notified if this field links to it?
     */
    private boolean tellParent = true;

    /**
     * The method to call if we are in virtual mode when a manual set happens
     */
    private SetterFunction<T> setterFunction = null;

    // Change Listeners ------------------------------------------------------------------------------------------------

    /**
     * Objects to be notified when data changes
     */
    private final List<FieldChangeListener<T>> fieldChangeListeners = new ArrayList<>();

    // Properties Of Attached Object To --------------------------------------------------------------------------------

    /**
     * The class that contains this field
     */
    private DataObject container;

    /**
     * The type of object that contains this field
     */
    private Class<? extends DataObject> parentType;

    // Value of the field ----------------------------------------------------------------------------------------------

    /**
     * The current value of the field
     */
    protected T value = null;

    /**
     * The most recent previous value
     */
    private T oldValue = null;

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Pre State ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public DataField(String identifierName, Class<T> type) {
        this(identifierName, type, false);
    }

    /**
     * Constructor
     */
    public DataField(String identifierName, Class<T> type, Boolean canBeNull) {
        this.state = N_BUILDING;
        this.sourceMode = SourceMode.DIRECT;

        this.identifierName = identifierName;
        this.type = type;
        this.canBeNull = canBeNull;

        this.displayName = identifierName.replace("get", "").replace("is", "").replace("has", "");
        this.notifyParentListener = (field, oldValue, newValue) -> {
            if (DataObject.class.isAssignableFrom(field.getType())) {
                if (field.tellParent) {
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

        addFilter(new Null_FieldFilter<>(canBeNull));
        addChangeListener(notifyParentListener);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################### N_BUILDING ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Add a dependant field
     *
     * @param field The field this one will depend on
     */
    public void addDependantField(String field) {
        if (!getState().equals(N_BUILDING))
            throw new RuntimeException("Dependencies can only be added during setup");

        dependantFields.add(field);
    }

    /**
     * Add a new filter
     *
     * @param filter The filter to add
     */
    public void addFilter(FieldFilter<T, ?> filter) {
        if (!getState().equals(N_BUILDING))
            throw new RuntimeException("Fillers can only be added during setup");

        this.filters.add(filter);
        filter.attachedToField(this);
    }

    /**
     * Set the data core for the field. The old one is removed
     *
     * @param dataCore The field to set
     */
    public void setDataCore(DataCore<T> dataCore) {
        if (!getState().equals(N_BUILDING))
            throw new IllegalStateException("DataCore can only be set during setup");

        if (this.dataCore != null)
            throw new IllegalStateException("Cant set a data core after one has already been set");

        if (dataCore == null)
            throw new IllegalArgumentException("DataCore can not be null");

        if (DataObject.class.isAssignableFrom(getType())) {
            setSource(DataObject.getSourceOptionMethod());
        }
        dataCore.attachToField(this);

        this.dataCore = dataCore;

        if (setterFunction != null) {
            this.sourceMode = VIRTUAL_DERIVED;
        } else {
            this.sourceMode = DERIVED;
        }
    }

    /**
     * Set the source of valid values that be used to set this field
     *
     * @param source The source of valid values that be used to set this field
     */
    public void setSource(Method source) {
        if (!getState().equals(N_BUILDING))
            throw new IllegalStateException("DataCore can only be set during setup");
        this.source = source;
    }

    /**
     * Set should the parent be notified if this field links to it?
     *
     * @param tellParent Should the parent be notified if this field links to it?
     */
    public void setTellParent(boolean tellParent) {
        if (!getState().equals(N_BUILDING))
            throw new IllegalStateException("tellParent can only be set during setup");

        this.tellParent = tellParent;
    }

    /**
     * Set if the field is operating in DIRECT mode, can the user change the value?
     *
     * @param canEdit If the field is operating in DIRECT mode, can the user change the value?
     */
    public void setCanEdit(Boolean canEdit) {
        if (!getState().equals(N_BUILDING))
            throw new IllegalStateException("canEdit can only be set during setup");

        this.canEdit = canEdit;
    }

    /**
     * Set the method to call if we are in virtual mode when a manual set happens
     *
     * @param setterFunction The method to call if we are in virtual mode when a manual set happens
     */
    public void setSetterFunction(SetterFunction<T> setterFunction) {
        if (!getState().equals(N_BUILDING))
            throw new RuntimeException("setterFunction can only be added during setup");

        this.setterFunction = setterFunction;
        this.sourceMode = VIRTUAL_DERIVED;
    }

    // Transition ------------------------------------------------------------------------------------------------------

    /**
     * Called when all fields in a container are finished and added to the container
     *
     * @param parentType The type of object this fields belongs too
     */
    public void containerFinished(Class<? extends DataObject> parentType) {
        if (!getState().equals(N_BUILDING))
            throw new IllegalStateException("The field has not been completed yet, or has been finished more than once");

        if (setterFunction != null) {
            if (dataCore == null || !(dataCore instanceof Derived_DataCore)) {
                throw new IllegalStateException("A field with a setterFunction must have a derived dataCore");
            }
            if (sourceMode != VIRTUAL_DERIVED) {
                throw new IllegalStateException("SourceMode is wrong");
            }
        } else if (dataCore != null) {
            if (sourceMode != DERIVED) {
                throw new IllegalStateException("SourceMode is wrong");
            }
        } else {
            if (sourceMode != DIRECT) {
                throw new IllegalStateException("SourceMode is wrong");
            }
        }

        this.parentType = parentType;
        this.state = N_ALL_FIELDS_FINISHED;
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################# N_ALL_FIELDS_FINISHED ##############################################
    //------------------------------------------------------------------------------------------------------------------

    // Transition ------------------------------------------------------------------------------------------------------

    /**
     * Called when this field is linked to a container
     *
     * @param container The container to link it too
     */
    public void fieldAttached(DataObject container) {
        if (!getState().equals(N_ALL_FIELDS_FINISHED))
            throw new IllegalStateException("The field has not been added to the container yet or has already been added");

        if (!container.getClass().equals(parentType))
            throw new IllegalArgumentException("Trying to attache the field to the wrong type");

        this.container = container;
        this.state = N_ATTACHED_TO_OBJECT;
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################## N_ATTACHED_TO_OBJECT ##############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Call to allow a data core to start receiving values
     */
    public void allowValue() {
        if (!getState().equals(N_ATTACHED_TO_OBJECT))
            throw new IllegalStateException("The field must be attached to an object before data can flow");

        if (!sourceMode.equals(DIRECT)) {
            getDataCore().startInitialSet();
        }
    }

    // Set can be called now

    // Transition ------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------
    //################################################## N_INITIALIZED #################################################
    //------------------------------------------------------------------------------------------------------------------

    // Transition ------------------------------------------------------------------------------------------------------

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
        if (DataObject.class.isAssignableFrom(getType())) {
            if (tellParent) {
                if (value != null) {
                    ((DataObject) value).notifyChildLink(getContainer());
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### N_ACTIVE ####################################################
    //------------------------------------------------------------------------------------------------------------------

    // Transition ------------------------------------------------------------------------------------------------------

    /**
     * Remove this field from the database
     */
    public void remove() {
        if (!getState().equals(N_ACTIVE))
            throw new IllegalStateException("The field has not been configured, added or had its initial value set yet");

        if (fieldChangeListeners.size() > 1 || (fieldChangeListeners.size() == 1 && !fieldChangeListeners.contains(notifyParentListener)))
            throw new IllegalStateException("Trying to delete and object that has change listeners attached");

        if (!getDependantFields().isEmpty())
            throw new IllegalStateException("Trying to remove a field that still has dependant fields attached");

        // Unlink this object from others in the database
        this.removeChangeListener(notifyParentListener);
        if (DataObject.class.isAssignableFrom(getType())) {
            if (tellParent) {
                if (get() != null) {
                    ((DataObject) get()).notifyChildUnLink(getContainer());
                }
            }
        }

        // Detach child objects
        while (!getFilters().isEmpty()) {
            FieldFilter<T, ?> toRemove = getFilters().get(0);
            filters.remove(toRemove);
            toRemove.detachedFromField(this);
        }

        // Detach the data core
        if (dataCore != null) {
            dataCore.detachFromField(this);
            dataCore = null;
        }

        this.state = N_REMOVED;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Value Access ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the current value
     *
     * @return The current value
     */
    public T get() {
        if (!state.equals(N_INITIALIZED) && !state.equals(N_ACTIVE))
            throw new IllegalStateException("Cant get the value until its initially set");

        return value;
    }

    /**
     * Set the field value and perform what ever actions are required from the user or file
     *
     * @param toSet The value to set
     */
    public void set(T toSet) {
        if (!getCanEdit() && !state.equals(N_ATTACHED_TO_OBJECT))
            throw new IllegalStateException("This field can only be set once");

        if (sourceMode.equals(VIRTUAL_DERIVED)) {
            setterFunction.set(toSet, getContainer());
            return;
        }

        if (sourceMode.equals(DERIVED))
            throw new IllegalStateException("Trying to set a field that is controlled by a data core");


        set_impl(toSet);
    }

    /**
     * Set the field value and perform what ever actions are required from a dataCore
     *
     * @param toSet The value to set
     */
    void setFromDataCore(T toSet) {
        if (sourceMode.equals(SourceMode.DIRECT))
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
    protected void set_impl(T value) {
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
    private void set_preCheck(T toCheck) {
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
    private void set_set(T value) {
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
    public boolean doFilterCheck(T toCheck, T pastValue) {
        //if (state.equals(N_ACTIVE)) {
        for (FieldFilter filter : getFilters()) {
            if (!filter.isValid(toCheck, pastValue, this.getContainer())) {
                return false;
            }
        }
        //}
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Change Listener #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Add a new change listener to get called when a value changes
     *
     * @param fieldChangeListener The FieldChangeListener to add
     */
    public void addChangeListener(FieldChangeListener<T> fieldChangeListener) {
        this.fieldChangeListeners.add(fieldChangeListener);
    }

    /**
     * Remove a change listener
     *
     * @param fieldChangeListener The FieldChangeListener to remove
     */
    public void removeChangeListener(FieldChangeListener<T> fieldChangeListener) {
        this.fieldChangeListeners.remove(fieldChangeListener);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    // Core Data -------------------------------------------------------------------------------------------------------

    public String getIdentifierName() {
        return identifierName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isCanBeNull() {
        return canBeNull;
    }

    // Field State -----------------------------------------------------------------------------------------------------

    public NewFieldState getState() {
        return state;
    }

    public SourceMode getSourceMode() {
        return sourceMode;
    }

    // Behavior Configuration Data -------------------------------------------------------------------------------------

    public List<String> getDependantFields() {
        return dependantFields;
    }

    public List<FieldFilter<T, ?>> getFilters() {
        return filters;
    }

    public DataCore<T> getDataCore() {
        return dataCore;
    }

    public Display_Properties getDisplayProperties() {
        return displayProperties;
    }

    public Boolean getCanEdit() {
        if (sourceMode.equals(VIRTUAL_DERIVED)) {
            return true;
        }
        if (sourceMode.equals(DERIVED)) {
            return false;
        }
        return canEdit;
    }

    public Method getSource() {
        return source;
    }

    // Change Listeners ------------------------------------------------------------------------------------------------

    public List<FieldChangeListener<T>> getFieldChangeListeners() {
        return fieldChangeListeners;
    }

    // Properties Of Attached Object To --------------------------------------------------------------------------------

    public DataObject getContainer() {
        return container;
    }

    public Class<? extends DataObject> getParentType() {
        return parentType;
    }

    public boolean isTellParent() {
        return tellParent;
    }

    public boolean hasValidValue() {
        return getState().equals(N_INITIALIZED) || getState().equals(N_ACTIVE);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Object Methods #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataField<?> dataField = (DataField<?>) o;
        return getIdentifierName().equals(dataField.getIdentifierName()) &&
                getType().equals(dataField.getType());
    }

    /**
     * {@inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(getIdentifierName(), getType());
    }

    /**
     * {@inheritDoc
     */
    @Override
    public String toString() {
        if (!state.equals(N_INITIALIZED) && !state.equals(N_ACTIVE))
            return "Wrong state - " + state.toString();

        if (get() != null) {
            return identifierName + " - " + type.getSimpleName() + " - " + getState().toString() + " - " + get().toString();
        }
        return identifierName + " - " + type.getSimpleName() + " - " + getState().toString() + " - null";
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Interface ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface for virtual setter function
     */
    public interface SetterFunction<T> {

        /**
         * Called when the user invokes a the setter on a virtual field
         *
         * @param toSet     The values to set
         * @param container The object containing the field
         */
        void set(T toSet, DataObject container);
    }
}
