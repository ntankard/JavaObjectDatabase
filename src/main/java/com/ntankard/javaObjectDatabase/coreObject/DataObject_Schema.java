package com.ntankard.javaObjectDatabase.coreObject;

import com.ntankard.javaObjectDatabase.coreObject.factory.ObjectFactory;
import com.ntankard.javaObjectDatabase.coreObject.field.DataField_Schema;
import com.ntankard.javaObjectDatabase.coreObject.field.dataCore.Static_DataCore;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataObject_Schema {

    // Field -----------------------------------------------------------------------------------------------------------

    /**
     * The master container for the fields
     */
    public final Map<String, DataField_Schema<?>> masterMap = new HashMap<>();

    /**
     * A list stored look up optimization
     */
    private final List<DataField_Schema<?>> list = new ArrayList<>();

    /**
     * The last added field
     */
    private DataField_Schema<?> last = null;

    /**
     * The last order value set
     */
    private int lastOrder = 0;

    /**
     * The current amount to increment while incrementing the order (must be a multiple of 10)
     */
    private int orderStep = 10000000;

    // Class Behavior --------------------------------------------------------------------------------------------------

    /**
     * All the factory this class has
     */
    private final List<ObjectFactory<?>> objectFactories = new ArrayList<>();

    /**
     * A factory to create this object
     */
    private ObjectFactory<?> myFactory = null;

    // State -----------------------------------------------------------------------------------------------------------

    /**
     * Has this container been finalised
     */
    private boolean isFinalized = false;

    /**
     * All the layers used to build the final object. Stored for testing purposes
     */
    public final List<Class<? extends DataObject>> inheritedObjects = new ArrayList<>();

    /**
     * The lowest level of object that has added to this container. Stored for testing purposes
     */
    public Class<? extends DataObject> solidObjectType = null;

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Field IO ####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Add a field after the key provided
     *
     * @param toFollowKey The key for the field to follow
     * @param dataFieldSchema   The field to add
     * @return The field that was just added
     */
    public DataField_Schema<?> add(String toFollowKey, DataField_Schema<?> dataFieldSchema) {
        if (isFinalized)
            throw new IllegalStateException("Trying to modify a finalised container");
        lastOrder = masterMap.get(toFollowKey).getDisplayProperties().getOrder();
        return add(dataFieldSchema);
    }

    /**
     * Add a new field
     *
     * @param dataFieldSchema The field to add
     * @return The field that was just added
     */
    public DataField_Schema<?> add(DataField_Schema<?> dataFieldSchema) {
        if (isFinalized)
            throw new IllegalStateException("Trying to modify a finalised container");
        if (masterMap.containsKey(dataFieldSchema.getIdentifierName()))
            throw new IllegalArgumentException("A field with this key has been added before");


        list.add(dataFieldSchema);
        masterMap.put(dataFieldSchema.getIdentifierName(), dataFieldSchema);
        last = dataFieldSchema;

        lastOrder = lastOrder + orderStep;
        dataFieldSchema.getDisplayProperties().setOrder(lastOrder);

        return dataFieldSchema;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Class Behavior #################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Add a new object factory to the list
     *
     * @param objectFactory The object factory to add
     */
    public void addObjectFactory(ObjectFactory objectFactory) {
        if (isFinalized)
            throw new IllegalStateException("Trying to modify a finalised container");
        this.objectFactories.add(objectFactory);
    }

    /**
     * Set a factory to create this object
     *
     * @param myFactory A factory to create this object
     */
    public void setMyFactory(ObjectFactory<?> myFactory) {
        if (isFinalized)
            throw new IllegalStateException("Trying to modify a finalised container");

        if (this.myFactory != null)
            throw new IllegalArgumentException("Can't set more that one self factory");

        this.myFactory = myFactory;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Finalization ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * End this layer of the group. Order will increment by 10 times less after this and will follow the filed toFollowKey
     *
     * @param endLayer    The Object for the current layer
     * @param toFollowKey The key of the field for all future orders to follow
     * @return This
     */
    public DataObject_Schema endLayer(Class<? extends DataObject> endLayer, String toFollowKey) {
        if (isFinalized)
            throw new IllegalStateException("Trying to modify a finalised container");
        lastOrder = masterMap.get(toFollowKey).getDisplayProperties().getOrder();
        return endLayer(endLayer);
    }

    /**
     * End this layer of the group. Order will increment by 10 times less after this and will follow the last added field
     *
     * @param endLayer The Object for the current layer
     * @return This
     */
    public DataObject_Schema endLayer(Class<? extends DataObject> endLayer) {
        if (isFinalized)
            throw new IllegalStateException("Trying to modify a finalised container");
        if (!Modifier.isAbstract(endLayer.getModifiers()))
            throw new IllegalStateException("Trying to end a layer on a solid object, the last object in the chain should call endContainer(...) instead");
        if (inheritedObjects.contains(endLayer))
            throw new IllegalArgumentException("Trying end a layer twice");
        if (orderStep <= 10)
            throw new IllegalStateException("To many layers added, can no longer decrement the order");

        inheritedObjects.add(endLayer);
        orderStep = orderStep / 10;

        return this;
    }

    /**
     * The the full container, no fields can be added after this. The container will be validated and the fields will be finalized
     *
     * @param end The final solid class containing all the fields
     * @return This
     */
    @SuppressWarnings("unchecked")
    public DataObject_Schema finaliseContainer(Class<? extends DataObject> end) {
        if (isFinalized)
            throw new IllegalStateException("Trying to modify a finalised container");
        if (Modifier.isAbstract(end.getModifiers()))
            throw new IllegalStateException("Trying to end the container on an abstract object");
        if (inheritedObjects.contains(end))
            throw new IllegalArgumentException("Trying end a layer twice");

        inheritedObjects.add(end);

        // Test that the tree was constructed correctly
        Class<? extends DataObject> aClass = end;
        do {
            if (inheritedObjects.size() == 0)
                throw new IllegalStateException("Not every layer invoked endLayer(...)");
            Class<? extends DataObject> toTest = inheritedObjects.get(inheritedObjects.size() - 1);

            if (!toTest.equals(aClass)) {
                throw new IllegalStateException("A layer has been missed. Solid class:" + end.getSimpleName() + " Expected class:" + toTest.getSimpleName() + " Actual class:" + aClass.getSimpleName());
            }

            // Jump up the inheritance tree
            aClass = (Class<? extends DataObject>) aClass.getSuperclass();
            inheritedObjects.remove(toTest);
        } while (DataObject.class.isAssignableFrom(aClass));

        // Finalise all fields
        for (DataField_Schema<?> dataFieldSchema : list) {
            dataFieldSchema.containerFinished(end);
        }

        solidObjectType = end;
        isFinalized = true;
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Field Access ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get a field from its key
     *
     * @param key The key to get
     * @param <T> The type of the field
     * @return The requested field
     */
    @SuppressWarnings("unchecked")
    public <T> DataField_Schema<T> get(String key) {
        if (!masterMap.containsKey(key))
            throw new IllegalArgumentException("Key not found");
        return (DataField_Schema<T>) masterMap.get(key);
    }

    /**
     * Get the list of all fields
     *
     * @return The list of fields
     */
    public List<DataField_Schema<?>> getList() {
        return list;
    }

    /**
     * Get the list of all fields with a certain, or above verbosity level
     *
     * @param verbosity The level to filter on
     * @return The list of fields
     */
    public List<DataField_Schema<?>> getVerbosityDataFields(int verbosity) {
        List<DataField_Schema<?>> fields = new ArrayList<>();
        for (DataField_Schema<?> f : getList()) {
            if (f.getDisplayProperties().getVerbosityLevel() > verbosity) {
                continue;
            }
            if (!f.getDisplayProperties().getShouldDisplay()) {
                continue;
            }
            fields.add(f);
        }
        return fields;
    }

    /**
     * Get the last field that was added
     *
     * @param <T> The type of the field
     * @return The last field that was added
     */
    @SuppressWarnings("unchecked")
    public <T> DataField_Schema<T> getLast() {
        return (DataField_Schema<T>) last;
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################### Special inspection ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get all the filed that will ba saved
     *
     * @return All the filed that will ba saved
     */
    public List<DataField_Schema<?>> getSavedFields() {
        List<DataField_Schema<?>> toReturn = new ArrayList<>(list);
        toReturn.removeIf(field -> !field.getSourceMode().equals(DataField_Schema.SourceMode.DIRECT));
        return toReturn;
    }

    /**
     * Get all the classes this one needs to load an instance
     *
     * @return All the classes this one needs to load an instance
     */
    @SuppressWarnings("unchecked")
    public List<Class<? extends DataObject>> getPreLoadDependencies() {
        List<Class<? extends DataObject>> dependencies = new ArrayList<>();

        // Get my direct dependencies
        for (DataField_Schema<?> field : list) {

            // Look for the direct dependency
            if (DataObject.class.isAssignableFrom(field.getType())) {
                if (field.getDataCore_factory() != null && Static_DataCore.Static_DataCore_Factory.class.isAssignableFrom(field.getDataCore_factory().getClass())) {
                    continue; // TODO THis is here because at least one Static_DataCore references itself resulting in a circular dependency. Some Static_DataCore may need to be added to dependency
                }
                Class<? extends DataObject> primeDependencies = (Class<? extends DataObject>) field.getType();

                if (primeDependencies.equals(this.solidObjectType)) {
                    continue;
                }

                if (!dependencies.contains(primeDependencies)) {
                    dependencies.add(primeDependencies);
                }
            }
        }

        if (myFactory != null) {
            for (Class<? extends DataObject> parent : myFactory.getGenerators()) {
                if (!dependencies.contains(parent)) {
                    dependencies.add(parent);
                }
            }
        }

        return dependencies;
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################# Class Behavior Access ##############################################
    //------------------------------------------------------------------------------------------------------------------

    public List<ObjectFactory<?>> getObjectFactories() {
        return objectFactories;
    }

    public ObjectFactory<?> getMyFactory() {
        return myFactory;
    }
}