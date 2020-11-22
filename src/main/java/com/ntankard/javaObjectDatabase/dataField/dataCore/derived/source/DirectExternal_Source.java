package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore.Calculator;

/**
 * A source that is a field in another object that is linked to by a field in this object. The field is the other object is the direct result
 */
public class DirectExternal_Source<ResultType, SourceContainerType extends DataObject> extends External_Source<ResultType, SourceContainerType, ResultType> {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Factory ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public static class DirectExternalSource_Factory<ResultType, SourceContainerType extends DataObject> extends Source_Factory<ResultType, DirectExternal_Source<ResultType, SourceContainerType>> {

        /**
         * The field containing the value that is the source of our new value
         */
        private final String sourceContainerFieldKey;

        /**
         * The field inside the source object used to get the final value
         */
        private final String sourceFieldName;

        /**
         * Interface to modify the value if needed
         */
        private final ValueModifier<ResultType> valueModifier;

        /**
         * Constructor
         */
        public DirectExternalSource_Factory(String sourceContainerFieldKey, String sourceFieldName) {
            this(sourceContainerFieldKey, sourceFieldName, null);
        }

        /**
         * Constructor
         */
        public DirectExternalSource_Factory(String sourceContainerFieldKey, String sourceFieldName, ValueModifier<ResultType> valueModifier) {
            this.sourceContainerFieldKey = sourceContainerFieldKey;
            this.sourceFieldName = sourceFieldName;
            this.valueModifier = valueModifier;
        }

        /**
         * @inheritDoc
         */
        @Override
        public DirectExternal_Source<ResultType, SourceContainerType> createSource(DataField<ResultType> container) {
            return new DirectExternal_Source<>(container.getContainer().getField(sourceContainerFieldKey), sourceFieldName, valueModifier);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Core Source ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The converter that the DataCore can use to extract the value from this source
     */
    private final Calculator<ResultType, SourceContainerType> calculator;

    /**
     * Constructor
     */
    public DirectExternal_Source(DataField<SourceContainerType> sourceObjectField, String sourceFieldName) {
        this(sourceObjectField, sourceFieldName, null);
    }

    /**
     * Constructor
     */
    public DirectExternal_Source(DataField<SourceContainerType> sourceContainerField, String sourceFieldName, ValueModifier<ResultType> valueModifier) {
        super(sourceContainerField, sourceFieldName);
        if (sourceContainerField.getDataFieldSchema().isCanBeNull())
            throw new IllegalArgumentException("The field can no allow null values");

        this.calculator = container -> {
            ResultType original = (getContainerObject().<ResultType>getField(getSourceFieldName())).get();
            if (valueModifier != null) {
                return valueModifier.modify(original);
            } else {
                return original;
            }
        };
    }

    /**
     * Get the converter that the DataCore can use to extract the value from this source
     *
     * @return The converter that the DataCore can use to extract the value from this source
     */
    public Calculator<ResultType, SourceContainerType> getCalculator() {
        return calculator;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Interface Classes ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface to make small changes to the directly extracted value
     */
    public interface ValueModifier<ResultType> {

        /**
         * Make any needed changes to the extracted value
         *
         * @param original The extracted value
         * @return The modified value
         */
        ResultType modify(ResultType original);
    }
}
