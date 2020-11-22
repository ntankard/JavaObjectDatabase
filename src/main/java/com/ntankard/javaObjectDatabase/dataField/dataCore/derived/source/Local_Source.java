package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.listener.FieldChangeListener;

/**
 * A source that is another field linked tot he same object
 */
public class Local_Source<ResultType, SourceType> extends Source<ResultType> implements FieldChangeListener<SourceType> {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Factory ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public static class LocalSource_Factory<ResultType, SourceType> extends Source_Factory<ResultType, Local_Source<ResultType, SourceType>> {

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
        public Local_Source<ResultType, SourceType> createSource(DataField<ResultType> container) {
            return new Local_Source<>(container.getContainer().getField(sourceObjectFieldKey));
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
    public Local_Source(DataField<SourceType> sourceObjectField) {
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
