package com.ntankard.javaObjectDatabase.dataObject;

/**
 * An interface for validating an object attached to a schema, This will be called when the DataObject_Schema is being
 * finalised
 *
 * @author Nicholas Tankard
 */
public interface ValidatableSchema {

    /**
     * Confirms that you this object is valid in regards to the schema it is attached to. If it is not it will throw an
     * appropriate exception
     *
     * @param dataObject_schema The Schema this object is attached to
     */
    void validateToAttachedSchema(DataObject_Schema dataObject_schema);
}
