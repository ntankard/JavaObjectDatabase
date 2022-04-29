package com.ntankard.javaObjectDatabase.dataObject;

import com.ntankard.javaObjectDatabase.dataField.*;
import com.ntankard.javaObjectDatabase.dataObject.factory.ObjectFactory;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.database.subContainers.DataObjectContainer;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;
import com.ntankard.javaObjectDatabase.exception.nonCorrupting.NonCorruptingException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataObject {

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Field Setup ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The method name to get the fields
     */
    public static String FieldName = "getDataObjectSchema";

    /**
     * Set all the fields for this object, should be called by a solid object constructor
     *
     * @param fields The fields to set
     */
    public void setFields(List<DataField<?>> fields) {
        for (DataField<?> field : fields) {
            fieldMap.put(field.getDataFieldSchema().getIdentifierName(), field);
        }
    }

    /**
     * The schema of this object
     */
    private final DataObject_Schema dataObjectSchema;

    /**
     * The fields for this DataObject
     */
    protected Map<String, DataField<?>> fieldMap = new HashMap<>();

    /**
     * A list of objects that were made in factories related to this object
     */
    private final List<DataObject> factoryConstructed = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    public static final String DataObject_Id = "getId";
    public static final String DataObject_ChildrenField = "getChildrenField";

    public interface DataObjectList extends List<DataObject> {
    }

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = new DataObject_Schema();
        // ID ==========================================================================================================
        dataObjectSchema.add(new DataField_Schema<>(DataObject_Id, Integer.class));
        // ChildrenField ===============================================================================================
        dataObjectSchema.add(new ListDataField_Schema<>(DataObject_ChildrenField, DataObjectList.class));
        dataObjectSchema.get(DataObject_ChildrenField).setShouldSave(false);
        //==============================================================================================================

        dataObjectSchema.follow(DataObject_Id);
        return dataObjectSchema.endLayer(DataObject.class);
    }

    /**
     * Constructor
     *
     * @param database The database this object will link to
     * @param args     The initial values for the fields (first is the key for the field, second is the value)
     */
    public DataObject(Database database, Object... args) {
        this.database = database;
        this.dataObjectSchema = database.getSchema().getClassSchema(this.getClass());
        assert dataObjectSchema.getSolidObjectType() == getClass();

        setup(args);
    }

    /**
     * Constructor (used for test only)
     *
     * @param dataObjectSchema The schema for the object to generate
     * @param args             The initial values for the fields (first is the key for the field, second is the value)
     */
    public DataObject(DataObject_Schema dataObjectSchema, Object... args) {
        assert dataObjectSchema.getSolidObjectType() == getClass();
        this.dataObjectSchema = dataObjectSchema;
        setup(args);
    }

    /**
     * Core impl for the constructor
     *
     * @param args The initial values for the fields (first is the key for the field, second is the value)
     */
    private void setup(Object... args) {
        // Link the fields to the object
        List<DataField<?>> instanceList = new ArrayList<>();
        for (DataField_Schema<?> field : dataObjectSchema.getList()) {
            DataField<?> instance = field.generate(this);
            instanceList.add(instance);
        }
        this.setFields(instanceList);

        if (args.length == 0) {
            return;
    }

        if (args.length / 2 * 2 != args.length)
            throw new IllegalArgumentException("Wrong amount of arguments");

        // Start sharing data
        instanceList.forEach(DataField::linkWithingDataObject);

        // Load each of the fields
        boolean idFound = false;
        for (int i = 0; i < args.length / 2; i++) {
            String identifier = args[i * 2].toString();
            Object value = args[i * 2 + 1];
            this.getField(identifier).set(value);
            if (!idFound && identifier.equals(DataObject_Id)) {
                idFound = true;
            }
        }
        if (!idFound) {
            this.getField(DataObject_Id).set(getTrackingDatabase().getNextId());
        }
        this.getField(DataObject_ChildrenField).set(new ArrayList<DataObject>());
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Database access ################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The core database
     */
    private Database database;

    /**
     * Get the core database
     *
     * @return The core database
     */
    public Database getTrackingDatabase() {
        return database;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Field Interface #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get a specific field
     *
     * @param field The Field to get
     * @param <T>   THe type of the field
     * @return The field
     */
    @SuppressWarnings("unchecked")
    public <T> DataField<T> getField(String field) {
        return (DataField<T>) fieldMap.get(field);
    }

    /**
     * Set the value from a specific field
     *
     * @param field The field to set
     * @param value THe value to set
     * @param <T>   The type of the Field
     */
    public <T> void set(String field, T value) {
        (getField(field)).set(value);
    }

    /**
     * Get the value from a specific field
     *
     * @param field The Field to get
     * @param <T>   The type of the Field
     * @return The value of the field
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String field) {
        return (T) getField(field).get();
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### General #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        try {
            return get(DataObject_Id).toString();
        } catch (Exception e) {
            return "NO ID";
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Database access  ################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Add this object to the database. Notify everyone required and create or add supporting objects if needed
     */
    @SuppressWarnings("unchecked")
    public <DataObjectType extends DataObject> DataObjectType add() {
        // TODO lifecycle test , add, remove, remove when object are in child list that sum

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            if (!field.getValue().hasValidValue()) {
                throw new RuntimeException();
            }
        }

        for (ObjectFactory<?> factory : dataObjectSchema.getObjectFactories()) {
            factoryConstructed.addAll(factory.generate(this));
        }

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            field.getValue().linkToDataBase(); // Possible error here, this may need to be done AFTER being put into the database
        }

        getTrackingDatabase().add(this);

        return (DataObjectType) this;
    }

    /**
     * Safely remove this object from the database. Disabled by default
     */
    public void remove() {
        throw new UnsupportedOperationException("Not cleared for removal");
    }

    /**
     * Safely remove this object from the database.
     */
    protected void remove_impl() {

        // catch exceptions thrown by the fields and elevate them if needed
        if (this.getChildren().size() != 0) {
            throw new RuntimeException("Cant delete this kind of object. NoneFundEvent still has children");
        }
        if (getTrackingDatabase().getSchema().getClassSchema(this.getClass()).getObjectFactories().size() != 0) {
            throw new UnsupportedOperationException("The code for deleting object that are factories has not been implemented yet");
        }

        getTrackingDatabase().remove(this);

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            if (field.getKey().equals(DataObject_Id)) {
                continue;
            }
            field.getValue().remove();
        }
        fieldMap.get(DataObject_Id).remove();

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            if (!field.getValue().getFieldChangeListeners().isEmpty()) {
                throw new RuntimeException();
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################### Parental Hierarchy  ##############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * All my children
     */
    private final DataObjectContainer children = new DataObjectContainer();

    /**
     * Add a new ChildrenListener listeners
     *
     * @param childrenListener The ChildrenListener
     * @param <T>              tClass type
     */
    public <T extends DataObject> void addChildrenListener(FieldChangeListener<List<T>> childrenListener) {
        this.<List<T>>getField(DataObject_ChildrenField).addChangeListener(childrenListener);
    }

    /**
     * Remove a ChildrenListener listeners
     *
     * @param childrenListener The ChildrenListener
     * @param <T>              tClass type
     */
    public <T extends DataObject> void removeChildrenListener(FieldChangeListener<List<T>> childrenListener) {
        this.<List<T>>getField(DataObject_ChildrenField).removeChangeListener(childrenListener);
    }

    /**
     * Notify that a child has linked to this object as its parent
     *
     * @param linkObject The child
     */
    public void notifyChildLink(DataObject linkObject) {
        children.add(linkObject);

        ((ListDataField<DataObject>) this.<List<DataObject>>getField(DataObject_ChildrenField)).add(linkObject);
    }

    /**
     * Notify that a child has un linked this object as its parent
     *
     * @param linkObject The child
     */
    public void notifyChildUnLink(DataObject linkObject) {
        children.remove(linkObject);

        ((ListDataField<DataObject>) this.<List<DataObject>>getField(DataObject_ChildrenField)).remove(linkObject);
    }

    /**
     * Get all children
     *
     * @return The List of all children
     */
    public List<DataObject> getChildren() {
        return this.<List<DataObject>>getField(DataObject_ChildrenField).get();
    }

    /**
     * Get the list of children for a a specific class type
     *
     * @param type The class type to get
     * @param <T>  The Object type
     * @return The list of children for a a specific class type
     */
    public <T extends DataObject> List<T> getChildren(Class<T> type) {
        return children.get(type);
    }

    /**
     * Does this DataObject have this child?
     *
     * @param toTest The child to check for
     * @return True if this DataObject contains the child
     */
    public boolean hasChild(DataObject toTest) {
        return getChildren().contains(toTest);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Integer getId() {
        return get(DataObject_Id);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public static Method getSourceOptionMethod() {
        try {
            return DataObject.class.getMethod("sourceOptions", Class.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get possible options that a field will accept
     *
     * @param type      The type of object expected
     * @param fieldName The field name
     * @return A list of objects the the field will accept
     */
    public <T extends DataObject> List<T> sourceOptions(Class<T> type, String fieldName) {
        return getTrackingDatabase().get(type);
    }
}
