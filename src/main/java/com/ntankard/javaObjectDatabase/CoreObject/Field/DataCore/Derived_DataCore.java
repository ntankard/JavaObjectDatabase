package com.ntankard.dynamicGUI.CoreObject.Field.DataCore;

import com.ntankard.dynamicGUI.CoreObject.CoreObject;
import com.ntankard.dynamicGUI.CoreObject.Field.DataField;
import com.ntankard.dynamicGUI.CoreObject.Field.Listener.FieldChangeListener;

public class Derived_DataCore<T, L extends CoreObject> extends ValueRead_DataCore<T> {

    /**
     * The method that gets the result object from the source
     */
    private final Converter<T, L> converter;

    /**
     * THe sources that drive this field
     */
    private final Source<T>[] sources;

    /**
     * Constructor
     */
    @SuppressWarnings("unchecked")
    public Derived_DataCore(DirectExternalSource<T, ?> source) {
        super(false);
        this.converter = null;
        this.sources = new Source[1];
        this.sources[0] = source;
        source.parent = this;
    }

    /**
     * Constructor
     */
    @SafeVarargs
    public Derived_DataCore(Converter<T, L> converter, Source<T>... sources) {
        super(false);
        this.converter = converter;
        this.sources = sources;
        for (Source<T> source : sources) {
            source.setParent(this);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Setup #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc
     */
    @Override
    public void attachToField(DataField<T> dataField) {
        super.attachToField(dataField);
        for (Source<T> source : sources) {
            source.attach();
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void detachFromField(DataField<T> field) {
        super.detachFromField(field);
        for (Source<T> source : sources) {
            source.detach();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################### DataCore Properties ##############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc
     */
    @Override
    public boolean canInitialSet() {
        return false;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Implementation ################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Recalculate the result object
     */
    @SuppressWarnings("unchecked")
    public void recalculate() {
        for (Source<T> source : sources) {
            if (!source.isReady()) {
                return;
            }
        }
        if (getDataField().getState().equals(DataField.NewFieldState.N_ATTACHED_TO_OBJECT)) {
            initialSet(converter.convert((L) getDataField().getContainer()));
        } else {
            set_impl(converter.convert((L) getDataField().getContainer()));
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface used to generate the value from the derived sources
     */
    public interface Converter<T, L extends CoreObject> {
        T convert(L container);
    }

    /**
     * A source of data used to derive the cores value
     */
    public abstract static class Source<ResultType> {

        /**
         * The data core this is attached to
         */
        protected Derived_DataCore<ResultType, ?> parent = null;

        /**
         * Set the data core this is attached to
         *
         * @param parent The data core this is attached to
         */
        private void setParent(Derived_DataCore<ResultType, ?> parent) {
            this.parent = parent;
        }

        /**
         * Is the source ready to be derived from?
         *
         * @return True if the source ready to be derived from
         */
        public abstract boolean isReady();

        /**
         * Attach the change listeners
         */
        public abstract void attach();

        /**
         * Detach the change listeners
         */
        public abstract void detach();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A source that is another field linked tot he same object
     */
    public static class LocalSource<ResultType, SourceType> extends Source<ResultType> implements FieldChangeListener<SourceType> {

        /**
         * The field containing the value that is the source of our new value
         */
        protected DataField<SourceType> sourceObjectField;

        /**
         * Constructor
         */
        public LocalSource(DataField<SourceType> sourceObjectField) {
            this.sourceObjectField = sourceObjectField;
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void valueChanged(DataField<SourceType> field, SourceType oldValue, SourceType newValue) {
            parent.recalculate();
        }

        /**
         * {@inheritDoc
         */
        @Override
        public boolean isReady() {
            return sourceObjectField.getState().equals(DataField.NewFieldState.N_INITIALIZED) || sourceObjectField.getState().equals(DataField.NewFieldState.N_ACTIVE);
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void attach() {
            sourceObjectField.addChangeListener(this);
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void detach() {
            sourceObjectField.removeChangeListener(this);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A source that is a field in another object that is linked to by a field in this object
     */
    public static class ExternalSource<ResultType, SourceContainerType extends CoreObject, SourceType> extends Source<ResultType> implements FieldChangeListener<SourceType> {

        /**
         * The field containing the value that is the source of our new value
         */
        protected DataField<SourceContainerType> sourceContainerField;

        /**
         * The last know source object
         */
        protected SourceContainerType containerObject;

        /**
         * The listener to determine is the source object changes
         */
        protected FieldChangeListener<SourceContainerType> sourceContainerChangeListener;

        /**
         * The field inside the source object used to get the final value
         */
        protected String fieldName;

        /**
         * Constructor
         */
        public ExternalSource(DataField<SourceContainerType> sourceContainerField, String sourceFieldName) {
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
                sourceChanged();
            };
        }

        /**
         * Attach the change listeners
         */
        public void attach() {
            this.sourceContainerField.addChangeListener(sourceContainerChangeListener);
            if (sourceContainerField.get() != null) {
                sourceContainerChangeListener.valueChanged(null, null, sourceContainerField.get());
            }
        }

        /**
         * Detach the change listeners
         */
        public void detach() {
            this.sourceContainerField.removeChangeListener(sourceContainerChangeListener);
            if (containerObject != null) {
                containerObject.<SourceType>getField(fieldName).removeChangeListener(this);
            }
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void valueChanged(DataField<SourceType> field, SourceType oldValue, SourceType newValue) {
            sourceChanged();
        }

        /**
         * {@inheritDoc
         */
        @Override
        public boolean isReady() {
            return sourceContainerField.getState().equals(DataField.NewFieldState.N_INITIALIZED) || sourceContainerField.getState().equals(DataField.NewFieldState.N_ACTIVE);
        }

        /**
         * Called when the source has changes
         */
        protected void sourceChanged() {
            parent.recalculate();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A source that is a field in another object that is linked to by a field in this object. The field is the other object is the direct result
     */
    public static class DirectExternalSource<ResultType, SourceContainerType extends CoreObject> extends ExternalSource<ResultType, SourceContainerType, ResultType> {

        public interface ValueModifier<ResultType> {
            ResultType modify(ResultType original);
        }

        /**
         * A modifier to use if the value is not exactly the same, if null the exact value is returned
         */
        private final ValueModifier<ResultType> valueModifier;

        /**
         * Constructor
         */
        public DirectExternalSource(DataField<SourceContainerType> sourceObjectField, String fieldName) {
            this(sourceObjectField, fieldName, null);
        }

        /**
         * Constructor
         */
        public DirectExternalSource(DataField<SourceContainerType> sourceContainerField, String sourceFieldName, ValueModifier<ResultType> valueModifier) {
            super(sourceContainerField, sourceFieldName);
            this.valueModifier = valueModifier;
        }

        /**
         * {@inheritDoc
         */
        @Override
        protected void sourceChanged() {
            ResultType toSet;
            if (valueModifier != null) {
                toSet = valueModifier.modify((containerObject.<ResultType>getField(fieldName)).get());
            } else {
                toSet = (containerObject.<ResultType>getField(fieldName)).get();
            }

            if (parent.getDataField().getState().equals(DataField.NewFieldState.N_ATTACHED_TO_OBJECT)) {
                parent.initialSet(toSet);
            } else {
                parent.set_impl(toSet);
            }
        }
    }
}
