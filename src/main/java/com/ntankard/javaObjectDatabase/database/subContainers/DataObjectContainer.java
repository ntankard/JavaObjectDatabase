package com.ntankard.javaObjectDatabase.database.subContainers;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.interfaces.Ordered;
import com.ntankard.javaObjectDatabase.dataObject.interfaces.Ordered_Comparator;

import java.util.*;

public class DataObjectContainer extends Container<Class<? extends DataObject>, Map<Integer, DataObject>> {

    private final Map<Class<? extends DataObject>, List<DataObject>> masterContainer = new HashMap<>();

    /**
     * Add a new object
     *
     * @param toAdd the object to add
     */
    @SuppressWarnings("unchecked")
    @Override
    public void add(DataObject toAdd) {
        Class<? extends DataObject> aClass = toAdd.getClass();
        do {
            // Create the class container if it dos'nt already exist
            if (!container.containsKey(aClass)) {
                container.put(aClass, new HashMap<>());
            }
            if (!masterContainer.containsKey(aClass)) {
                masterContainer.put(aClass, new ArrayList<>());
            }

            // Check for duplicate IDs across the entire container
            if (container.get(aClass).containsKey(toAdd.getId())) {
                throw new RuntimeException("Duplicate key found");
            }

            // Add the object at this layer
            container.get(aClass).put(toAdd.getId(), toAdd);
            masterContainer.get(aClass).add(toAdd);

            // TODO This is critical for anything involving period range checks, should probably be handled another way
            if (Ordered.class.isAssignableFrom(aClass)) {
                masterContainer.get(aClass).sort(new Ordered_Comparator());
            }

            // Jump up the inheritance tree
            aClass = (Class<? extends DataObject>) aClass.getSuperclass();
        } while (DataObject.class.isAssignableFrom(aClass));
    }

    /**
     * Remove an object
     *
     * @param toRemove The object to remove
     */
    @SuppressWarnings("unchecked")
    @Override
    public void remove(DataObject toRemove) {
        Class<? extends DataObject> aClass = toRemove.getClass();
        do {
            // Check that we have seen this object before
            if (!container.containsKey(aClass) || !container.get(aClass).containsKey(toRemove.getId())) {
                throw new RuntimeException("Removing an object that was never added");
            }

            // Remove the object
            container.get(aClass).remove(toRemove.getId());
            masterContainer.get(aClass).remove(toRemove);

            // Jump up the inheritance tree
            aClass = (Class<? extends DataObject>) aClass.getSuperclass();
        } while (DataObject.class.isAssignableFrom(aClass));
    }

    /**
     * Get all object in the container
     *
     * @return All the objects in the containers
     */
    public List<DataObject> get() {
        return get(DataObject.class);
    }

    /**
     * Get all object of, or extending a certain class
     *
     * @param tClass The object to get
     * @param <T>    Type, same as tClass
     * @return All object that are, or inherit from tClass
     */
    @SuppressWarnings("unchecked")
    public <T extends DataObject> List<T> get(Class<T> tClass) {
        if (!masterContainer.containsKey(tClass)) {
            if (tClass == null) {
                throw new RuntimeException("Trying to get a null item");
            }
            masterContainer.put(tClass, new ArrayList<>());
        }
        return new ArrayList<T>((Collection<? extends T>) masterContainer.get(tClass));
    }

    /**
     * Get a specific object from the container
     *
     * @param tClass The type of the object
     * @param id     The ID to get
     * @param <T>    Type, same as tClass
     * @return The specific object or null
     */
    @SuppressWarnings("unchecked")
    public <T extends DataObject> T get(Class<T> tClass, Integer id) {
        if (!container.containsKey(tClass)) {
            if (tClass == null) {
                throw new RuntimeException("Trying to get a null item");
            }
            container.put(tClass, new HashMap<>());
        }
        return (T) container.get(tClass).get(id);
    }

    /**
     * Dose this container contain this object at all
     *
     * @param id The ID to check
     * @return True if this object is contained at all
     */
    public boolean contains(Integer id) {
        return container.get(DataObject.class).containsKey(id);
    }

    /**
     * Get thee next ID that will not cause a conflict with any elements in the container
     *
     * @return The next ID that will not cause a conflict with any elements in the container
     */
    public Integer getNextId() {
        int max = 0;
        for (DataObject dataObject_new : get()) {
            int value = dataObject_new.getId();
            if (value > max) {
                max = value;
            }
        }
        return (max + 1);
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<Class<? extends DataObject>> keySet() {
        return container.keySet();
    }
}
