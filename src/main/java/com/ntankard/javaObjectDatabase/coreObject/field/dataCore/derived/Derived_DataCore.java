package com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.DataCore;
import com.ntankard.javaObjectDatabase.coreObject.field.DataField;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived.source.DirectExternalSource;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived.source.DirectExternalSource.DirectExternalSource_Factory;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.derived.source.Source.Source_Factory;

public class Derived_DataCore<FieldType, ContainerType extends DataObject> extends DataCore<FieldType> {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Factory ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public static class Derived_DataCore_Factory<FieldType, ContainerType extends DataObject> extends DataCore_Factory<FieldType, Derived_DataCore<FieldType, ContainerType>> {

        /**
         * Single source factory when DirectExternalSource is used
         */
        private final DirectExternalSource_Factory<FieldType, ContainerType> sourceFactory;

        /**
         * The method that gets the result object from the source
         */
        private final Calculator<FieldType, ContainerType> calculator;

        /**
         * The sources that drives this field
         */
        private final Source_Factory<FieldType, ?>[] sources;

        /**
         * Constructor
         */
        public Derived_DataCore_Factory(Calculator<FieldType, ContainerType> calculator, Source_Factory<FieldType, ?>... sources) {
            this.calculator = calculator;
            this.sources = sources;
            this.sourceFactory = null;
        }

        /**
         * Constructor
         */
        public Derived_DataCore_Factory(DirectExternalSource_Factory<FieldType, ContainerType> sourceFactory) {
            this.calculator = null;
            this.sources = null;
            this.sourceFactory = sourceFactory;
        }

        /**
         * {@inheritDoc
         */
        @SuppressWarnings({"unchecked", "ConstantConditions"})
        @Override
        public Derived_DataCore<FieldType, ContainerType> createCore(DataField<FieldType> container) {
            if (sourceFactory != null) {
                return new Derived_DataCore<>(sourceFactory.createSource(container));
            } else {
                Source<FieldType>[] sourceInstances = new Source[sources.length];
                int i = 0;
                for (Source_Factory<FieldType, ?> factory : sources) {
                    sourceInstances[i++] = factory.createSource(container);
                }
                return new Derived_DataCore<>(calculator, sourceInstances);
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Core DataCore #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The method that gets the result object from the source
     */
    private final Calculator<FieldType, ContainerType> calculator;

    /**
     * The sources that drives this field
     */
    private final Source<FieldType>[] sources;

    /**
     * Constructor
     */
    public Derived_DataCore(DirectExternalSource<FieldType, ContainerType> source) {
        this(source.getCalculator(), source);
    }

    /**
     * Constructor
     */
    @SafeVarargs
    public Derived_DataCore(Calculator<FieldType, ContainerType> calculator, Source<FieldType>... sources) {
        this.calculator = calculator;
        this.sources = sources;

        if (sources.length == 0) {
            throw new IllegalArgumentException("At least 1 source is required");
        }

        for (Source<FieldType> source : sources) {
            if (source instanceof DirectExternalSource) {
                if (sources.length != 1) {
                    throw new IllegalArgumentException("DirectExternalSource can only be used if there is one source");
                }
            }
        }

        for (Source<FieldType> source : sources) {
            source.setParent(this);
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void startInitialSet() {
        for (Source<FieldType> source : sources) {
            source.attach();
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void detachFromField(DataField<FieldType> field) {
        for (Source<FieldType> source : sources) {
            source.detach();
        }
        super.detachFromField(field);
    }

    /**
     * Recalculate the result object
     */
    @SuppressWarnings("unchecked")
    public void recalculate() {
        for (Source<FieldType> source : sources) {
            if (!source.isReady()) {
                return;
            }
        }
        doSet(calculator.reCalculate((ContainerType) getDataField().getContainer()));
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface used to generate the value from the derived sources
     */
    public interface Calculator<T, L extends DataObject> {

        /**
         * One of the sources has changed, recalculate the value
         *
         * @param container The container that owns this field
         * @return The value to set
         */
        T reCalculate(L container);
    }
}
