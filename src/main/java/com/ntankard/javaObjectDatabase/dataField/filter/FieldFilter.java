package com.ntankard.javaObjectDatabase.dataField.filter;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * @param <T>             The type of object that will need to be checked
 * @param <ContainerType> The type of the container of the field that houses this filter
 */
public abstract class FieldFilter<T, ContainerType extends DataObject> {

    /**
     * Check that a newValue is valid fora  given field
     *
     * @param newValue  The newValue to check
     * @param pastValue The past or current value
     * @param container The object that contains the field this filter is attached to
     * @return True if the newValue is valid
     */
    public abstract boolean isValid(T newValue, T pastValue, ContainerType container);
}
