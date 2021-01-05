package com.ntankard.javaObjectDatabase.dataField.dataCore.derived;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A DataCore_Schema to for Derived_DataCore
 *
 * @param <ContainerType> The type of object the field that this core it attached to, is attached to
 * @author Nicholas Tankard
 * @see DataCore_Schema
 */
public class Derived_DataCore_Schema<FieldType, ContainerType extends DataObject> extends DataCore_Schema<FieldType> {

    /**
     * Interface used to generate the value from the derived sources
     *
     * @param <FieldType>     The type of data in the field this core will be attached to
     * @param <ContainerType> The type of object the field that this core it attached to, is attached to
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
        assert calculator != null;
        assert sourcesFactories.length != 0;
        this.calculator = calculator;
        this.sourcesFactories = sourcesFactories;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Derived_DataCore<FieldType, ContainerType> createCore(DataField<FieldType> dataField) {
        return new Derived_DataCore<>(this, dataField);
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Calculator<FieldType, ContainerType> getCalculator() {
        return calculator;
    }

    public Source_Schema<?>[] getSourcesFactories() {
        return sourcesFactories;
    }
}
