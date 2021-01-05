package com.ntankard.javaObjectDatabase.dataField.dataCore;

import com.ntankard.javaObjectDatabase.dataField.DataField;

/**
 * All static data necessary top create a DataCore object
 *
 * @param <FieldType> The type of data in the field this core will be attached to
 * @author Nicholas Tankard
 */
public abstract class DataCore_Schema<FieldType> {

    /**
     * Create a stand alone instance of DataCore that can have state information
     *
     * @param dataField The DataField this will be attached to
     * @return A stand alone instance of DataCore that can have state information
     */
    public abstract DataCore<FieldType, ?> createCore(DataField<FieldType> dataField);
}
