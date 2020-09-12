package com.ntankard.javaObjectDatabase.CoreObject.Field.Filter;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

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
    public boolean isValid(Integer value, ContainerType container) {
        if (min != null && value < min) return false;
        return max == null || value <= max;
    }
}
