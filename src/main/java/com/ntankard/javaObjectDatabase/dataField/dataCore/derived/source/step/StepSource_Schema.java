package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.step;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.ListDataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * A Source_Schema to for Step_Source and ListStep_Source
 *
 * @author Nicholas Tankard
 * @see Source_Schema
 */
public class StepSource_Schema<EndFieldType> extends Source_Schema<EndFieldType> {

    /**
     * The Schema that can be used to create the next source in the chain
     */
    private final Source_Schema<?> childSourceSchema;

    /**
     * @param childSourceSchema The Schema that can be used to create the next source in the chain
     * @see Source_Schema#Source_Schema(String)
     */
    public StepSource_Schema(String sourceContainerFieldKey, Source_Schema<?> childSourceSchema, IndividualCalculator<EndFieldType> individualCalculator) {
        super(sourceContainerFieldKey, individualCalculator);
        this.childSourceSchema = childSourceSchema;
    }

    /**
     * @param childSourceSchema The Schema that can be used to create the next source in the chain
     * @see Source_Schema#Source_Schema(String, IndividualCalculator)
     */
    public StepSource_Schema(String sourceContainerFieldKey, Source_Schema<EndFieldType> childSourceSchema) {
        super(sourceContainerFieldKey);
        this.childSourceSchema = childSourceSchema;
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Source<?, StepSource_Schema<EndFieldType>> createRootSource(Derived_DataCore<?, ?> parentDataCore) {
        DataField<?> attachedField = parentDataCore.getDataField().getContainer().getField(getAttachedFieldKey());
        Source source;

        if (ListDataField.class.isAssignableFrom(attachedField.getClass())) {
            source = new ListStep_Source(this, attachedField, parentDataCore, null);
        } else {
            assert DataObject.class.isAssignableFrom(attachedField.getDataFieldSchema().getType());
            source = new Step_Source(this, attachedField, parentDataCore, null);
        }

        source.attach();
        return source;
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Source<?, StepSource_Schema<EndFieldType>> createChildSource(DataObject attachedFieldContainer, Source<?, ?> parentSource) {
        assert getIndividualCalculator() == null;
        DataField<?> attachedField = attachedFieldContainer.getField(getAttachedFieldKey());
        Source source;

        if (ListDataField.class.isAssignableFrom(attachedField.getClass())) {
            source = new ListStep_Source(this, attachedField, null, parentSource);
        } else {
            assert DataObject.class.isAssignableFrom(attachedField.getDataFieldSchema().getType());
            source = new Step_Source(this, attachedField, null, parentSource);
        }

        return source;
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Source_Schema<?> getChildSourceSchema() {
        return childSourceSchema;
    }
}
