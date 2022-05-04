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

    public interface DataObjectList extends List<DataObject> {
    }

    private static final String DataObject_Prefix = "DataObject_";

    public static final String DataObject_Id = DataObject_Prefix + "Id";
    public static final String DataObject_ChildrenField = DataObject_Prefix + "ChildrenField";

    // TODO because we now do an attempt load as part of the factories, can we create and add at the same time???

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = new DataObject_Schema();

        dataObjectSchema.add(new DataField_Schema<>(DataObject_Id, Integer.class));
        dataObjectSchema.add(new ListDataField_Schema<>(DataObject_ChildrenField, DataObjectList.class));

        // ChildrenField ===============================================================================================
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
        setup(args);
    }

    /**
     * Constructor (used for test only)
     *
     * @param dataObjectSchema The schema for the object to generate
     * @param args             The initial values for the fields (first is the key for the field, second is the value)
     */
    public DataObject(DataObject_Schema dataObjectSchema, Object... args) {
        this.database = null;
        this.dataObjectSchema = dataObjectSchema;
        setup(args);
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Add #######################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Core impl for the constructor
     *
     * @param args The initial values for the fields (first is the key for the field, second is the value)
     */
    private void setup(Object... args) {
        assert dataObjectSchema.getSolidObjectType() == getClass();
        if (args.length / 2 * 2 != args.length || args.length == 0)
            throw new NonCorruptingException("Wrong amount of arguments");

        // Link the fields to the object
        for (DataField_Schema<?> field : dataObjectSchema.getList()) {
            DataField<?> instance = field.generate(this);
            fieldMap.put(instance.getDataFieldSchema().getIdentifierName(), instance);
        }

        // Start sharing data
        fieldMap.forEach((s, dataField) -> dataField.linkWithingDataObject());

        // Load each of the fields
        List<DataField<?>> done = new ArrayList<>();
        boolean idFound = false;
        for (int i = 0; i < args.length / 2; i++) {
            String identifier = args[i * 2].toString();
            Object value = args[i * 2 + 1];
            this.getField(identifier).set(value);
            done.add(this.getField(identifier));
            if (!idFound && identifier.equals(DataObject_Id)) {
                idFound = true;
            }
        }
        if (!idFound) {
            this.getField(DataObject_Id).set(getTrackingDatabase().getNextId());
            done.add(this.getField(DataObject_Id));
        }
        this.getField(DataObject_ChildrenField).set(new ArrayList<DataObject>());
        done.add(this.getField(DataObject_ChildrenField));

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            if (!field.getValue().hasValidValue()) {
                throw new CorruptingException(database, "Fields have not been setup correctly");
            }
        }
    }

    /**
     * Add this object to the database. Notify everyone required and create or add supporting objects if needed
     */
    @SuppressWarnings("unchecked")
    public <DataObjectType extends DataObject> DataObjectType add() {
        // TODO lifecycle test , add, remove, remove when object are in child list that sum


        for (ObjectFactory<?> factory : dataObjectSchema.getObjectFactories()) {
            factoryConstructed.addAll(factory.generate(this));
        }

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            field.getValue().linkToDataBase(); // Possible error here, this may need to be done AFTER being put into the database
        }

        getTrackingDatabase().add(this);

        return (DataObjectType) this;
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Remove #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Safely remove this object from the database. Disabled by default
     */
    public void remove() {
        throw new UnsupportedOperationException("Not cleared for removal");
    }

    protected void checkCanRemove() {
        // Can this object be deleted
        if (this.getChildren().size() != 0) {
            for (DataObject dataObject : this.getChildren()) {
                if (!factoryConstructed.contains(dataObject)) {
                    throw new NonCorruptingException("Cant delete this kind of object, other objects depend on it where not created by it");
                }
            }
        }

        // check that none of thease are savable
        // Are all factories of a type that support deletion?
        for (ObjectFactory<?> factory : dataObjectSchema.getObjectFactories()) {
            if (!factory.isCanDelete()) {
                throw new NonCorruptingException("This object has a factory attached that does not support deletion");
            }
        }

        // Are all object made from this one cleared for deletion
        for (DataObject children : factoryConstructed) {
            children.checkCanRemove();
        }
    }

    /**
     * Safely remove this object from the database.
     */
    protected void remove_impl() {

        // Will throw non corrupting if not clear to go
        checkCanRemove();

        // add
        {
            // add
            getTrackingDatabase().remove(this);

            // link to database
            for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
                field.getValue().removeFromDataBase();
            }

            // generate
            for (DataObject children : factoryConstructed) {
                children.remove();
            }
            factoryConstructed.clear();
        }

//        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
//            field.getValue().removeFromDataBase();
//        }

        // by now it should be fully expected
        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            if (field.getValue().isExternallyLinked()) {
                //field.getValue().isExternallyLinked();
                throw new CorruptingException(getTrackingDatabase());
            }
        }
        // clear to nuke?

        // setAllValues()
        {
            // set

            // linkWithingDataObject
        }

        // Constructor
        {
            // generate
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Core Data ###################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The core database
     */
    private final Database database;

    /**
     * The schema of this object
     */
    private final DataObject_Schema dataObjectSchema;

    /**
     * The fields for this DataObject
     */
    private final Map<String, DataField<?>> fieldMap = new HashMap<>();

    /**
     * A list of objects that were made in factories related to this object
     */
    private final List<DataObject> factoryConstructed = new ArrayList<>();

    /**
     * All my children
     */
    private final DataObjectContainer children = new DataObjectContainer();

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### General #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the core database
     *
     * @return The core database
     */
    public Database getTrackingDatabase() {
        return database;
    }

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
    //#################################################### Children ####################################################
    //------------------------------------------------------------------------------------------------------------------

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
     * Get the list of children for a a specific class type
     *
     * @param type The class type to get
     * @param <T>  The Object type
     * @return The list of children for a a specific class type
     */
    public <T extends DataObject> List<T> getChildren(Class<T> type) {
        return children.get(type);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Source Options #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the sourceOptions method
     *
     * @return The sourceOptions method
     */
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

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
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

    public Integer getId() {
        return get(DataObject_Id);
    }

    public List<DataObject> getChildren() {
        return get(DataObject_ChildrenField);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Setters #####################################################
    //------------------------------------------------------------------------------------------------------------------

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
}
