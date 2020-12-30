package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

/**
 * All static data necessary top create a Source object
 *
 * @param <EndFieldType> The type of data stored in the last field of the source chain. The ultimate data type this
 *                       source chain is looking at
 * @author Nicholas Tankard
 */
public abstract class Source_Schema<EndFieldType> {

    /**
     * An interface to provide a method to recalculate a DataCore's values based on the change of one of the sources
     *
     * @param <EndFieldType> The type of data stored in the last field of the source chain. The ultimate data type
     *                       this source chain is looking at
     */
    public interface IndividualCalculator<EndFieldType> {

        /**
         * Recalculate the value of the DataCore based on the change of only 1 of the sources. This method must call set
         * as needed. No changes are made to the DataCore outside of what you implement.
         *
         * @param parent   The DataCore to drive
         * @param oldValue The old value/values
         * @param newValue The new value/values
         */
        void doIndividualRecalculate(Derived_DataCore<EndFieldType, ?> parent, EndFieldType oldValue, EndFieldType newValue);
    }

    /**
     * The key for the field this source will attach to
     */
    private final String attachedFieldKey;

    /**
     * The method to recalculate the fields value based on the change of this source chain. This can only be attached
     * to the top Source in the chain
     */
    private final IndividualCalculator<EndFieldType> individualCalculator;

    /**
     * Constructor, setting individualCalculator to null
     *
     * @param attachedFieldKey The key for the field this source will attach to
     */
    public Source_Schema(String attachedFieldKey) {
        this(attachedFieldKey, null);
    }

    /**
     * @param individualCalculator The method to recalculate the fields value based on the change of this source chain.
     * @see Source_Schema#Source_Schema(String)
     */
    public Source_Schema(String attachedFieldKey, IndividualCalculator<EndFieldType> individualCalculator) {
        assert attachedFieldKey != null;

        this.attachedFieldKey = attachedFieldKey;
        this.individualCalculator = individualCalculator;
    }

    /**
     * Recalculate the value of the DataCore based on the change of only 1 of the sources.
     *
     * @param parent   The DataCore to drive
     * @param oldValue The old value/values
     * @param newValue The new value/values
     */
    @SuppressWarnings("unchecked")
    public void doIndividualRecalculate(Derived_DataCore<?, ?> parent, Object oldValue, Object newValue) {
        individualCalculator.doIndividualRecalculate((Derived_DataCore<EndFieldType, ?>) parent, (EndFieldType) oldValue, (EndFieldType) newValue);
    }

    /**
     * Create a source instance. This should be called from the data core the source chain is attached to and should
     * be invoked on the top Source object in the chain.
     *
     * @param parentDataCore The Derived_DataCore this source chain will bne attached to
     * @return A stand alone instance Source instance
     */
    public abstract Source<?, ?> createRootSource(Derived_DataCore<?, ?> parentDataCore);

    /**
     * Create a source instance. This should be called from another source object in the chain.
     *
     * @param attachedFieldContainer The container that has the field this source will attach to
     * @param parentSource           The next step up in the source chain that this source will be attached to
     * @return A stand alone Source instance
     */
    public abstract Source<?, ?> createChildSource(DataObject attachedFieldContainer, Source<?, ?> parentSource);

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public String getAttachedFieldKey() {
        return attachedFieldKey;
    }

    public IndividualCalculator<EndFieldType> getIndividualCalculator() {
        return individualCalculator;
    }
}
