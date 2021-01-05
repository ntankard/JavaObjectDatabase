package com.ntankard.javaObjectDatabase.dataField.dataCore;

import com.ntankard.javaObjectDatabase.dataField.DataField;

/**
 * A DataCore that provides a fixed static value when setup that never changes
 *
 * @author Nicholas Tankard
 * @see DataCore
 */
public class Static_DataCore<FieldType> extends DataCore<FieldType, Static_DataCore_Schema<FieldType>> {

    /**
     * Constructor
     */
    protected Static_DataCore(Static_DataCore_Schema<FieldType> schema, DataField<FieldType> dataField) {
        super(schema, dataField);
        if (schema.getValueGetter() != null) {
            doSet(schema.getValueGetter().get(getDataField()));
        } else {
            doSet(schema.getValue());
        }
    }
}
