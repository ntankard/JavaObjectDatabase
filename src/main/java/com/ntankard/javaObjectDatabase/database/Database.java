package com.ntankard.javaObjectDatabase.database;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.io.Database_IO_Reader;
import com.ntankard.javaObjectDatabase.database.subContainers.*;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.ntankard.javaObjectDatabase.database.io.Database_IO_Util.ROOT_FILES_PATH;

public class Database {

    // Core data objects
    private final List<Container<?, ?>> containers = new ArrayList<>();
    private final DefaultObjectMap defaultObjectMap = new DefaultObjectMap();
    private final SpecialValuesMap specialValuesMap = new SpecialValuesMap();
    private final DataObjectContainer masterMap = new DataObjectContainer();
    private final DataObjectClassTree dataObjectClassTree = new DataObjectClassTree();
    private final UnprocessedFileMap unprocessedFileMap = new UnprocessedFileMap();

    // Paths where supporting files can be found
    private final String filesPath;

    /**
     * The reader used to make this database
     */
    private final Database_IO_Reader reader;

    /**
     * The schema this database is built on
     */
    private final Database_Schema schema;

    /**
     * The lowest ID of the loaded objects, this is stored because new objects can be created while they are loading in
     */
    private Integer IDFloor = null;

    /**
     * The last known corrupting exception for this database
     */
    private CorruptingException corruptingException = null;

    //------------------------------------------------------------------------------------------------------------------
    //############################################### Constructor ######################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Private Constructor
     */
    public Database(Database_Schema schema, Database_IO_Reader reader, String corePath) {
        this.schema = schema;
        this.reader = reader;
        this.filesPath = corePath + ROOT_FILES_PATH;
        containers.add(masterMap);
        containers.add(defaultObjectMap);
        containers.add(specialValuesMap);
        containers.add(dataObjectClassTree);
        containers.add(unprocessedFileMap);
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
        T dataObject = defaultObjectMap.getDefault(aClass);
        if (dataObject == null) {
            return get(aClass).get(0);
        }
        return dataObject;
    }

    /**
     * Get the object that is the special value
     *
     * @param aClass The type of object to search
     * @param key    The key to search
     * @return The value of type aClass that is the special value for the key
     */
    public <T extends DataObject> T getSpecialValue(Class<T> aClass, String key) {
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

    /**
     * Get the reader the builds this database
     *
     * @return The reader the builds this database
     */
    public Database_IO_Reader getReader() {
        return reader;
    }

    /**
     * Get the schema this database is built on
     *
     * @return The schema this database is built on
     */
    public Database_Schema getSchema() {
        return schema;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ File access #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public String getFilesPath() {
        return filesPath;
    }

    public UnprocessedFileMap getFileMap() {
        return unprocessedFileMap;
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

    /**
     * Called when a corrupting exception is raised
     *
     * @param corruptingException The exception that caused the corruption
     */
    public void notifyCorruptingException(CorruptingException corruptingException) {
        this.corruptingException = corruptingException;
    }

    /**
     * Is the database clear to save?
     *
     * @return True if the database is clear to save
     */
    public boolean canSave() {
        return corruptingException == null;
    }
}
