package com.ntankard.javaObjectDatabase.dataField.filter;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField;

public class Shared_FieldFilter<FirstType, SecondType, ContainerType extends DataObject> {

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Interface ###################################################
    //------------------------------------------------------------------------------------------------------------------

    public interface SharedFilter<FirstType, SecondType, ContainerType extends DataObject> {

        /**
         * Filter based on values from both fields. Will only be called once both fields are valid
         *
         * @param firstNewValue   The new value of the first field. Current if the second field is driving the change
         * @param firstPastValue  The past value of the first field. Current if the second field is driving the change
         * @param secondNewValue  The new value of the second field. Current if the first field is driving the change
         * @param secondPastValue The past value of the second field. Current if the first field is driving the change
         * @param container       The object the fields are attached to
         * @return True of the new value is valid
         */
        boolean filter(FirstType firstNewValue, FirstType firstPastValue, SecondType secondNewValue, SecondType secondPastValue, ContainerType container);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Core ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The filter to run
     */
    private final SharedFilter<FirstType, SecondType, ContainerType> sharedFilter;

    /**
     * The key for the second field
     */
    private final String firstFieldKey;

    /**
     * The actual filter to attach to the second field
     */
    private final FirstFilter<FirstType, ContainerType> firstFilter;

    /**
     * The key for the first field
     */
    private final String secondFieldKey;

    /**
     * The actual filter to attach to the first field
     */
    private final SecondFilter<SecondType, ContainerType> secondFilter;

    /**
     * Constructor
     */
    public Shared_FieldFilter(String firstFieldKey, String secondFieldKey, SharedFilter<FirstType, SecondType, ContainerType> sharedFilter) {
        this.firstFieldKey = firstFieldKey;
        this.secondFieldKey = secondFieldKey;
        this.sharedFilter = sharedFilter;
        firstFilter = new FirstFilter<>(this);
        secondFilter = new SecondFilter<>(this);
    }

    /**
     * Check if a new value in the first field is valid
     */
    private boolean doFirstFilter(FirstType newValue, FirstType pastValue, ContainerType container) {
        DataField<SecondType> secondField = container.getField(secondFieldKey);
        if (secondField.hasValidValue()) {
            return sharedFilter.filter(newValue, pastValue, secondField.get(), secondField.get(), container);
        }
        return true;
    }

    /**
     * Check if a new value in the second field is valid
     */
    private boolean doSecondFilter(SecondType newValue, SecondType pastValue, ContainerType container) {
        DataField<FirstType> firstField = container.getField(firstFieldKey);
        if (firstField.hasValidValue()) {
            return sharedFilter.filter(firstField.get(), firstField.get(), newValue, pastValue, container);
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################### End Filters ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The actual filter to attach to the first field
     */
    public static class FirstFilter<FirstType, ContainerType extends DataObject> extends FieldFilter<FirstType, ContainerType> {

        /**
         * The core filter calculator
         */
        private final Shared_FieldFilter<FirstType, ?, ContainerType> parent;

        /**
         * Constructor
         */
        public FirstFilter(Shared_FieldFilter<FirstType, ?, ContainerType> parent) {
            this.parent = parent;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean isValid(FirstType newValue, FirstType pastValue, ContainerType container) {
            return parent.doFirstFilter(newValue, pastValue, container);
        }
    }

    /**
     * The actual filter to attach to the second field
     */
    public static class SecondFilter<SecondType, ContainerType extends DataObject> extends FieldFilter<SecondType, ContainerType> {

        /**
         * The core filter calculator
         */
        private final Shared_FieldFilter<?, SecondType, ContainerType> parent;

        /**
         * Constructor
         */
        public SecondFilter(Shared_FieldFilter<?, SecondType, ContainerType> parent) {
            this.parent = parent;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean isValid(SecondType newValue, SecondType pastValue, ContainerType container) {
            return parent.doSecondFilter(newValue, pastValue, container);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Getters ####################################################
    //------------------------------------------------------------------------------------------------------------------

    public FirstFilter<FirstType, ContainerType> getFirstFilter() {
        return firstFilter;
    }

    public SecondFilter<SecondType, ContainerType> getSecondFilter() {
        return secondFilter;
    }
}
