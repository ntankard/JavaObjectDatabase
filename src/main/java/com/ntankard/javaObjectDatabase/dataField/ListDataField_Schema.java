package com.ntankard.javaObjectDatabase.dataField;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.exception.nonCorrupting.NonCorruptingException;

import java.util.List;

/**
 * All static data necessary top create a ListDataField object
 *
 * @param <FieldType> The type of data stored the list the field holds
 * @author Nicholas Tankard
 */
public class ListDataField_Schema<FieldType> extends DataField_Schema<List<FieldType>> {

    /**
     * @see DataField_Schema#DataField_Schema(String, Class)
     */
    @SuppressWarnings("unchecked")
    public ListDataField_Schema(String name, Class<? extends List<FieldType>> type) {
        super(name, (Class<List<FieldType>>) type, false);
    }

    /**
     * @inheritDoc
     */
    @Override
    public DataField<List<FieldType>> generate(DataObject container) {
        return new ListDataField<>(this, container);
    }

    @Override
    public void setSetterFunction(SetterFunction<List<FieldType>> setterFunction) {
        throw new NonCorruptingException("Setter functions can no be used on List type fields");
    }
}
