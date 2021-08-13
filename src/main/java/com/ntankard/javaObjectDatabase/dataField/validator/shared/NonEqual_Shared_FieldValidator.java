package com.ntankard.javaObjectDatabase.dataField.validator.shared;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A NonEqual_Shared_FieldValidator is used to ensure that multiple fields do not get given the same value. A
 * combination of fields being null can be made invalid or valid as needed. By default multiple fields can be null.
 *
 * @author Nicholas Tankard
 */
public class NonEqual_Shared_FieldValidator extends Multi_FieldValidator<DataObject> {

    /**
     * Constructor
     *
     * @param keys The key for all the fields that can not be the same
     */
    public NonEqual_Shared_FieldValidator(String... keys) {
        this(true, keys);
    }

    /**
     * Constructor
     *
     * @param canBothBeNull If multiple values are null, is this valid? (NOTE: this validator does not check if null values are allowed in the field itself)
     * @param keys          The key for all the fields that can not be the same
     */
    public NonEqual_Shared_FieldValidator(boolean canBothBeNull, String... keys) {
        super((newValues, pastValues, container) -> {
            for (int i = 0; i < newValues.length; i++) {
                for (int j = i + 1; j < newValues.length; j++) {
                    if (newValues[i] != null) {
                        if (newValues[i].equals(newValues[j])) {
                            return false;
                        }
                    } else {
                        if (newValues[j] == null && !canBothBeNull) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }, "Fields can not be equal", keys);
    }
}
