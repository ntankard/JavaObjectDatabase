package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.end;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;

/**
 * The last source in the chain that directly links to the desired data
 *
 * @author Nicholas Tankard
 * @see Source
 */
public class End_Source<EndFieldType> extends Source<EndFieldType, End_Source_Schema<EndFieldType>> {

    /**
     * @see Source#Source(Source_Schema, DataField, Derived_DataCore, Source)
     */
    protected End_Source(End_Source_Schema<EndFieldType> schema, DataField<EndFieldType> attachedField, Derived_DataCore<?, ?> parentDataCore, Source<?, ?> parentSource) {
        super(schema, attachedField, parentDataCore, parentSource);
    }

    /**
     * @inheritDoc
     */
    @Override
    public DataField<EndFieldType> getDestinationField() {
        return getAttachedField();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void valueChanged(DataField<EndFieldType> field, EndFieldType oldValue, EndFieldType newValue) {
        sourceChanged(oldValue, newValue);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isValid() {
        // Only the top can be false depending on load order, everything lower should always be valid
        if (isTop()) {
            return getAttachedField().hasValidValue();
        } else {
            assert getAttachedField().hasValidValue();
            return true;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object getEndFieldValue() {
        if (isValid()) {
            return getAttachedField().get();
        }
        return null;
    }
}
