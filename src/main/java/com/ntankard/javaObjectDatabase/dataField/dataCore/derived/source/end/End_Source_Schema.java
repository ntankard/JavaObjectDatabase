package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.end;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A Source_Schema to for End_Source
 *
 * @author Nicholas Tankard
 * @see Source_Schema
 */
public class End_Source_Schema<EndFieldType> extends Source_Schema<EndFieldType> {

    /**
     * @see Source_Schema#Source_Schema(String)
     */
    public End_Source_Schema(String sourceContainerFieldKey) {
        super(sourceContainerFieldKey);
    }

    /**
     * @see Source_Schema#Source_Schema(String, IndividualCalculator)
     */
    public End_Source_Schema(String sourceContainerFieldKey, IndividualCalculator<EndFieldType> individualCalculator) {
        super(sourceContainerFieldKey, individualCalculator);
    }

    /**
     * @inheritDoc
     */
    @Override
    public End_Source<EndFieldType> createRootSource(Derived_DataCore<?, ?> parentDataCore) {
        assert parentDataCore != null;
        return new End_Source<>(this, parentDataCore.getDataField().getContainer().getField(getAttachedFieldKey()), parentDataCore, null);
    }

    /**
     * @inheritDoc
     */
    @Override
    public End_Source<EndFieldType> createChildSource(DataObject attachedFieldContainer, Source<?, ?> parentSource) {
        assert attachedFieldContainer != null;
        assert parentSource != null;
        assert getIndividualCalculator() == null;
        return new End_Source<>(this, attachedFieldContainer.getField(getAttachedFieldKey()), null, parentSource);
    }
}
