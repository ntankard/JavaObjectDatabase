package com.ntankard.javaObjectDatabase.dataField.dataCore;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.ListDataField;

/**
 * A object used to drive data in a dataField. This is used for any application where the value is not directly set by
 * the user
 *
 * @param <FieldType>  The type of data in the field this core will be attached to
 * @param <SchemaType> The type of Schema used to generate this core
 * @author Nicholas Tankard
 */
public abstract class DataCore<FieldType, SchemaType extends DataCore_Schema<FieldType>> {

    /**
     * The Schema describing the behavior of this Core
     */
    private SchemaType schema;

    /**
     * The field containing this data core
     */
    private DataField<FieldType> dataField;

    /**
     * Constructor
     *
     * @param schema    The Schema describing the behavior of this Core
     * @param dataField The field containing this data core
     */
    protected DataCore(SchemaType schema, DataField<FieldType> dataField) {
        assert schema != null;
        assert dataField != null;
        this.schema = schema;
        this.dataField = dataField;
    }

    /**
     * Called when this filter is detached from a field. Clean up all attachments
     */
    public void detachFromField() {
        this.schema = null;
        this.dataField = null;
    }

    /**
     * Set the value in the dataField
     *
     * @param toSet The value to set
     */
    protected void doSet(FieldType toSet) {
        getDataField().setFromDataCore(toSet);
    }

    /**
     * Add the value to the data field. The Field must be of type ListDataField
     *
     * @param toAdd The value to add
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void doAdd(Object toAdd) {
        this.<ListDataField>getDataField().addFromDataCore(toAdd);
    }

    /**
     * Remove the value to the data field. The Field must be of type ListDataField
     *
     * @param toRemove The value to remove
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void doRemove(Object toRemove) {
        this.<ListDataField>getDataField().removeFromDataCore(toRemove);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <DataFieldType extends DataField<FieldType>> DataFieldType getDataField() {
        return (DataFieldType) dataField;
    }

    public SchemaType getSchema() {
        return schema;
    }
}
