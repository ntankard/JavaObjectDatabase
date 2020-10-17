package com.ntankard.javaObjectDatabase.CoreObject.Field.Listener;

import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField_Instance;

public interface FieldChangeListener<T> {

    /**
     * Called when the value is changed
     *
     * @param field    The field that changed
     * @param oldValue The past value
     * @param newValue The new value
     */
    void valueChanged(DataField_Instance<T> field, T oldValue, T newValue);
}
