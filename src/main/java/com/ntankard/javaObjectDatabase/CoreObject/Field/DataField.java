package com.ntankard.javaObjectDatabase.CoreObject.Field;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore.DataCore_Factory;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Filter.FieldFilter;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Filter.Null_FieldFilter;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Properties.Display_Properties;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.ntankard.javaObjectDatabase.CoreObject.Field.DataField.SourceMode.*;

public class DataField<FieldType> {

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
    private DataCore_Factory<FieldType, ?> dataCore_factory;

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
     * The properties to use when displaying the data
     */
    private final Display_Properties displayProperties = new Display_Properties();

    /**
     * A list of fields this one depends on (must be part of the same container as this one)
     */
    private List<String> dependantFields = new ArrayList<>();

    /**
     * The fillers used to check the data
     */
    private List<FieldFilter<FieldType, ?>> filters = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public DataField(String identifierName, Class<FieldType> type) {
        this(identifierName, type, false);
    }

    /**
     * Constructor
     */
    public DataField(String identifierName, Class<FieldType> type, Boolean canBeNull) {
        this.sourceMode = SourceMode.DIRECT;

        this.identifierName = identifierName;
        this.type = type;
        this.canBeNull = canBeNull;

        this.displayName = identifierName.replace("get", "").replace("is", "").replace("has", "");

        addFilter(new Null_FieldFilter<>(canBeNull));
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

        if (setterFunction != null && dataCore_factory == null)
            throw new IllegalStateException("Setter function added but not data core provided");

        this.displayProperties.finish();
        this.filters = Collections.unmodifiableList(this.filters);
        this.dependantFields = Collections.unmodifiableList(this.dependantFields);
    }

    /**
     * Generate a DataField_Instance for this field
     *
     * @param blackObject The object to attach the instance too
     * @return The new instance
     */
    public DataField_Instance<FieldType> generate(DataObject blackObject) {
        return new DataField_Instance<>(this, blackObject);
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

    public DataCore_Factory<FieldType, ?> getDataCore_factory() {
        return dataCore_factory;
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

    public Display_Properties getDisplayProperties() {
        return displayProperties;
    }

    public List<String> getDependantFields() {
        return dependantFields;
    }

    public List<FieldFilter<FieldType, ?>> getFilters() {
        return filters;
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    // Data Control ----------------------------------------------------------------------------------------------------

    public void setManualCanEdit(Boolean manualCanEdit) {
        this.manualCanEdit = manualCanEdit;
    }

    public void setDataCore_factory(DataCore_Factory<FieldType, ?> dataCore_factory) {
        this.dataCore_factory = dataCore_factory;
        if (setterFunction != null) {
            this.sourceMode = VIRTUAL_DERIVED;
        } else {
            this.sourceMode = DERIVED;
        }
    }

    public void setSetterFunction(DataField.SetterFunction<FieldType> setterFunction) {
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

    public void addDependantField(String field) {
        this.dependantFields.add(field);
    }

    public void addFilter(FieldFilter<FieldType, ?> filter) {
        this.filters.add(filter);
        filter.attachedToField(this);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Object Methods #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataField<?> dataField = (DataField<?>) o;
        return getIdentifierName().equals(dataField.getIdentifierName()) &&
                getType().equals(dataField.getType());
    }

    /**
     * {@inheritDoc
     */
    @Override
    public int hashCode() {
        return Objects.hash(getIdentifierName(), getType());
    }

    /**
     * {@inheritDoc
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
