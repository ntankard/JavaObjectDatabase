package com.ntankard.javaObjectDatabase.dataField.properties;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;

/**
 * An interface to create the default version of any properties to add add to the fields
 *
 * @author Nicholas Tankard
 */
public interface PropertyBuilder {

    /**
     * Add the property to this field
     *
     * @param dataFieldSchema The field to add the property too
     */
    void attachProperty(DataField_Schema<?> dataFieldSchema);
}
