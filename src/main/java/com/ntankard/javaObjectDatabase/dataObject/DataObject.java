package com.ntankard.javaObjectDatabase.dataObject;

import com.ntankard.javaObjectDatabase.dataObject.factory.ObjectFactory;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataField.DataField;
import com.ntankard.javaObjectDatabase.dataField.properties.Display_Properties;
import com.ntankard.javaObjectDatabase.database.subContainers.DataObjectContainer;
import com.ntankard.javaObjectDatabase.database.Database;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntankard.javaObjectDatabase.dataField.DataField.NewFieldState.N_ACTIVE;

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
            instanceList.add(field);
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
     * A list of the fields on this DataObject
     */
    private final List<DataField<?>> instanceList = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    public static final String DataObject_Id = "getId";

    /**
     * Get all the fields for this object
     */
    public static DataObject_Schema getDataObjectSchema() {
        DataObject_Schema dataObjectSchema = new DataObject_Schema();
        // ID ==========================================================================================================
        dataObjectSchema.add(new DataField_Schema<>(DataObject_Id, Integer.class));
        dataObjectSchema.get(DataObject_Id).getDisplayProperties().setVerbosityLevel(Display_Properties.INFO_DISPLAY);
        //==============================================================================================================
        return dataObjectSchema.endLayer(DataObject.class);
    }

    /**
     * Constructor
     */
    public DataObject(Database database) {
        this.database = database;
        this.dataObjectSchema = database.getSchema().getClassSchema(this.getClass());

        // Link the fields to the object
        List<DataField<?>> instanceList = new ArrayList<>();
        for (DataField_Schema<?> field : dataObjectSchema.getList()) {
            DataField<?> instance = field.generate(this);
            instanceList.add(instance);
        }
        this.setFields(instanceList);
    }

    /**
     * Constructor
     */
    public DataObject(DataObject_Schema dataObjectSchema) {
        this.dataObjectSchema = dataObjectSchema;

        // Link the fields to the object
        List<DataField<?>> instanceList = new ArrayList<>();
        for (DataField_Schema<?> field : dataObjectSchema.getList()) {
            DataField<?> instance = field.generate(this);
            instanceList.add(instance);
        }
        this.setFields(instanceList);
    }

    /**
     * Initialise all the values of the field.
     *
     * @param database The database this will link to
     * @param args     ALl fields to initialize
     * @return This
     */
    public DataObject setAllValues(Database database, Object... args) {
        this.database = database;
        return setAllValues(args);
    }

    /**
     * Initialise all the values of the field.
     *
     * @param args ALl fields to initialize
     * @return This
     */
    public DataObject setAllValues(Object... args) {
        if (args.length / 2 * 2 != args.length)
            throw new IllegalArgumentException("Wrong amount of arguments");

        // Start sharing data
        instanceList.forEach(DataField::allowValue);

        List<String> done = new ArrayList<>();
        List<Integer> paramIndexes = new ArrayList<>();
        for (int i = 0; i < (args.length / 2); i++) {
            paramIndexes.add(i);
        }

        // Load each of the fields
        int attempt = paramIndexes.size() * 2;
        do {
            // Check for an infinite loop
            attempt--;
            if (attempt <= 0)
                throw new RuntimeException("Imposable dependency is causing an infinite loop");

            // Get the next field to set
            int toAdd = paramIndexes.get(0);
            String identifier = args[toAdd * 2].toString();
            Object value = args[toAdd * 2 + 1];
            DataField_Schema<?> dataFieldSchema = dataObjectSchema.get(identifier);

            // Check that all dependencies are loaded first
            boolean canDo = true;
            for (String dependant : dataFieldSchema.getDependantFields()) {
                if (!done.contains(dependant)) {
                    canDo = false;
                    break;
                }
            }
            if (!canDo) {
                // This field depends on another that is not loaded, move it t other end of the line
                paramIndexes.remove(0);
                paramIndexes.add(toAdd);
                continue;
            }

            // Load the field
            this.getField(identifier).set(value);
            paramIndexes.remove(0);
            done.add(identifier);
        } while (paramIndexes.size() != 0);

        // phase 3, database read write


        return this;
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

    /**
     * Set the core database
     *
     * @param database The core database
     */
    public void setTrackingDatabase(Database database) {
        this.database = database;
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
     * {@inheritDoc
     */
    @Override
    public String toString() {
        return get(DataObject_Id).toString();
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Database access  ################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Add this object to the database. Notify everyone required and create or add supporting objects if needed
     */
    public void add() {

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            field.getValue().initialFilter(); // Possible error here, this may need to be done AFTER being put into the database
        }

        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            if (!field.getValue().getState().equals(N_ACTIVE)) {
                throw new RuntimeException();
            }
        }

        for (ObjectFactory<?> factory : dataObjectSchema.getObjectFactories()) {
            factory.generate(this);
        }

        for (Map.Entry<String, DataField<?>> field : this.fieldMap.entrySet()) {
            field.getValue().forceNotify(); // possible error here, this may need to be done AFTER being put into the database
        }

        getTrackingDatabase().add(this);
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
     * All object to be notified when a child is linked or unlinked
     */
    private final List<ChildrenListener<?>> childrenListeners = new ArrayList<>();

    /**
     * A listener for notifying when new children are added or removed to this object
     *
     * @param <T> THe type of child object
     */
    public interface ChildrenListener<T extends DataObject> {

        /**
         * Called when a new child is linked to this object
         *
         * @param dataObject The added object
         */
        void childAdded(T dataObject);

        /**
         * Called when a child is unlinked from this object
         *
         * @param dataObject The object that was removed
         */
        void childRemoved(T dataObject);
    }

    /**
     * Add a new ChildrenListener listeners
     *
     * @param childrenListener The ChildrenListener
     * @param <T>              tClass type
     */
    public <T extends DataObject> void addChildrenListener(ChildrenListener<T> childrenListener) {
        childrenListeners.add(childrenListener);
    }

    /**
     * Remove a ChildrenListener listeners
     *
     * @param childrenListener The ChildrenListener
     * @param <T>              tClass type
     */
    public <T extends DataObject> void removeChildrenListener(ChildrenListener<T> childrenListener) {
        childrenListeners.remove(childrenListener);
    }

    /**
     * Get all object to be notified when a child is linked or unlinked
     *
     * @return All object to be notified when a child is linked or unlinked
     */
    public List<ChildrenListener<?>> getChildrenListeners() {
        return childrenListeners;
    }

    /**
     * Get all the parents of this object
     *
     * @return All the parents of this object
     */
    private List<DataObject> getParentsImpl() {
        List<DataObject> toReturn = new ArrayList<>();
        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            if (DataObject.class.isAssignableFrom(field.getValue().getDataFieldSchema().getType())) {
                if (field.getValue().getDataFieldSchema().isTellParent()) {
                    if (field.getValue().get() != null) {
                        try {
                            toReturn.add((DataObject) field.getValue().get());
                        } catch (Exception e) {
                            throw new RuntimeException();
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    /**
     * Notify that a child has linked to this object as its parent
     *
     * @param linkObject The child
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void notifyChildLink(DataObject linkObject) {
        children.add(linkObject);

        for (ChildrenListener childrenListener : childrenListeners) {
            childrenListener.childAdded(linkObject);
        }
    }

    /**
     * Notify that a child has un linked this object as its parent
     *
     * @param linkObject The child
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void notifyChildUnLink(DataObject linkObject) {
        for (ChildrenListener childrenListener : childrenListeners) {
            childrenListener.childRemoved(linkObject);
        }

        children.remove(linkObject);
    }

    /**
     * Get all children
     *
     * @return The List of all children
     */
    public List<DataObject> getChildren() {
        return children.get();
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
     * Get the list of children for a a specific class type
     *
     * @param type The class type to get
     * @param key  The ID of the object to get
     * @param <T>  The Object type
     * @return The list of children for a a specific class type
     */
    public <T extends DataObject> T getChildren(Class<T> type, Integer key) {
        return children.get(type, key);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Integer getId() {
        return get(DataObject_Id);
    }

    public List<DataObject> getParents() {
        return getParentsImpl();
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

    /**
     * Check that all parents are linked properly
     */
    public void validateParents() {
        for (DataObject dataObject : getParents()) {
            if (!dataObject.getChildren().contains(this)) {
                throw new RuntimeException("Not registered with a parent");
            }
        }
    }

    /**
     * Check that all children are linked properly
     */
    public void validateChildren() {
        for (DataObject dataObject : children.get()) {
            if (!dataObject.getParents().contains(this)) {
                throw new RuntimeException("Not registered with a child");
            }
        }
    }
}
