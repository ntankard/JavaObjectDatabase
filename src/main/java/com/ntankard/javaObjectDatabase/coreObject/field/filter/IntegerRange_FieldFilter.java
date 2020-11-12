package com.ntankard.javaObjectDatabase.coreObject.field.filter;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;

public class IntegerRange_FieldFilter<ContainerType extends DataObject> extends FieldFilter<Integer, ContainerType> {

    /**
     * The minimum value, null if not needed
     */
    private final Integer min;

    /**
     * The maximum value, null if not needed
     */
    private final Integer max;

    /**
     * Constructor
     */
    public IntegerRange_FieldFilter(Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public boolean isValid(Integer newValue, Integer pastValue, ContainerType container) {
        if (min != null && newValue < min) return false;
        return max == null || newValue <= max;
    }
}
