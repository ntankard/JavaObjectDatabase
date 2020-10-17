package com.ntankard.javaObjectDatabase.CoreObject.Field;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

import java.util.List;


public class ListDataField<FieldType> extends DataField<List<FieldType>> {

    /**
     * Constructor
     */
    @SuppressWarnings("unchecked")
    public ListDataField(String name, Class<? extends List<FieldType>> type) {
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
    public DataField_Instance<List<FieldType>> generate(DataObject blackObject) {
        return new ListDataField_Instance<>(this, blackObject);
    }
}
