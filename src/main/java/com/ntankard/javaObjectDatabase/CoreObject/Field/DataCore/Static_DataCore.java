package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore;

import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;

public class Static_DataCore<FieldType> extends DataCore<FieldType> {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Factory ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public static class Static_DataCore_Factory<FieldType> extends DataCore_Factory<FieldType, Static_DataCore<FieldType>> {

        /**
         * The value the fired should always have
         */
        private final FieldType value;

        /**
         * The source of the value the field should always have (only called once during setup
         */
        private final ValueGetter<FieldType> valueGetter;

        /**
         * Constructor
         */
        public Static_DataCore_Factory(FieldType value) {
            this.value = value;
            this.valueGetter = null;
        }

        /**
         * Constructor
         */
        public Static_DataCore_Factory(ValueGetter<FieldType> valueGetter) {
            this.valueGetter = valueGetter;
            this.value = null;
        }

        /**
         * {@inheritDoc
         */
        @Override
        public Static_DataCore<FieldType> createCore(DataField<FieldType> container) {
            if (valueGetter == null) {
                return new Static_DataCore<>(value);
            } else {
                return new Static_DataCore<>(valueGetter);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Core DataCore #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The value the fired should always have
     */
    private final FieldType value;

    /**
     * The source of the value the field should always have (only called once during setup
     */
    private final ValueGetter<FieldType> valueGetter;

    /**
     * Constructor
     */
    public Static_DataCore(FieldType value) {
        this.value = value;
        this.valueGetter = null;
    }

    /**
     * Constructor
     */
    public Static_DataCore(ValueGetter<FieldType> valueGetter) {
        this.valueGetter = valueGetter;
        this.value = null;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void startInitialSet() {
        if (valueGetter != null) {
            doSet(valueGetter.get(getDataField()));
        } else {
            doSet(value);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface to extract the static value during setup
     *
     * @param <T>
     */
    public interface ValueGetter<T> {

        /**
         * Get the static value
         *
         * @return The Static value
         */
        T get(DataField<T> dataField);
    }
}
