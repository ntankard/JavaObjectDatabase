package com.ntankard.javaObjectDatabase.CoreObject;

import com.ntankard.javaObjectDatabase.CoreObject.Factory.Dummy_Factory;
import com.ntankard.javaObjectDatabase.CoreObject.Factory.ObjectFactory;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.CoreObject.Field.Properties.Display_Properties;
import com.ntankard.javaObjectDatabase.Database.SubContainers.DataObjectContainer;
import com.ntankard.javaObjectDatabase.Database.TrackingDatabase;

import java.lang.reflect.InvocationTargetException;
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
    public static String FieldName = "getFieldContainer";

    /**
     * Get all the fields for this object an object
     *
     * @param aClass The object to get
     */
    public static FieldContainer getFieldContainer(Class<?> aClass) {
        try {
            Method method = aClass.getDeclaredMethod(DataObject.FieldName);
            return ((FieldContainer) method.invoke(null));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ee) {
            throw new RuntimeException("Cant extract object fields", ee);
        }
    }

    /**
     * Set all the fields for this object, should be called by a solid object constructor
     *
     * @param fields The fields to set
     */
    public void setFields(List<DataField<?>> fields) {
        for (DataField<?> field : fields) {
            fieldMap.put(field.getIdentifierName(), field);
        }
    }

    /**
     * The fields for this DataObject
     */
    protected Map<String, DataField<?>> fieldMap = new HashMap<>();

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    public static final String DataObject_Id = "getId";

    /**
     * Get all the fields for this object
     */
    public static FieldContainer getFieldContainer() {
        FieldContainer fieldContainer = new FieldContainer();
        // ID ==========================================================================================================
        fieldContainer.add(new DataField<>(DataObject_Id, Integer.class));
        fieldContainer.get(DataObject_Id).getDisplayProperties().setVerbosityLevel(Display_Properties.INFO_DISPLAY);
        //==============================================================================================================
        return fieldContainer.endLayer(DataObject.class);
    }

    /**
     * Construct an object with initialised values
     *
     * @param fieldContainer The Fields of the object
     * @param blackObject    The constructed object without the fields attached yet
     * @param args           The values for the fields
     * @param <T>            The object type
     * @return The assembled object
     */
    public static <T extends DataObject> T assembleDataObject(FieldContainer fieldContainer, T blackObject, Object... args) {
        if (args.length / 2 * 2 != args.length)
            throw new IllegalArgumentException("Wrong amount of arguments");

        // Link the fields to the object
        fieldContainer.getList().forEach(field -> field.fieldAttached(blackObject));
        blackObject.setFields(fieldContainer.getList());
        blackObject.allValid = true;

        // Start sharing data
        fieldContainer.getList().forEach(DataField::allowValue);

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
            DataField<?> dataField = fieldContainer.get(identifier);

            // Check that all dependencies are loaded first
            boolean canDo = true;
            for (String dependant : dataField.getDependantFields()) {
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
            fieldContainer.get(identifier).set(value);
            paramIndexes.remove(0);
            done.add(identifier);
        } while (paramIndexes.size() != 0);

        return blackObject;
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

    public boolean allValid = false;

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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void add() {
        for (Map.Entry<String, DataField<?>> field : fieldMap.entrySet()) {
            field.getValue().add();
        }

        TrackingDatabase.get().add(this);

        FieldContainer fieldContainer = DataObject.getFieldContainer(this.getClass());
        for (ObjectFactory factory : fieldContainer.getObjectFactories()) {
            if (!(factory instanceof Dummy_Factory)) {
                factory.generate(this);
            }
        }
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
        if (DataObject.getFieldContainer(this.getClass()).getObjectFactories().size() != 0) {
            throw new UnsupportedOperationException("The code for deleting object that are factories has not been implemented yet");
        }

        TrackingDatabase.get().remove(this);

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
            if (DataObject.class.isAssignableFrom(field.getValue().getType())) {
                if (field.getValue().isTellParent()) {
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
        return TrackingDatabase.get().get(type);
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

    public boolean isAllValid() {
        return allValid;
    }
}
