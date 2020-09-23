package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.ListDataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;

/**
 * A source that is a field in another object that is linked to by a field in this object
 */
public class ExternalSource<ResultType, SourceContainerType extends DataObject, SourceType> extends Source<ResultType> implements FieldChangeListener<SourceType> {

    /**
     * The field containing the value that is the source of our new value
     */
    private final DataField<SourceContainerType> sourceContainerField;

    /**
     * The last know source object
     */
    private SourceContainerType containerObject;

    /**
     * The listener to determine is the source object changes
     */
    private final FieldChangeListener<SourceContainerType> sourceContainerChangeListener;

    /**
     * The field inside the source object used to get the final value
     */
    private final String fieldName;

    /**
     * Constructor
     */
    public ExternalSource(DataField<SourceContainerType> sourceContainerField, String sourceFieldName) {
        if (ListDataField.class.isAssignableFrom(sourceContainerField.getClass()))
            throw new IllegalArgumentException("sourceContainerField can not be of type ListDataField");

        this.sourceContainerField = sourceContainerField;
        this.fieldName = sourceFieldName;
        this.sourceContainerChangeListener = (field, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.<SourceType>getField(sourceFieldName).removeChangeListener(this);
            }
            if (newValue != null) {
                newValue.<SourceType>getField(sourceFieldName).addChangeListener(this);
            }
            containerObject = newValue;
            doRecalculate();
        };
    }

    /**
     * Attach the change listeners
     */
    protected void attach_impl() {
        this.sourceContainerField.addChangeListener(sourceContainerChangeListener);
        if (sourceContainerField.hasValidValue()) {
            sourceContainerChangeListener.valueChanged(null, null, sourceContainerField.get());
        }
    }

    /**
     * Detach the change listeners
     */
    protected void detach_impl() {
        this.sourceContainerField.removeChangeListener(sourceContainerChangeListener);
        if (containerObject != null) {
            containerObject.<SourceType>getField(fieldName).removeChangeListener(this);
        }
        containerObject = null;
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected boolean isReady_impl() {
        if (sourceContainerField.hasValidValue()) {
            if (sourceContainerField.isCanBeNull() && sourceContainerField.get() == null) {
                return true;
            }
            if (containerObject == null) {
                return false;
            }
            if (!containerObject.getField(fieldName).hasValidValue())
                throw new IllegalStateException("This should not be possible. It means that the containerObject is not not valid. This should not have been allowed to be set to the container object");

            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void valueChanged(DataField<SourceType> field, SourceType oldValue, SourceType newValue) {
        doRecalculate();
    }

    /**
     * Get the last know source object
     *
     * @return The last know source object
     */
    protected SourceContainerType getContainerObject() {
        return containerObject;
    }

    /**
     * Get the field inside the source object used to get the final value
     *
     * @return The field inside the source object used to get the final value
     */
    protected String getFieldName() {
        return fieldName;
    }
}
