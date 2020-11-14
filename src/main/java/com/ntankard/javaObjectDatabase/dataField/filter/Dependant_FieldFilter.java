package com.ntankard.javaObjectDatabase.dataField.filter;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

public abstract class Dependant_FieldFilter<T, ContainerType extends DataObject> extends FieldFilter<T, ContainerType> {

    /**
     * The fields that this filter relies on to work, must have values before this filter is called
     */
    private final String[] requiredFields;

    /**
     * Constructor
     */
    public Dependant_FieldFilter(String... requiredFields) {
        this.requiredFields = requiredFields;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void attachedToField(DataField_Schema<T> field) {
        for (String requiredField : requiredFields) {
            field.addDependantField(requiredField);
        }
        super.attachedToField(field);
    }
}
