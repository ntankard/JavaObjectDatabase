package com.ntankard.javaObjectDatabase.dataField.dataCore;

import com.ntankard.javaObjectDatabase.dataField.DataField;

/**
 * A DataCore_Schema to for Static_DataCore
 *
 * @author Nicholas Tankard
 * @see DataCore_Schema
 */
public class Static_DataCore_Schema<FieldType> extends DataCore_Schema<FieldType> {

    /**
     * Interface to extract the static value during setup
     *
     * @param <FieldType>
     */
    public interface ValueGetter<FieldType> {

        /**
         * Get the static value
         *
         * @return The Static value
         */
        FieldType get(DataField<FieldType> dataField);
    }

    /**
     * The value the field should always have
     */
    private final FieldType value;

    /**
     * The source of the value the field should always have (only called once during setup)
     */
    private final ValueGetter<FieldType> valueGetter;

    /**
     * Constructor
     */
    public Static_DataCore_Schema(FieldType value) {
        this.value = value;
        this.valueGetter = null;
    }

    /**
     * Constructor
     */
    public Static_DataCore_Schema(ValueGetter<FieldType> valueGetter) {
        this.valueGetter = valueGetter;
        this.value = null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Static_DataCore<FieldType> createCore(DataField<FieldType> dataField) {
        return new Static_DataCore<>(this, dataField);
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public FieldType getValue() {
        return value;
    }

    public ValueGetter<FieldType> getValueGetter() {
        return valueGetter;
    }
}
