package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived;

import com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.source.DirectExternalSource;
import com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.source.Source;

public class Derived_DataCore<FieldType, ContainerType extends DataObject> extends DataCore<FieldType> {

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
