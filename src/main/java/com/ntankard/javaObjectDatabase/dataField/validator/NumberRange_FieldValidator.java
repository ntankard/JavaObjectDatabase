package com.ntankard.javaObjectDatabase.dataField.validator;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A validator that checks if a value is withing a certain range. If null is given for either bound then it is
 * considered unbound in that direction. This validator requires that the field is not null
 *
 * @param <NumberType>    The type of number to compare
 * @param <ContainerType> The type of the container of the field that houses this filter
 * @author Nicholas Tankard
 */
public class NumberRange_FieldValidator<NumberType extends Number & Comparable<NumberType>, ContainerType extends DataObject> implements FieldValidator<NumberType, ContainerType> {

    /**
     * The minimum value, null if not needed
     */
    private final NumberType min;

    /**
     * The maximum value, null if not needed
     */
    private final NumberType max;

    /**
     * Constructor
     *
     * @param min The minimum value that can be set or null if there is no lower limit
     * @param max The maximum value that can be set or null if there is no upper limit
     */
    public NumberRange_FieldValidator(NumberType min, NumberType max) {
        if (max == null && min == null)
            throw new IllegalArgumentException("Both bounds cannot be null");
        if (min != null && max != null && min.compareTo(max) >= 0)
            throw new IllegalArgumentException("Min must be less than max");

        this.min = min;
        this.max = max;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isValid(NumberType newValue, NumberType pastValue, ContainerType container) {
        if (newValue == null) {
            return true;
        }
        if (min != null && newValue.compareTo(min) < 0) {
            return false;
        }
        return max == null || newValue.compareTo(max) <= 0;
    }
}
