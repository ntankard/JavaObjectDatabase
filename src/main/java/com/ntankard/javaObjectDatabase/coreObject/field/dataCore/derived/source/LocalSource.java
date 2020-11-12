package com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.coreObject.field.DataField;
import com.ntankard.javaObjectDatabase.coreObject.field.listener.FieldChangeListener;

/**
 * A source that is another field linked tot he same object
 */
public class LocalSource<ResultType, SourceType> extends Source<ResultType> implements FieldChangeListener<SourceType> {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Factory ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public static class LocalSource_Factory<ResultType, SourceType> extends Source_Factory<ResultType, LocalSource<ResultType, SourceType>> {

        /**
         * The key for the field containing the value that is the source of our new value
         */
        private final String sourceObjectFieldKey;

        /**
         * Constructor
         */
        public LocalSource_Factory(String sourceObjectFieldKey) {
            this.sourceObjectFieldKey = sourceObjectFieldKey;
        }

        /**
         * {@inheritDoc
         */
        @Override
        public LocalSource<ResultType, SourceType> createSource(DataField<ResultType> container) {
            return new LocalSource<>(container.getContainer().getField(sourceObjectFieldKey));
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Core Source ###################################################
    //------------------------------------------------------------------------------------------------------------------

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
