package com.ntankard.javaObjectDatabase.dataField.validator.shared;

import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.validator.FieldValidator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.exception.corrupting.DatabaseStructureException;

import java.util.ArrayList;
import java.util.List;

/**
 * A validator that is designed to be used on multiple fields that have interlinking requirements. The filter will not
 * run until all values are initialized so a shared failure will be seen in the last of the fields to be initialised. A
 * change to any field will trigger the validator
 *
 * @param <ContainerType> The type of the container of the field that houses this filter
 * @author Nicholas Tankard
 */
public class Multi_FieldValidator<ContainerType extends DataObject> {

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Interface ###################################################
    //------------------------------------------------------------------------------------------------------------------

    public interface MultiValidator<ContainerType extends DataObject> {

        /**
         * Validate based on values from both fields. Will only be called once both fields are valid
         *
         * @param newValues  The new values of all the fields. Current if another field is driving the change
         * @param pastValues The past values of all the fields. Current if another field is driving the change
         * @param container  The object the fields are attached to
         * @return True if the new value is valid
         */
        boolean validate(Object[] newValues, Object[] pastValues, ContainerType container);
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Core ######################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The filter to run
     */
    protected final MultiValidator<ContainerType> multiValidator;

    /**
     * A description of the validator to use in error messages
     */
    private final String description;

    /**
     * The keys for each field that this validator will link to
     */
    private final String[] keys;

    /**
     * The number of fields
     */
    private final int size;

    /**
     * The individual validators to attach to each field
     */
    private final List<EndValidator<ContainerType>> validators = new ArrayList<>();

    /**
     * Constructor
     *
     * @param multiValidator The validate logic to use
     * @param description    A description of the validator to use in error messages
     * @param keys           A list of keys for the validators that will be attached
     */
    public Multi_FieldValidator(MultiValidator<ContainerType> multiValidator, String description, String... keys) {
        assert multiValidator != null;
        assert description != null;
        assert keys != null;
        assert keys.length != 0;
        for (int i = 0; i < keys.length; i++) {
            assert keys[i] != null;
            for (int j = i + 1; j < keys.length; j++) {
                assert !keys[i].equals(keys[j]);
            }
        }

        this.multiValidator = multiValidator;
        this.description = description;
        this.keys = keys;
        this.size = keys.length;

        for (int i = 0; i < size; i++) {
            this.validators.add(new EndValidator<>(i, this));
        }
    }

    /**
     * Get tge validator to attached based on its key
     *
     * @param key The key of the field
     * @return The validator to attach
     */
    public EndValidator<ContainerType> getValidator(String key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                return validators.get(i);
            }
        }
        throw new DatabaseStructureException(null, "Requested validator does not exist");
    }

    /**
     * Check if a new value in the first field is valid
     */
    private boolean doFirstFilter(int key, Object newValue, Object pastValue, ContainerType container) {
        Object[] newValues = new Object[size];
        Object[] pastValues = new Object[size];
        for (int i = 0; i < size; i++) {
            DataField<Object> field = container.getField(keys[i]);
            if (key == i) {
                newValues[i] = newValue;
                pastValues[i] = pastValue;
            } else {
                if (!field.hasValidValue()) {
                    return true;
                }
                newValues[i] = field.get();
                pastValues[i] = field.get();
            }
        }

        return multiValidator.validate(newValues, pastValues, container);
    }

    /**
     * Confirms that you this object is valid in regards to the schema it is attached to. If it is not it will throw an
     * appropriate exception
     *
     * @param dataObject_schema The Schema this object is attached to
     */
    public void validateToAttachedSchema(DataObject_Schema dataObject_schema) {
        DataField_Schema<?> firstField;

        for (int i = 0; i < size; i++) {
            try {
                firstField = dataObject_schema.get(keys[i]);
            } catch (IllegalArgumentException e) {
                throw new DatabaseStructureException(null, "The DataObject_Schema we are attached to does not contain the first field", e);
            }

            if (!firstField.getValidators().contains(validators.get(i))) {
                throw new DatabaseStructureException(null, "The first field filter is not attached to the fist field");
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################### End Filters ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The actual filter to attach to the field
     */
    public static class EndValidator<ContainerType extends DataObject> implements FieldValidator<Object, ContainerType> {

        /**
         * The core filter calculator
         */
        private final Multi_FieldValidator<ContainerType> parent;

        /**
         * The key of this validator
         */
        private final int key;

        /**
         * Constructor
         */
        public EndValidator(int key, Multi_FieldValidator<ContainerType> parent) {
            this.key = key;
            this.parent = parent;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean isValid(Object newValue, Object pastValue, ContainerType container) {
            return parent.doFirstFilter(key, newValue, pastValue, container);
        }

        /**
         * @inheritDoc
         */
        @Override
        public String getValidatorDetails() {
            return parent.description;
        }

        /**
         * Confirms that you this object is valid in regards to the schema it is attached to. If it is not it will throw an
         * appropriate exception
         *
         * @param dataObject_schema The Schema this object is attached to
         */
        public void validateToAttachedSchema(DataObject_Schema dataObject_schema) {
            parent.validateToAttachedSchema(dataObject_schema);
        }
    }
}
