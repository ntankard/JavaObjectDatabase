package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.step;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.ListDataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

import java.util.*;

/**
 * A link in a source chain connecting to the final end source or other steps. This source is only for list links not
 * single ones. Each object in the list will have its own step source added.
 *
 * @param <EndFieldType> The type of data stored in the last field of the source chain
 * @author Nicholas Tankard
 * @see Source
 */
public class ListStep_Source<EndFieldType, AttachedFieldType extends List<? extends DataObject>> extends Source<AttachedFieldType, StepSource_Schema<EndFieldType>> {

    /**
     * The next Sources below this one in the chain, may be the last one or another step
     */
    private final Map<DataObject, Source<?, ?>> lowerSources = new HashMap<>();

    /**
     * @see Source#Source(Source_Schema, DataField, Derived_DataCore, Source)
     */
    protected ListStep_Source(StepSource_Schema<EndFieldType> schema, DataField<AttachedFieldType> attachedField, Derived_DataCore<?, ?> parentDataCore, Source<?, ?> parentSource) {
        super(schema, attachedField, parentDataCore, parentSource);
        assert ListDataField.class.isAssignableFrom(attachedField.getClass());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void valueChanged(DataField<AttachedFieldType> field, AttachedFieldType oldValue, AttachedFieldType newValue) {
        ArrayList<Object> toSendOld = null;
        ArrayList<Object> toSendNew = null;

        if (oldValue != null) {
            toSendOld = new ArrayList<>();
            for (DataObject dataField : oldValue) {
                Object toRemove = lowerSources.get(dataField).getEndFieldValue();
                if (Collection.class.isAssignableFrom(toRemove.getClass())) {
                    toSendOld.addAll((Collection<?>) toRemove);
                } else {
                    toSendOld.add(toRemove);
                }
                assert lowerSources.containsKey(dataField);
                lowerSources.get(dataField).detach();
                lowerSources.remove(dataField);
            }
        }
        if (newValue != null) {
            toSendNew = new ArrayList<>();
            for (DataObject dataField : newValue) {
                assert !lowerSources.containsKey(dataField);
                lowerSources.put(dataField, schema.getChildSourceSchema().createChildSource(dataField, this));
                lowerSources.get(dataField).attach();
                Object toAdd = lowerSources.get(dataField).getEndFieldValue();
                if (Collection.class.isAssignableFrom(toAdd.getClass())) {
                    toSendNew.addAll((Collection<?>) toAdd);
                } else {
                    toSendNew.add(toAdd);
                }
            }
        }

        sourceChanged(toSendOld, toSendNew);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object getEndFieldValue() {
        List<Object> toReturn = new ArrayList<>();

        for (Map.Entry<DataObject, Source<?, ?>> entry : lowerSources.entrySet()) {
            Object toAdd = entry.getValue().getEndFieldValue();
            if (Collection.class.isAssignableFrom(toAdd.getClass())) {
                toReturn.addAll((Collection<?>) toAdd);
            } else {
                toReturn.add(toAdd);
            }
        }
        return toReturn;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void detach() {
        super.detach();
        List<DataObject> toTest = new ArrayList<>(lowerSources.keySet());
        for (DataObject key : toTest) {
            lowerSources.get(key).detach();
            lowerSources.remove(key);
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
            assert lowerSources.size() == 0;
            return true;
        }

        // I am valid, confirm that the child is as well (should always be)
        for (Map.Entry<DataObject, Source<?, ?>> entry : lowerSources.entrySet()) {
            if (!entry.getValue().isValid()) {
                return false;
            }
        }
        return true;
    }
}
