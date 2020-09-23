package com.ntankard.javaObjectDatabase.CoreObject.Field;

public abstract class DataCore<T> {

    /**
     * The field containing this data core
     */
    private DataField<T> dataField = null;

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Setup #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Called when this filter is attached to a field. It is safe to call subscriptions at this point
     *
     * @param dataField The field this object was attached to
     */
    public void attachToField(DataField<T> dataField) {
        this.dataField = dataField;
    }

    /**
     * Called when this filter is detached from a field. Clean up all attachments
     *
     * @param field The field this object was removed from
     */
    public void detachFromField(DataField<T> field) {
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
    protected void doSet(T toSet) {
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
    public DataField<T> getDataField() {
        return dataField;
    }
}
