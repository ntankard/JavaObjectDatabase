package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Listener.FieldChangeListener;

/**
 * A source that is another field linked tot he same object
 */
public class LocalSource<ResultType, SourceType> extends Source<ResultType> implements FieldChangeListener<SourceType> {

    /**
     * The field containing the value that is the source of our new value
     */
    private final DataField<SourceType> sourceObjectField;

    /**
     * Constructor
     */
    public LocalSource(DataField<SourceType> sourceObjectField) {
        if (sourceObjectField == null)
            throw new IllegalArgumentException("sourceObjectField cannot be null");

        this.sourceObjectField = sourceObjectField;
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected void attach_impl() {
        sourceObjectField.addChangeListener(this);
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected void detach_impl() {
        sourceObjectField.removeChangeListener(this);
    }

    /**
     * {@inheritDoc
     */
    @Override
    protected boolean isReady_impl() {
        return sourceObjectField.hasValidValue();
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void valueChanged(DataField<SourceType> field, SourceType oldValue, SourceType newValue) {
        doRecalculate();
    }
}
