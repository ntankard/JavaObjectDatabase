package com.ntankard.javaObjectDatabase.dataField.dataCore.derived;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

public class Derived_DataCore<FieldType, ContainerType extends DataObject> extends DataCore<FieldType> {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Factory ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public static class Derived_DataCore_Schema<FieldType, ContainerType extends DataObject> extends DataCore_Factory<FieldType, Derived_DataCore<FieldType, ContainerType>> {

        /**
         * The method that gets the result object from the source
         */
        private final Calculator<FieldType, ContainerType> calculator;

        /**
         * The sources that drives this field
         */
        private final Source_Schema<?>[] sourcesFactories;

        /**
         * Constructor
         */
        public Derived_DataCore_Schema(Calculator<FieldType, ContainerType> calculator, Source_Schema<?>... sourcesFactories) {
            this.calculator = calculator;
            this.sourcesFactories = sourcesFactories;
            if (sourcesFactories.length == 0) {
                throw new IllegalArgumentException("At least 1 source is required");
            }
        }

        /**
         * @inheritDoc
         */
        @Override
        public Derived_DataCore<FieldType, ContainerType> createCore(DataField<FieldType> container) {
            return new Derived_DataCore<>(this);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Core DataCore #################################################
    //------------------------------------------------------------------------------------------------------------------

    private final Derived_DataCore_Schema<FieldType, ContainerType> factory;

    /**
     * The sources that drives this field
     */
    private Source<?, ?>[] sources;

    public Source<?, ?>[] getSources() {
        return sources;
    }

    /**
     * Constructor
     */
    public Derived_DataCore(Derived_DataCore_Schema<FieldType, ContainerType> factory) {
        this.factory = factory;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void startInitialSet() {

        Source<?, ?>[] sourceInstances = new Source[factory.sourcesFactories.length];
        int i = 0;
        for (Source_Schema<?> factory : factory.sourcesFactories) {
            sourceInstances[i++] = factory.createRootSource(this);
        }
        this.sources = sourceInstances;

        recalculate();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void detachFromField(DataField<FieldType> field) {
        for (Source<?, ?> source : sources) {
            source.detach();
        }
        super.detachFromField(field);
    }

    public boolean canCalculate() {
        if (sources == null) {
            return false;
        }
        for (Source<?, ?> source : sources) {
            if (!source.isValid()) {
                return false;
            }
        }
        return true;
    }

    public void recalculate() {
        recalculate(false);
    }

    /**
     * Recalculate the result object
     */
    @SuppressWarnings("unchecked")
    public void recalculate(boolean force) {
        if (!force && !canCalculate()) {
            return;
        }
        doSet(factory.calculator.reCalculate((ContainerType) getDataField().getContainer()));
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface used to generate the value from the derived sources
     */
    public interface Calculator<FieldType, ContainerType extends DataObject> {

        /**
         * One of the sources has changed, recalculate the value
         *
         * @param container The container that owns this field
         * @return The value to set
         */
        FieldType reCalculate(ContainerType container);
    }
}
