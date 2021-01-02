package com.ntankard.javaObjectDatabase.dataField;

import com.ntankard.javaObjectDatabase.dataField.dataCore.DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.properties.CustomProperty;
import com.ntankard.javaObjectDatabase.dataField.validator.FieldValidator;
import com.ntankard.javaObjectDatabase.dataField.validator.Null_FieldValidator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;

import java.lang.reflect.Method;
import java.util.*;

import static com.ntankard.javaObjectDatabase.dataField.DataField_Schema.SourceMode.*;

public class DataField_Schema<FieldType> {

    // Core Data -------------------------------------------------------------------------------------------------------

    /**
     * The name of the Field. This must be unique and is used to identify and save the field
     */
    private final String identifierName;

    /**
     * The data type of the field (same as T)
     */
    private final Class<FieldType> type;

    /**
     * Can the field be null?
     */
    private final boolean canBeNull;

    /**
     * The name to be displayed to the user, can be anything.
     */
    private final String displayName;

    /**
     * The type of object that contains this field
     */
    private Class<? extends DataObject> parentType;

    // Data Control ----------------------------------------------------------------------------------------------------

    public enum SourceMode {
        DIRECT,         // The field is controlled by the user
        DERIVED,        // The is set by other fields directly or indirectly
        VIRTUAL_DERIVED // The field is controlled another field but has a setter that performs an external function
    }

    /**
     * The mode the field is operating in
     */
    private SourceMode sourceMode;

    /**
     * If the field is operating in DIRECT mode, can the user change the value?
     */
    private boolean manualCanEdit = false;

    /**
     * The factory for the DataCore
     */
    private DataCore_Schema<FieldType> dataCore_schema;

    /**
     * The method to call if we are in virtual mode when a manual set happens
     */
    private SetterFunction<FieldType> setterFunction = null;

    // General Properties ----------------------------------------------------------------------------------------------

    /**
     * Should the parent be notified if this field links to it?
     */
    private boolean tellParent = true;

    /**
     * The source of valid values that be used to set this field
     */
    private Method source = null;

    /**
     * Should this field be saved? Assuming all other save conditions are met
     */
    private boolean shouldSave = true;

    /**
     * Any custom properties attached to the field
     */
    private final Map<Class<? extends CustomProperty>, CustomProperty> properties = new HashMap<>();

    /**
     * The order of this field relative to the other fields in the DataObject_Schema
     */
    private int order = -1;

    /**
     * The fillers used to check the data
     */
    private List<FieldValidator<FieldType, ?>> validators = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public DataField_Schema(String identifierName, Class<FieldType> type) {
        this(identifierName, type, false);
    }

    /**
     * Constructor
     */
    public DataField_Schema(String identifierName, Class<FieldType> type, Boolean canBeNull) {
        this.sourceMode = SourceMode.DIRECT;

        this.identifierName = identifierName;
        this.type = type;
        this.canBeNull = canBeNull;

        this.displayName = identifierName.replace("get", "").replace("is", "").replace("has", "");

        addValidator(new Null_FieldValidator<>(canBeNull));
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Finalisation ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Called when all fields in a container are finished and added to the container
     *
     * @param parentType The type of object this fields belongs too
     */
    public void containerFinished(Class<? extends DataObject> parentType) {
        this.parentType = parentType;

        if (manualCanEdit && !sourceMode.equals(DIRECT))
            throw new IllegalStateException("Manual edit was set but the field is operating in a non direct mode");

        if (setterFunction != null && dataCore_schema == null)
            throw new IllegalStateException("Setter function added but not data core provided");

        for (Map.Entry<Class<? extends CustomProperty>, CustomProperty> customProperty : properties.entrySet()) {
            customProperty.getValue().finalise();
        }

        this.validators = Collections.unmodifiableList(this.validators);
    }

    /**
     * Generate a DataField_Instance for this field
     *
     * @param blackObject The object to attach the instance too
     * @return The new instance
     */
    public DataField<FieldType> generate(DataObject blackObject) {
        return new DataField<>(this, blackObject);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    // Core Data -------------------------------------------------------------------------------------------------------

    public String getIdentifierName() {
        return identifierName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<FieldType> getType() {
        return type;
    }

    public boolean isCanBeNull() {
        return canBeNull;
    }

    public Class<? extends DataObject> getParentType() {
        return parentType;
    }

    // Data Control ----------------------------------------------------------------------------------------------------

    public SourceMode getSourceMode() {
        return sourceMode;
    }

    public Boolean getCanEdit() {
        if (sourceMode.equals(VIRTUAL_DERIVED)) {
            return true;
        }
        if (sourceMode.equals(DERIVED)) {
            return false;
        }
        return manualCanEdit;
    }

    public DataCore_Schema<FieldType> getDataCore_schema() {
        return dataCore_schema;
    }

    public SetterFunction<FieldType> getSetterFunction() {
        return setterFunction;
    }

    // General Properties ----------------------------------------------------------------------------------------------

    public boolean isTellParent() {
        return tellParent;
    }

    public Method getSource() {
        return source;
    }

    public boolean isShouldSave() {
        return shouldSave;
    }

    @SuppressWarnings("unchecked")
    public <T extends CustomProperty> T getProperty(Class<T> key) {
        return (T) properties.get(key);
    }

    public int getOrder() {
        return order;
    }

    public List<FieldValidator<FieldType, ?>> getValidators() {
        return validators;
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    // Data Control ----------------------------------------------------------------------------------------------------

    public void setManualCanEdit(Boolean manualCanEdit) {
        this.manualCanEdit = manualCanEdit;
    }

    public void setDataCore_schema(DataCore_Schema<FieldType> dataCore_schema) {
        this.dataCore_schema = dataCore_schema;
        if (setterFunction != null) {
            this.sourceMode = VIRTUAL_DERIVED;
        } else {
            this.sourceMode = DERIVED;
        }
    }

    public void setSetterFunction(DataField_Schema.SetterFunction<FieldType> setterFunction) {
        this.setterFunction = setterFunction;
        this.sourceMode = VIRTUAL_DERIVED;
    }

    // General Properties ----------------------------------------------------------------------------------------------

    public void setTellParent(boolean tellParent) {
        this.tellParent = tellParent;
    }

    public void setSource(Method source) {
        this.source = source;
    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }

    public void addValidator(FieldValidator<FieldType, ?> validator) {
        this.validators.add(validator);
    }

    public void setProperty(CustomProperty customProperty) {
        properties.put(customProperty.getClass(), customProperty);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Object Methods #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataField_Schema<?> dataFieldSchema = (DataField_Schema<?>) o;
        return getIdentifierName().equals(dataFieldSchema.getIdentifierName()) &&
                getType().equals(dataFieldSchema.getType());
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(getIdentifierName(), getType());
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return identifierName + " - " + type.getSimpleName();
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Interface ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface for virtual setter function
     */
    public interface SetterFunction<T> {

        /**
         * Called when the user invokes a the setter on a virtual field
         *
         * @param toSet     The values to set
         * @param container The object containing the field
         */
        void set(T toSet, DataObject container);
    }
}
