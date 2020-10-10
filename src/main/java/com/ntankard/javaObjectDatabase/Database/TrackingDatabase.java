package com.ntankard.javaObjectDatabase.Database;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.Database.SubContainers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TrackingDatabase {

    // Core data objects
    private List<Container<?, ?>> containers = new ArrayList<>();
    private DefaultObjectMap defaultObjectMap = new DefaultObjectMap();
    private SpecialValuesMap specialValuesMap = new SpecialValuesMap();
    private DataObjectContainer masterMap = new DataObjectContainer();
    private DataObjectClassTree dataObjectClassTree = new DataObjectClassTree();

    // Paths where images can be found
    private String imagePath;

    /**
     * The lowest ID of the loaded objects, this is stored because new objects can be created while they are loading in
     */
    private Integer IDFloor = null;

    //------------------------------------------------------------------------------------------------------------------
    //############################################### Constructor ######################################################
    //------------------------------------------------------------------------------------------------------------------

    // Singleton constructor
    private static TrackingDatabase master;

    /**
     * Singleton access
     */
    public static TrackingDatabase get() {
        if (master == null) {
            master = new TrackingDatabase();
        }
        return master;
    }

    /**
     * Reset the singleton and delete all data
     */
    public static void reset() {
        master = null;
    }

    /**
     * Private Constructor
     */
    private TrackingDatabase() {
        containers.add(masterMap);
        containers.add(defaultObjectMap);
        containers.add(specialValuesMap);
        containers.add(dataObjectClassTree);
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Data IO #########################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the next free ID
     *
     * @return The next free ID
     */
    public Integer getNextId() {
        if (IDFloor == null) {
            throw new IllegalArgumentException("Trying to get an ID before the IDFloor has been set");
        }
        return ++IDFloor;
    }

    /**
     * Set the ID floor before all the objects are loaded
     *
     * @param maxID The floor ID to set
     */
    public void setIDFloor(Integer maxID) {
        this.IDFloor = maxID;
    }

    /**
     * Add a new element to the database. New elements are repaired if needed and all relevant parents are notified
     *
     * @param dataObject The object to add
     */
    public void add(DataObject dataObject) {
        containers.forEach(container -> container.add(dataObject));
    }

    /**
     * Remove an element from the database as long as it has no children linking to it. All relevant parents are notified
     *
     * @param dataObject The object to remove
     */
    public void remove(DataObject dataObject) {
        containers.forEach(container -> container.remove(dataObject));
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Accessors #######################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get an element of the data base based on its unique ID
     *
     * @param type The data type to get
     * @param id   The ID of the object to get
     * @param <T>  The data type to return (same as type)
     * @return The element of the Database
     */
    public <T extends DataObject> T get(Class<T> type, Integer id) {
        return masterMap.get(type, id);
    }

    /**
     * Get all elements of the database of a certain type
     *
     * @param type The data type to get
     * @param <T>  The data type to return (same as type)
     * @return A unmodifiableList of all elements of that type
     */
    public <T extends DataObject> List<T> get(Class<T> type) {
        return masterMap.get(type);
    }

    /**
     * Get all the DataObject types added to this database
     *
     * @return All the DataObject types added to this database
     */
    public Set<Class<? extends DataObject>> getDataObjectTypes() {
        return masterMap.keySet();
    }

    /**
     * Combine all values into one master list
     *
     * @return The list of all values
     */
    public List<DataObject> getAll() {
        return Collections.unmodifiableList(masterMap.get());
    }

    /**
     * Get the default object of a type (specified or the 0th element)
     *
     * @param aClass The class to get
     * @param <T>    The data type to return (same as aClass)
     * @return The default object
     */
    public <T extends DataObject> T getDefault(Class<T> aClass) {
        return defaultObjectMap.getDefault(aClass);
    }

    /**
     * Get the object that is the special value
     *
     * @param aClass The type of object to search
     * @param key    The key to search
     * @return The value of type aClass that is the special value for the key
     */
    public <T extends DataObject> T getSpecialValue(Class<T> aClass, Integer key) {
        return specialValuesMap.get(aClass, key);
    }

    /**
     * Get the root node to the object inheritance tree
     *
     * @return The root node to the object inheritance tree
     */
    public TreeNode<Class<? extends DataObject>> getClassTreeRoot() {
        return dataObjectClassTree.getClassTreeRoot();
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################### Image access #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################# System Behavior ####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Should all calculations made using listeners be recalculated and checked at the time of access?
     *
     * @return True if they should be recalculated
     */
    public boolean shouldVerifyCalculations() {
        return true;
    }
}
