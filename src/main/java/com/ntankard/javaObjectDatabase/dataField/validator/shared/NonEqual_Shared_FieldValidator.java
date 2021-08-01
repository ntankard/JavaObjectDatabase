package com.ntankard.javaObjectDatabase.dataField.validator.shared;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A NonEqual_Shared_FieldValidator used to ensure 2 fields to not get given the same value. Both fields being null can
 * be made invalid or valid as needed. By default both fields can be null.
 *
 * @author Nicholas Tankard
 */
public class NonEqual_Shared_FieldValidator extends Shared_FieldValidator<Object, Object, DataObject> {

    /**
     * Constructor
     *
     * @param firstFieldKey  The key for the first field this filter applies to (attach getFirstFilter() to this field as well)
     * @param secondFieldKey The key for the second field this filter applies to (attach getSecondFilter() to this field as well)
     */
    public NonEqual_Shared_FieldValidator(String firstFieldKey, String secondFieldKey) {
        this(firstFieldKey, secondFieldKey, true);
    }

    /**
     * Constructor
     *
     * @param firstFieldKey  The key for the first field this filter applies to (attach getFirstFilter() to this field as well)
     * @param secondFieldKey The key for the second field this filter applies to (attach getSecondFilter() to this field as well)
     * @param canBothBeNull  If both values are null, is this valid? (NOTE: this validator does not check if null values are allowed in the field itself)
     */
    public NonEqual_Shared_FieldValidator(String firstFieldKey, String secondFieldKey, boolean canBothBeNull) {
        super(firstFieldKey, secondFieldKey, (firstNewValue, firstPastValue, secondNewValue, secondPastValue, container) -> {
            if (firstNewValue != null) {
                return !firstNewValue.equals(secondNewValue);
            } else if (!canBothBeNull) {
                return secondNewValue != null;
            }
            return true;
        }, "The value of the " + firstFieldKey + " field can not be same as the " + secondFieldKey + " field" + (canBothBeNull ? " unless both are null" : ""));
    }
}
