package com.ntankard.javaObjectDatabase.CoreObject.Field.Filter;

import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField_Schema;
import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

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

    /**
     * Called when this filter is attached to a field, run all required attachments now
     *
     * @param field the field i was attached to
     */
    public void attachedToField(DataField_Schema<T> field) {
    }

    /**
     * Called when this filter is detached from a field. Clean up all attachments
     *
     * @param field The field i was removed from
     */
    public void detachedFromField(DataField_Schema<T> field) {
    }
}
