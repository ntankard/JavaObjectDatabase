package com.ntankard.javaObjectDatabase.coreObject.field;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;

import java.util.List;


public class ListDataField_Schema<FieldType> extends DataField_Schema<List<FieldType>> {

    /**
     * Constructor
     */
    @SuppressWarnings("unchecked")
    public ListDataField_Schema(String name, Class<? extends List<FieldType>> type) {
        super(name, (Class<List<FieldType>>) type, false);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void setManualCanEdit(Boolean canEdit) {
        throw new IllegalStateException("A list data field can not be edited");
    }

    /**
     * {@inheritDoc
     */
    @Override
    public DataField<List<FieldType>> generate(DataObject blackObject) {
        return new ListDataField<>(this, blackObject);
    }
}
