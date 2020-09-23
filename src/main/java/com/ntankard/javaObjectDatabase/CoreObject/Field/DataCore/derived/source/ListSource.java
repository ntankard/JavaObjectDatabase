package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.ListDataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;

import java.util.Collections;
import java.util.List;

public class ListSource<ResultType, ListType extends DataObject> extends Source<ResultType> implements FieldChangeListener<List<ListType>> {

    /**
     * The list data field
     */
    private final ListDataField<ListType> listField;

    /**
     * The fields to watch in each of the items in the list
     */
    private final String[] fieldsToWatch;

    /**
     * The internal listener when one of the fieldsToWatch changes
     */
    private final FieldChangeListener<Object> internalListener;

    /**
     * Constructor
     */
    public ListSource(ListDataField<ListType> listField, String... fieldsToWatch) {
        this.listField = listField;
        this.fieldsToWatch = fieldsToWatch;
        this.internalListener = (field, oldValue, newValue) -> doRecalculate();
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected boolean isReady_impl() {
        return listField.hasValidValue();
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected void attach_impl() {
        listField.addChangeListener(this);
        if (listField.hasValidValue()) {
            for (ListType dataObject : listField.get()) {
                valueChanged(listField, null, Collections.singletonList(dataObject));
            }
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected void detach_impl() {
        listField.removeChangeListener(this);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void valueChanged(DataField<List<ListType>> field, List<ListType> oldValue, List<ListType> newValue) {
        for (String fieldName : fieldsToWatch) {
            if (newValue != null) {
                for (Object dataObject : newValue) {
                    ((DataObject) dataObject).getField(fieldName).addChangeListener(internalListener);
                }
            }
            if (oldValue != null) {
                for (Object dataObject : oldValue) {
                    ((DataObject) dataObject).getField(fieldName).removeChangeListener(internalListener);
                }
            }
        }
        doRecalculate();
    }
}
