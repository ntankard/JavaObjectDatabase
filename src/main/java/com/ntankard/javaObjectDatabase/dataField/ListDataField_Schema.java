package com.ntankard.javaObjectDatabase.dataField;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

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
     * @inheritDoc
     */
    @Override
    public DataField<List<FieldType>> generate(DataObject blackObject) {
        return new ListDataField<>(this, blackObject);
    }
}