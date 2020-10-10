package com.ntankard.javaObjectDatabase.CoreObject;

import com.ntankard.javaObjectDatabase.util.SourceCodeInspector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingDatabase_Schema {

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    // Singleton constructor
    private static TrackingDatabase_Schema master;

    /**
     * Singleton access
     */
    public static TrackingDatabase_Schema get() {
        if (master == null) {
            master = new TrackingDatabase_Schema();
        }
        return master;
    }

    /**
     * Private Constructor
     */
    private TrackingDatabase_Schema() {
    }

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Static #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get all the fields for this object an object
     *
     * @param aClass The object to get
     */
    public static FieldContainer getFieldContainer(Class<?> aClass) {
        try {
            Method method = aClass.getDeclaredMethod(DataObject.FieldName);
            return ((FieldContainer) method.invoke(null)); // TODO optimise by cashing
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ee) {
            throw new RuntimeException("Cant extract object fields", ee);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Core ######################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * A map of names that have been changed, old mapping to new
     */
    private Map<String, String> reNamed;

    /**
     * The path pointing to where the classes are
     */
    private String path;

    /**
     * All DataObject classes that can be instantiated
     */
    private List<Class<? extends DataObject>> solidClasses;

    /**
     * All DataObject classes
     */
    private List<Class<? extends DataObject>> allClasses;

    /**
     * All solid classes ordered so that if loaded in this order, all dependencies will be met
     */
    private ArrayList<Class<? extends DataObject>> decencyOrder;

    /**
     * Initialize the Schema with the location of the classes and any rename information
     *
     * @param path    The path containing the database objects
     * @param reNamed A map of names that have been changed, old mapping to new
     */
    public void init(String path, Map<String, String> reNamed) {
        if (isInitialized()) {
            throw new IllegalArgumentException("You can not initialize the schema more than once");
        }

        this.reNamed = reNamed;
        this.path = path;

        generateClassList();
        generateDependencyList();
    }

    /**
     * Generate a list of all classes
     */
    @SuppressWarnings("unchecked")
    private void generateClassList() {
        this.solidClasses = new ArrayList<>();
        this.allClasses = new ArrayList<>();

        // Find classes
        final Class<?>[] classes;
        try {
            classes = SourceCodeInspector.getClasses(path);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        // Filter classes
        for (Class<?> aClass : classes) {
            if (DataObject.class.isAssignableFrom(aClass)) {
                if (!Modifier.isAbstract(aClass.getModifiers())) {
                    solidClasses.add((Class<? extends DataObject>) aClass);
                }
                allClasses.add((Class<? extends DataObject>) aClass);
            }
        }
    }

    /**
     * Generate a class order that ensures all dependencies come before a class
     */
    private void generateDependencyList() {
        Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> dependencyMap = new HashMap<>();
        for (Class<? extends DataObject> dataObjectClass : getSolidClasses()) {
            dependencyMap.put(dataObjectClass, getSolidPreLoadDependencies(dataObjectClass));
        }

        this.decencyOrder = new ArrayList<>(dependencyMap.keySet());

        // Sort the list
        boolean allSorted;
        int attempts = 0;
        do {

            // Infinite loop catch
            if (attempts++ > decencyOrder.size() * decencyOrder.size()) {
                throw new IllegalStateException("Failed to sort dependencies, infinite loop detected");
            }

            allSorted = true;
            for (int i = 0; i < decencyOrder.size(); i++) {

                Class<? extends DataObject> toTest = decencyOrder.get(i);
                List<Class<? extends DataObject>> toTestDependencies = dependencyMap.get(toTest);

                // Check that all dependencies are earlier in the list
                boolean allFound = true;
                for (Class<?> dependency : toTestDependencies) {

                    // Dose the object this one depends on exist? if not then ignore this dependency
                    boolean knownFound = false;
                    for (Class<? extends DataObject> known : dependencyMap.keySet()) {
                        if (dependency.isAssignableFrom(known)) {
                            knownFound = true;
                            break;
                        }
                    }
                    if (!knownFound) {
                        continue;
                    }

                    // Look for a dependency earlier in the list
                    boolean found = false;
                    for (int j = 0; j < i; j++) {
                        if (dependency.isAssignableFrom(decencyOrder.get(j))) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        allFound = false;
                        break;
                    }
                }

                // If one of more dependencies is not before the test object, move it to the end of the list
                if (!allFound) {
                    decencyOrder.remove(toTest);
                    decencyOrder.add(toTest);
                    allSorted = false;
                    break;
                }
            }
        } while (!allSorted);
    }

    /**
     * Get getPreLoadDependencies for dataObjectClass but all abstract dependencies replaced with solid ones
     *
     * @param dataObjectClass The class to get the dependencies for
     * @return getPreLoadDependencies for dataObjectClass but all abstract dependencies replaced with solid ones
     */
    public List<Class<? extends DataObject>> getSolidPreLoadDependencies(Class<? extends DataObject> dataObjectClass) {
        List<Class<? extends DataObject>> rawDependencies = TrackingDatabase_Schema.getFieldContainer(dataObjectClass).getPreLoadDependencies();
        List<Class<? extends DataObject>> dependencies = new ArrayList<>();

        for (Class<? extends DataObject> aClass : rawDependencies) {
            if (getSolidClasses().contains(aClass)) {
                dependencies.add(aClass);
            } else {
                boolean found = false;
                for (Class<? extends DataObject> toTest : getSolidClasses()) {
                    if (aClass.isAssignableFrom(toTest)) {
                        dependencies.add(toTest);
                        found = true;
                    }
                }
                if (!found) {
                    throw new RuntimeException();
                }
            }
        }

        return dependencies;
    }

    /**
     * Has the schema been initialized
     *
     * @return True if the schema been initialized
     */
    public boolean isInitialized() {
        return path != null;
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public List<Class<? extends DataObject>> getSolidClasses() {
        if (!isInitialized())
            throw new IllegalStateException("Cant access the schema before it is initialized");
        return solidClasses;
    }

    public List<Class<? extends DataObject>> getAllClasses() {
        if (!isInitialized())
            throw new IllegalStateException("Path has not been set so no objects can be found");
        return allClasses;
    }

    public ArrayList<Class<? extends DataObject>> getDecencyOrder() {
        if (!isInitialized())
            throw new IllegalStateException("Path has not been set so no objects can be found");
        return decencyOrder;
    }
}
