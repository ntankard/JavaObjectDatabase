package com.ntankard.javaObjectDatabase.dataField.validator;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A generic interface to check if a valid is valid for a certain field
 *
 * @param <ToTestType>    The type of object that will need to be checked
 * @param <ContainerType> The type of the container of the field that houses this filter
 * @author Nicholas Tankard
 */
public interface FieldValidator<ToTestType, ContainerType extends DataObject> {

    /**
     * Check that a newValue is valid fora  given field
     *
     * @param newValue  The newValue to check
     * @param pastValue The past or current value
     * @param container The object that contains the field this filter is attached to
     * @return True if the newValue is valid
     */
    boolean isValid(ToTestType newValue, ToTestType pastValue, ContainerType container);
}
