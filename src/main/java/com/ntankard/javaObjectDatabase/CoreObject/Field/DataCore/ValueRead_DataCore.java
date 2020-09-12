package com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore;

import com.ntankard.javaObjectDatabase.CoreObject.Field.Filter.FieldFilter;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;

public class ValueRead_DataCore<T> extends DataCore<T> {

    /**
     * The current value of the field
     */
    private T value;

    /**
     * The most recent previous value
     */
    private T oldValue;

    /**
     * Can the value be edited after it is initially set?
     */
    private final boolean canEdit;

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public ValueRead_DataCore() {
        this(false);
    }

    /**
     * Constructor
     */
    public ValueRead_DataCore(boolean canEdit) {
        this.canEdit = canEdit;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Data Access ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc
     */
    @Override
    public T get() {
        return value;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void set(T toSet) {
        if (canEdit()) {
            set_impl(toSet);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void initialSet(T toSet) {
        set_impl(toSet);
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################### DataCore Properties ##############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc
     */
    @Override
    public boolean canEdit() {
        return canEdit;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean canInitialSet() {
        return true;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean doseSupportChangeListeners() {
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Implementation ################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Set the field value and perform what ever actions are required
     *
     * @param value The value to set
     */
    protected void set_impl(T value) {
        set_preCheck(value);
        set_preSet();
        set_set(value);
        set_postSet();
        set_postCheck();
    }

    /**
     * Validate the value to set
     *
     * @param value Teh value to set
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void set_preCheck(T value) {
        for (FieldFilter filter : getDataField().getFilters()) {
            if (!filter.isValid(value, this.getDataField().getContainer())) {
                throw new IllegalArgumentException("Attempting to set a invalid value");
            }
        }
    }

    /**
     * Perform what ever actions are required before setting a new value (remove old value)
     */
    protected void set_preSet() {
    }

    /**
     * Set the value
     *
     * @param value The value to set
     */
    protected void set_set(T value) {
        this.oldValue = this.value;
        this.value = value;
    }

    /**
     * Perform what ever actions are required after setting a new value (register, notify ect)
     */
    protected void set_postSet() {
        for (FieldChangeListener<T> fieldChangeListener : getDataField().getFieldChangeListeners()) {
            fieldChangeListener.valueChanged(getDataField(), oldValue, value);
        }
    }

    /**
     * Validate the field with the new value
     */
    protected void set_postCheck() {
    }
}
