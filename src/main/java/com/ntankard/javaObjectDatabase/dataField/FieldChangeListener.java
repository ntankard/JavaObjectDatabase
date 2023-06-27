package com.ntankard.javaObjectDatabase.dataField;

/**
 * An interface for a method to be called when a value in the a DataField changes. If the DataField its a ListDataField,
 * it will work the same as a regular field when the entire list is set. When data is added or removed from the list it
 * will call valueChanged list of added or removed items.
 *
 * @author Nicholas Tankard
 * @see DataField
 * @see ListDataField
 */
public interface FieldChangeListener<T> {

    /**
     * Get the field that will be notified when a change occurs (who is listening to the change)
     *
     * @return The field that will be notified when a change occurs (who is listening to the change)
     */
    DataField<T> getDestinationField();

    /**
     * Called when the value is changed
     *
     * @param field    The field that changed
     * @param oldValue The past value, or a list of removed values if attached to a ListDataField
     * @param newValue The new value, or a list of added values if attached to a ListDataField
     */
    void valueChanged(DataField<T> field, T oldValue, T newValue);
}
