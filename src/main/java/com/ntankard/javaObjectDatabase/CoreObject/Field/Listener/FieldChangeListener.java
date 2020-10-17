package com.ntankard.javaObjectDatabase.CoreObject.Field.Listener;

import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;

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
