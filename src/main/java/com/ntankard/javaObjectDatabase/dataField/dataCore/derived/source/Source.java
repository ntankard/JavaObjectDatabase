package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.listener.FieldChangeListener;

/**
 * A source of data that can be used to drive a DataCore either by notifying it of changes or by making incremental
 * changes directly. Each source is attached to a DataField and can be linked together in a chain to get to data several
 * objects away.
 *
 * @param <AttachedFieldType> The type of data stored in the attached field
 * @param <SchemaType>        The type of Schema used to generate this source
 * @author Nicholas Tankard
 * @see Source_Schema
 */
public abstract class Source<AttachedFieldType, SchemaType extends Source_Schema<?>> implements FieldChangeListener<AttachedFieldType> {

    /**
     * The Schema describing the behavior of this Source
     */
    protected SchemaType schema;

    /**
     * The field this source listens too
     */
    protected DataField<AttachedFieldType> attachedField;

    /**
     * The data core this is attached to. Null if parentSource is set
     */
    protected Derived_DataCore<?, ?> parentDataCore;

    /**
     * The Source this Source is attached to. Null if parentDataCore is set
     */
    protected Source<?, ?> parentSource;

    /**
     * A flag to indicate if notifications of changes from lower sources should be ignored. This is activated when this
     * source is undergoing a change. It also guarantees that this source will take responsibility for picking up the
     * change once the flag is turned off
     */
    protected boolean suppress;

    /**
     * Constructor
     *
     * @param schema         The Schema describing the behavior of this Source
     * @param attachedField  The field this source listens too
     * @param parentDataCore The data core this is attached to. Null if parentSource is set
     * @param parentSource   The Source this Source is attached to. Null if parentDataCore is set
     */
    protected Source(SchemaType schema, DataField<AttachedFieldType> attachedField, Derived_DataCore<?, ?> parentDataCore, Source<?, ?> parentSource) {
        assert schema != null;
        assert attachedField != null;
        assert parentDataCore != null || parentSource != null;
        assert !(parentDataCore != null && parentSource != null);

        this.suppress = false;
        this.schema = schema;
        this.attachedField = attachedField;
        this.parentDataCore = parentDataCore;
        this.parentSource = parentSource;

        this.attachedField.addChangeListener(this);
        if (this.attachedField.hasValidValue()) {
            valueChanged(this.attachedField, null, this.attachedField.get());
        }
    }

    /**
     * Detach this source and all its children from there attached fields and clear up and other links. The source can
     * not be used after calling this
     */
    public void detach() {
        this.attachedField.removeChangeListener(this);

        this.schema = null;
        this.attachedField = null;
        this.parentDataCore = null;
        this.parentSource = null;
    }

    /**
     * Is the source ready to be derived from? Do all fields involved have valid values?
     *
     * @return True if the source ready to be derived from
     */
    public abstract boolean isValid();

    /**
     * Call when the source value has changed. Either because the lowest level has changed or a parent has resulting in
     * a new lowest source being attached
     */
    protected void sourceChanged(Object oldValue, Object newValue) {
        if (!suppress) {                                        // Notifications are not being stopped
            if (isTop()) {                                          // Top of the source chain
                assert isValid();
                if (schema.getIndividualCalculator() != null) {         // Individual calculation supported
                    if (!parentDataCore.canIncrementalCalculate()) {         // Full recalculation has not been run once
                        parentDataCore.recalculate();
                    } else {                                                // Full recalculation has been run once, update individual only
                        schema.doIndividualRecalculate(parentDataCore, oldValue, newValue);
                    }
                } else {                                                // Individual calculation not supported
                    parentDataCore.recalculate();
                }
            } else {                                                // Step in the source chain, forward on the update
                parentSource.sourceChanged(oldValue, newValue);
            }
        }
    }

    /**
     * Get the current value of the lowest source in the chain (the watched value)
     *
     * @return The current value of the lowest source in the chain
     */
    public abstract Object getEndFieldValue();

    /**
     * Is this the top of the chain? (Directly attached to the DataCore)
     *
     * @return True if this the top of the chain
     */
    protected boolean isTop() {
        return parentDataCore != null;
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public DataField<AttachedFieldType> getAttachedField() {
        assert attachedField != null; // This can be null if called before attach
        return attachedField;
    }

    public Derived_DataCore<?, ?> getParentDataCore() {
        assert isTop();
        return parentDataCore;
    }

    public SchemaType getSchema() {
        return schema;
    }
}
