package com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.dataCore.derived.Derived_DataCore.Calculator;

/**
 * A source that is a field in another object that is linked to by a field in this object. The field is the other object is the direct result
 */
public class DirectExternalSource<ResultType, SourceContainerType extends DataObject> extends ExternalSource<ResultType, SourceContainerType, ResultType> {

    /**
     * The converter that the DataCore can use to extract the value from this source
     */
    private final Calculator<ResultType, SourceContainerType> calculator;

    /**
     * Constructor
     */
    public DirectExternalSource(DataField<SourceContainerType> sourceObjectField, String fieldName) {
        this(sourceObjectField, fieldName, null);
    }

    /**
     * Constructor
     */
    public DirectExternalSource(DataField<SourceContainerType> sourceContainerField, String sourceFieldName, ValueModifier<ResultType> valueModifier) {
        super(sourceContainerField, sourceFieldName);
        if (sourceContainerField.isCanBeNull())
            throw new IllegalArgumentException("The field can no allow null values");

        this.calculator = container -> {
            ResultType original = (getContainerObject().<ResultType>getField(getFieldName())).get();
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
