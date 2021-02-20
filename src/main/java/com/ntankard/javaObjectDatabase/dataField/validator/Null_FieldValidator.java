package com.ntankard.javaObjectDatabase.dataField.validator;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A validator that checks if a field is null
 *
 * @param <ToTestType>    The type of object that will need to be checked
 * @param <ContainerType> The type of the container of the field that houses this filter
 * @author Nicholas Tankard
 */
public class Null_FieldValidator<ToTestType, ContainerType extends DataObject> implements FieldValidator<ToTestType, ContainerType> {

    /**
     * Can the field be set to null?
     */
    private final boolean canBeNull;

    /**
     * Constructor
     *
     * @param canBeNull Can the field be null
     */
    public Null_FieldValidator(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isValid(ToTestType newValue, ToTestType pastValue, ContainerType container) {
        return canBeNull || newValue != null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getValidatorDetails() {
        return this.getClass().getSimpleName();
    }
}
