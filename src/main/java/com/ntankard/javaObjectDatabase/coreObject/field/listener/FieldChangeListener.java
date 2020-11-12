package com.ntankard.javaObjectDatabase.coreObject.field.listener;

import com.ntankard.javaObjectDatabase.coreObject.field.DataField;

public interface FieldChangeListener<T> {

    /**
     * Called when the value is changed
     *
     * @param field    The field that changed
     * @param oldValue The past value
     * @param newValue The new value
     */
    void valueChanged(DataField<T> field, T oldValue, T newValue);
}
