package com.ntankard.javaObjectDatabase.dataField.validator;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.exception.corrupting.DatabaseStructureException;

/**
 * A validator that is designed to be used on 2 fields that have interlinking requirements. The filter will not run
 * until both values are initialized so a shared failure will be seen in the last of the 2 to be initialised. A change
 * to either field will trigger the validator
 *
 * @param <FirstType>     The type of object that will need to be checked on the first field
 * @param <SecondType>    The type of object that will need to be checked on the second field
 * @param <ContainerType> The type of the container of the field that houses this filter
 * @author Nicholas Tankard
 */
public class Shared_FieldValidator<FirstType, SecondType, ContainerType extends DataObject> implements ValidatableSchema {

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Interface ###################################################
    //------------------------------------------------------------------------------------------------------------------

    public interface FullSharedValidator<FirstType, SecondType, ContainerType extends DataObject> {

        /**
         * Validate based on values from both fields. Will only be called once both fields are valid
         *
         * @param firstNewValue   The new value of the first field. Current if the second field is driving the change
         * @param firstPastValue  The past value of the first field. Current if the second field is driving the change
         * @param secondNewValue  The new value of the second field. Current if the first field is driving the change
         * @param secondPastValue The past value of the second field. Current if the first field is driving the change
         * @param container       The object the fields are attached to
         * @return True of the new value is valid
         */
        boolean validate(FirstType firstNewValue, FirstType firstPastValue, SecondType secondNewValue, SecondType secondPastValue, ContainerType container);
    }

    public interface OnlyNewSharedValidator<FirstType, SecondType, ContainerType extends DataObject> {

        /**
         * Validate based on values from both fields. Will only be called once both fields are valid
         *
         * @param firstNewValue  The new value of the first field. Current if the second field is driving the change
         * @param secondNewValue The new value of the second field. Current if the first field is driving the change
         * @param container      The object the fields are attached to
         * @return True of the new value is valid
         */
        boolean validate(FirstType firstNewValue, SecondType secondNewValue, ContainerType container);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Core ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The filter to run
     */
    protected final FullSharedValidator<FirstType, SecondType, ContainerType> fullSharedValidator;

    /**
     * The filter to run
     */
    protected final OnlyNewSharedValidator<FirstType, SecondType, ContainerType> onlyNewSharedValidator;

    /**
     * The key for the second field
     */
    private final String firstFieldKey;

    /**
     * The actual filter to attach to the second field
     */
    private final FirstValidator<FirstType, ContainerType> firstFilter;

    /**
     * The key for the first field
     */
    private final String secondFieldKey;

    /**
     * The actual filter to attach to the first field
     */
    private final SecondValidator<SecondType, ContainerType> secondFilter;

    /**
     * A description of the validator to use in error messages
     */
    private final String description;

    /**
     * Constructor
     *
     * @param firstFieldKey       The key for the first field this filter applies to (attach getFirstFilter() to this field as well)
     * @param secondFieldKey      The key for the second field this filter applies to (attach getSecondFilter() to this field as well)
     * @param fullSharedValidator The validate logic to use
     * @param description         A description of the validator to use in error messages
     */
    public Shared_FieldValidator(String firstFieldKey, String secondFieldKey, FullSharedValidator<FirstType, SecondType, ContainerType> fullSharedValidator, String description) {
        this(firstFieldKey, secondFieldKey, fullSharedValidator, null, description);
    }

    /**
     * Constructor
     *
     * @param firstFieldKey          The key for the first field this filter applies to (attach getFirstFilter() to this field as well)
     * @param secondFieldKey         The key for the second field this filter applies to (attach getSecondFilter() to this field as well)
     * @param onlyNewSharedValidator The validate logic to use
     * @param description            A description of the validator to use in error messages
     */
    public Shared_FieldValidator(String firstFieldKey, String secondFieldKey, OnlyNewSharedValidator<FirstType, SecondType, ContainerType> onlyNewSharedValidator, String description) {
        this(firstFieldKey, secondFieldKey, null, onlyNewSharedValidator, description);
    }

    /**
     * Constructor
     *
     * @param firstFieldKey          The key for the first field this filter applies to (attach getFirstFilter() to this field as well)
     * @param secondFieldKey         The key for the second field this filter applies to (attach getSecondFilter() to this field as well)
     * @param fullSharedValidator    The validate logic to use, or null if onlyNewSharedValidator is provided
     * @param onlyNewSharedValidator The validate logic to use, or null if fullSharedValidator is provided
     * @param description            A description of the validator to use in error messages
     */
    private Shared_FieldValidator(String firstFieldKey, String secondFieldKey, FullSharedValidator<FirstType, SecondType, ContainerType> fullSharedValidator, OnlyNewSharedValidator<FirstType, SecondType, ContainerType> onlyNewSharedValidator, String description) {
        assert firstFieldKey != null;
        assert secondFieldKey != null;
        assert fullSharedValidator != null || onlyNewSharedValidator != null;
        assert !(fullSharedValidator != null && onlyNewSharedValidator != null);

        this.firstFieldKey = firstFieldKey;
        this.secondFieldKey = secondFieldKey;
        this.fullSharedValidator = null;
        this.onlyNewSharedValidator = onlyNewSharedValidator;
        this.firstFilter = new FirstValidator<>(this);
        this.secondFilter = new SecondValidator<>(this);
        this.description = description;
    }

    /**
     * Check if a new value in the first field is valid
     */
    private boolean doFirstFilter(FirstType newValue, FirstType pastValue, ContainerType container) {
        DataField<SecondType> secondField = container.getField(secondFieldKey);
        if (secondField.hasValidValue()) {
            if (fullSharedValidator != null) {
                return fullSharedValidator.validate(newValue, pastValue, secondField.get(), secondField.get(), container);
            } else if (onlyNewSharedValidator != null) {
                return onlyNewSharedValidator.validate(newValue, secondField.get(), container);
            } else {
                throw new DatabaseStructureException(null, "Filter method missing, should be impossible");
            }
        }
        return true;
    }

    /**
     * Check if a new value in the second field is valid
     */
    private boolean doSecondFilter(SecondType newValue, SecondType pastValue, ContainerType container) {
        DataField<FirstType> firstField = container.getField(firstFieldKey);
        if (firstField.hasValidValue()) {
            if (fullSharedValidator != null) {
                return fullSharedValidator.validate(firstField.get(), firstField.get(), newValue, pastValue, container);
            } else if (onlyNewSharedValidator != null) {
                return onlyNewSharedValidator.validate(firstField.get(), newValue, container);
            } else {
                throw new DatabaseStructureException(null, "Filter method missing, should be impossible");
            }
        }
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void validateToAttachedSchema(DataObject_Schema dataObject_schema) {
        DataField_Schema<?> firstField;
        DataField_Schema<?> secondField;

        try {
            firstField = dataObject_schema.get(firstFieldKey);
        } catch (IllegalArgumentException e) {
            throw new DatabaseStructureException(null, "The DataObject_Schema we are attached to does not contain the first field", e);
        }

        try {
            secondField = dataObject_schema.get(secondFieldKey);
        } catch (IllegalArgumentException e) {
            throw new DatabaseStructureException(null, "The DataObject_Schema we are attached to does not contain the second field", e);
        }

        if (!firstField.getValidators().contains(firstFilter)) {
            throw new DatabaseStructureException(null, "The first field filter is not attached to the fist field");
        }

        if (!secondField.getValidators().contains(secondFilter)) {
            throw new DatabaseStructureException(null, "The second field filter is not attached to the second field");
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################### End Filters ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The actual filter to attach to the first field
     */
    public static class FirstValidator<FirstType, ContainerType extends DataObject> implements FieldValidator<FirstType, ContainerType>, ValidatableSchema {

        /**
         * The core filter calculator
         */
        private final Shared_FieldValidator<FirstType, ?, ContainerType> parent;

        /**
         * Constructor
         */
        public FirstValidator(Shared_FieldValidator<FirstType, ?, ContainerType> parent) {
            this.parent = parent;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean isValid(FirstType newValue, FirstType pastValue, ContainerType container) {
            return parent.doFirstFilter(newValue, pastValue, container);
        }

        /**
         * @inheritDoc
         */
        @Override
        public String getValidatorDetails() {
            return parent.description;
        }

        /**
         * @inheritDoc
         */
        @Override
        public void validateToAttachedSchema(DataObject_Schema dataObject_schema) {
            parent.validateToAttachedSchema(dataObject_schema);
        }
    }

    /**
     * The actual filter to attach to the second field
     */
    public static class SecondValidator<SecondType, ContainerType extends DataObject> implements FieldValidator<SecondType, ContainerType>, ValidatableSchema {

        /**
         * The core filter calculator
         */
        private final Shared_FieldValidator<?, SecondType, ContainerType> parent;

        /**
         * Constructor
         */
        public SecondValidator(Shared_FieldValidator<?, SecondType, ContainerType> parent) {
            this.parent = parent;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean isValid(SecondType newValue, SecondType pastValue, ContainerType container) {
            return parent.doSecondFilter(newValue, pastValue, container);
        }

        /**
         * @inheritDoc
         */
        @Override
        public void validateToAttachedSchema(DataObject_Schema dataObject_schema) {
            parent.validateToAttachedSchema(dataObject_schema);
        }

        /**
         * @inheritDoc
         */
        @Override
        public String getValidatorDetails() {
            return parent.description;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public FirstValidator<FirstType, ContainerType> getFirstFilter() {
        return firstFilter;
    }

    public SecondValidator<SecondType, ContainerType> getSecondFilter() {
        return secondFilter;
    }
}
