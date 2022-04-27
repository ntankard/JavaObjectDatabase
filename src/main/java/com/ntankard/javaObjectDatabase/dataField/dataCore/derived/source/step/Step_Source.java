package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.step;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.ListDataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;

/**
 * A link in a source chain connecting to the final end source or other steps. This source is only for single and not list links.
 *
 * @author Nicholas Tankard
 * @see Source
 */
public class Step_Source<SourceEndType, AttachedFieldType extends DataObject> extends Source<AttachedFieldType, Step_Source_Schema<SourceEndType>> {

    /**
     * The next Source below this one in the chain, may be the last one or another step
     */
    private Source<?, ?> lowerSource;

    /**
     * @see Source#Source(Source_Schema, DataField, Derived_DataCore, Source)
     */
    protected Step_Source(Step_Source_Schema<SourceEndType> schema, DataField<AttachedFieldType> attachedField, Derived_DataCore<?, ?> parentDataCore, Source<?, ?> parentSource) {
        super(schema, attachedField, parentDataCore, parentSource);
        assert !ListDataField.class.isAssignableFrom(attachedField.getClass());
    }

    /**
     * @inheritDoc
     */
    @Override
    public DataField<AttachedFieldType> getDestinationField() {
        return getAttachedField();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void valueChanged(DataField<AttachedFieldType> field, AttachedFieldType oldValue, AttachedFieldType newValue) {
        suppress = true;
        Object toSendOld = null;
        Object toSendNew = null;
        try {
            if (oldValue != null) {
                assert oldValue == lowerSource.getAttachedField().getContainer();
                toSendOld = lowerSource.getEndFieldValue();
                lowerSource.detach();
                lowerSource = null;
            }
            if (newValue != null) {
                lowerSource = getSchema().getChildSourceSchema().createChildSource(newValue, this);
                assert newValue == lowerSource.getAttachedField().getContainer();
                toSendNew = lowerSource.getEndFieldValue();
            }
            suppress = false;
            sourceChanged(toSendOld, toSendNew);
        } finally {
            suppress = false;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object getEndFieldValue() {
        if (lowerSource != null) {
            return lowerSource.getEndFieldValue();
        }
        if (getAttachedField().getDataFieldSchema().isCanBeNull() && getAttachedField().hasValidValue()) {
            return null;
        }
        throw new CorruptingException(getAttachedField().getContainer().getTrackingDatabase(), "lowerSource is null when it should not be");
    }

    /**
     * @inheritDoc
     */
    @Override
    public void detach() {
        super.detach();
        if (lowerSource != null) {
            lowerSource.detach();
            lowerSource = null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isValid() {

        // Is the attached field still invalid? (This should only be possible on the top level)
        if (!getAttachedField().hasValidValue()) {
            assert isTop();
            return false;
        }

        // If the field can be null and is null then this is valid even without a child source
        if (getAttachedField().getDataFieldSchema().isCanBeNull() && getAttachedField().get() == null) {
            assert lowerSource == null;
            return true;
        }

        // I am valid, confirm that the child is as well (should always be)
        if (lowerSource != null) {
            return lowerSource.isValid();
        }

        // Normal you should never get here but there is one special case. If a DataCore has multiple sources attached
        // who all have the same first step source then when that source field is set it will be valid but then has to
        // call all the sources via a change listener one by 1. The first one that is called will trigger a full check
        // of all the other sources so this method will get invoked AFTER the source field has become valid but BEFORE
        // this source specifically has been called by the change listener
        assert isTop();
        assert confirmMultiSourceSet();
        return false;
    }

    /**
     * Confirm that this source is attached to a DataCore with multiple sources and that at least one of those sources
     * first step points to the same field and this source
     *
     * @return True if another Source in the DataCore has the same attached field
     */
    private boolean confirmMultiSourceSet() {
        for (Source<?, ?> source : getParentDataCore().getSources()) {
            if (source != this) {
                if (source.getSchema().getAttachedFieldKey().equals(getSchema().getAttachedFieldKey())) {
                    return true;
                }
            }
        }
        return false;
    }
}
