package com.ntankard.javaObjectDatabase.dataField.dataCore.derived;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A DataCore that calculates its value based on the value of other objects and fields in the system. Values are
 * recalculated whenever one of the "Sources" driving this core are changed.
 *
 * @author Nicholas Tankard
 * @see DataCore
 */
public class Derived_DataCore<FieldType, ContainerType extends DataObject>
        extends DataCore<FieldType, Derived_DataCore_Schema<FieldType, ContainerType>> {

    /**
     * The possible states of the Derived_DataCore
     */
    private enum DataCoreState {
        SETUP,                  // The dataCore is under construction so all notifications should ber ignored
        WAITING_FIRST_VALID,    // The dataCore is constructed but all the sources are not valid yet. THis state is only valid while the field is under construction
        ACTIVE,                 // The dataCore is fully setup and all sources are now valid and should stay that way
    }

    /**
     * The current state of the DataCore
     */
    private DataCoreState state;

    /**
     * The sources that drives this field
     */
    private Source<?, ?>[] sources;

    /**
     * @see DataCore#DataCore(DataCore_Schema, DataField)
     */
    protected Derived_DataCore(Derived_DataCore_Schema<FieldType, ContainerType> schema, DataField<FieldType> dataField) {
        super(schema, dataField);
        this.state = DataCoreState.SETUP;

        // Create all the sources
        sources = new Source[schema.getSourcesFactories().length];
        int i = 0;
        for (Source_Schema<?> factory : schema.getSourcesFactories()) {
            sources[i++] = factory.createRootSource(this);
        }

        this.state = DataCoreState.WAITING_FIRST_VALID;
        recalculate();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void detachFromField() {
        for (Source<?, ?> source : sources) {
            source.detach();
        }
        sources = null;
        state = null;
        super.detachFromField();
    }

    /**
     * Should a source recalculate individually if it can? Only true if a full calculation has been done at least once
     * so that incremental changes will work
     *
     * @return True if the should should do an incremental recalculation
     */
    public boolean canIncrementalCalculate() {
        if (!state.equals(DataCoreState.ACTIVE)) {
            return false;
        }
        assert getDataField().hasValidValue();
        assert canCalculate();
        return true;
    }

    /**
     * Fully recalculate the value of the field. Should be called when any source changes that does not have the ability
     * to do an incremental recalculation
     */
    @SuppressWarnings("unchecked")
    public void recalculate() {
        if (canCalculate()) {
            doSet(getSchema().getCalculator().reCalculate((ContainerType) getDataField().getContainer()));
            state = DataCoreState.ACTIVE;
        }
    }

    /**
     * Is the system ready for a calculation? Only true if the DataCore is setup and all the sources are valid. Should
     * only be false at the start before the field is fully active
     *
     * @return True if the DataCore is ready for recalculation
     */
    private boolean canCalculate() {
        if (state.equals(DataCoreState.SETUP)) {
            // Still under construction, ignore all call backs from the sources
            return false;
        }
        for (Source<?, ?> source : sources) {
            if (!source.isValid()) {
                // At least 1 of the sources is not ready, this should only happen at the start and should never happen again
                assert !getDataField().hasValidValue();
                assert state.equals(DataCoreState.WAITING_FIRST_VALID);
                return false;
            }
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Source<?, ?>[] getSources() {
        return sources;
    }
}
