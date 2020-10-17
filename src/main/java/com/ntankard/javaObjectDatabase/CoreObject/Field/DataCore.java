package com.ntankard.javaObjectDatabase.CoreObject.Field;

public abstract class DataCore<FieldType> {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Factory ####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * A factory to create DataCore object that can contain state information
     */
    public static abstract class DataCore_Factory<FieldType, DataCoreType extends DataCore<FieldType>> {

        /**
         * Create a stand alone instance of DataCore that can have state information
         *
         * @param container The DataField this will be attached to
         * @return A stand alone instance of DataCore that can have state information
         */
        public abstract DataCoreType createCore(DataField_Instance<FieldType> container);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Core DataCore #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The field containing this data core
     */
    private DataField_Instance<FieldType> dataField = null;

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Setup #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Called when this filter is attached to a field. It is safe to call subscriptions at this point
     *
     * @param dataField The field this object was attached to
     */
    public void attachToField(DataField_Instance<FieldType> dataField) {
        this.dataField = dataField;
    }

    /**
     * Called when this filter is detached from a field. Clean up all attachments
     *
     * @param field The field this object was removed from
     */
    public void detachFromField(DataField_Instance<FieldType> field) {
        this.dataField = null;
    }

    /**
     * This is called instead of initialSet if canInitialSet is false. It is used to initialise a internal value if needed
     */
    public abstract void startInitialSet();

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Implementation #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Set the value in the dataField
     *
     * @param toSet The value to set
     */
    protected void doSet(FieldType toSet) {
        getDataField().setFromDataCore(toSet);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the field containing this data core
     *
     * @return The field containing this data core
     */
    public DataField_Instance<FieldType> getDataField() {
        return dataField;
    }
}
