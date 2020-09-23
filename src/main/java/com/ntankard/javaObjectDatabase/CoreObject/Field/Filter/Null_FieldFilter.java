package com.ntankard.javaObjectDatabase.CoreObject.Field.Filter;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

public class Null_FieldFilter<T, ContainerType extends DataObject> extends FieldFilter<T, ContainerType> {

    /**
     * Can the field be set to null?
     */
    private final Boolean canBeNull;

    /**
     * Constructor
     */
    public Null_FieldFilter(Boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean isValid(T newValue, T pastValue, ContainerType container) {
        if (!canBeNull) {
            return newValue != null;
        }
        return true;
    }

    /**
     * Get can the field be set to null?
     *
     * @return Can the field be set to null?
     */
    public Boolean getCanBeNull() {
        return canBeNull;
    }
}
