package com.ntankard.javaObjectDatabase.database;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.util.SourceCodeInspector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class Database_Schema {

    //------------------------------------------------------------------------------------------------------------------
    //##################################################### Static #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Previously found schemas
     */
    private static final Map<String, Database_Schema> knownGlobalSchemas = new HashMap<>();

    /**
     * Generate a schema for all classes in a package
     *
     * @param path The package to search
     * @return The generated TrackingDatabase_Schema
     */
    public static synchronized Database_Schema getSchemaFromPackage(String path) {
        return getSchemaFromPackage(path, new HashMap<>());
    }

    /**
     * Generate a schema for all classes in a package
     *
     * @param path               The package to search
     * @param forcedDependencies For any DataObject, what other DataObjects do they depend on (have to be loaded first) that would not be picked up by the dependency mapping process
     * @return The generated TrackingDatabase_Schema
     */
    public static synchronized Database_Schema getSchemaFromPackage(String path, Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> forcedDependencies) {
        knownGlobalSchemas.putIfAbsent(path, new Database_Schema(findClasses(path), forcedDependencies));
        return knownGlobalSchemas.get(path);
    }

    /**
     * Generate a schema for all classes in a package plus added ones
     *
     * @param path               The package to search
     * @param solidClasses       Classes to add
     * @param forcedDependencies For any DataObject, what other DataObjects do they depend on (have to be loaded first) that would not be picked up by the dependency mapping process
     * @return The generated TrackingDatabase_Schema
     */
    public static synchronized Database_Schema getSchemaFromPackage(String path, List<Class<? extends DataObject>> solidClasses, Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> forcedDependencies) {
        List<Class<? extends DataObject>> all = new ArrayList<>();
        all.addAll(solidClasses);
        all.addAll(findClasses(path));

        return new Database_Schema(all, forcedDependencies);
    }

    /**
     * Parse a package for the desired type of class
     *
     * @param path The package to search
     * @return All classes that are instantiatable and extend DateObject
     */
    @SuppressWarnings("unchecked")
    private static List<Class<? extends DataObject>> findClasses(String path) {
        List<Class<? extends DataObject>> solidClasses = new ArrayList<>();

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
                solidClasses.add((Class<? extends DataObject>) aClass);
            }
        }
        return solidClasses;
    }

    //------------------------------------------------------------------------------------------------------------------
    //###################################################### Core ######################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * All DataObject classes that can be instantiated
     */
    private final List<Class<? extends DataObject>> solidClasses;

    /**
     * All abstract classes
     */
    private final List<Class<? extends DataObject>> abstractClasses;

    /**
     * All solid classes ordered so that if loaded in this order, all dependencies will be met
     */
    private ArrayList<Class<? extends DataObject>> dependencyOrder = null;

    /**
     * Known Class schemas
     */
    private final Map<Class<?>, DataObject_Schema> knownSchemas = new HashMap<>();

    /**
     * For any DataObject, what other DataObjects do they depend on (have to be loaded first) that would not be picked up by the dependency mapping process
     */
    private final Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> forcedDependencies;

    //------------------------------------------------------------------------------------------------------------------
    //################################################### Constructor ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Private Constructor
     */
    public Database_Schema(List<Class<? extends DataObject>> solidClasses) {
        this(solidClasses, new HashMap<>());
    }

    /**
     * Private Constructor
     */
    public Database_Schema(List<Class<? extends DataObject>> solidClasses, Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> forcedDependencies) {
        this.solidClasses = new ArrayList<>();
        this.abstractClasses = new ArrayList<>();
        this.forcedDependencies = forcedDependencies;
        for (Class<? extends DataObject> aClass : solidClasses) {
            if (!Modifier.isAbstract(aClass.getModifiers())) {
                this.solidClasses.add(aClass);
            } else {
                this.abstractClasses.add(aClass);
            }
        }

        generateClassSchemas();
    }

    /**
     * Generate all the individual class schemas
     */
    private void generateClassSchemas() {
        for (Class<? extends DataObject> aClass : solidClasses) {
            knownSchemas.put(aClass, getDataObjectSchema(aClass));
        }

        for (Class<? extends DataObject> aClass : abstractClasses) {
            DataObject_Schema schema = getDataObjectSchema(aClass);
            schema.endNow();
            knownSchemas.put(aClass, schema);
        }
    }

    /**
     * Get all the fields for this object an object
     *
     * @param aClass The object to get
     */
    private DataObject_Schema getDataObjectSchema(Class<?> aClass) {
        try {
            Method method = aClass.getDeclaredMethod("getDataObjectSchema");
            return ((DataObject_Schema) method.invoke(null));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ee) {
            throw new RuntimeException("Cant extract object fields", ee);
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

        this.dependencyOrder = new ArrayList<>(dependencyMap.keySet());

        // Sort the list
        boolean allSorted;
        int attempts = 0;
        do {

            // Infinite loop catch
            if (attempts++ > dependencyOrder.size() * dependencyOrder.size()) {
                throw new IllegalStateException("Failed to sort dependencies, infinite loop detected");
            }

            allSorted = true;
            for (int i = 0; i < dependencyOrder.size(); i++) {

                Class<? extends DataObject> toTest = dependencyOrder.get(i);
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
                        if (dependency.isAssignableFrom(dependencyOrder.get(j))) {
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
                    dependencyOrder.remove(toTest);
                    dependencyOrder.add(toTest);
                    allSorted = false;
                    break;
                }
            }
        } while (!allSorted);
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get a schema for an individual class
     *
     * @param aClass The class to get
     * @return The schema for that class
     */
    public DataObject_Schema getClassSchema(Class<?> aClass) {
        assert knownSchemas.containsKey(aClass);
        return knownSchemas.get(aClass);
    }

    /**
     * Get getPreLoadDependencies for dataObjectClass but all abstract dependencies replaced with solid ones
     *
     * @param dataObjectClass The class to get the dependencies for
     * @return getPreLoadDependencies for dataObjectClass but all abstract dependencies replaced with solid ones
     */
    private List<Class<? extends DataObject>> getSolidPreLoadDependencies(Class<? extends DataObject> dataObjectClass) {
        List<Class<? extends DataObject>> rawDependencies = getClassSchema(dataObjectClass).getPreLoadDependencies();
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

        // Add any forced, unknown dependencies
        if (forcedDependencies.containsKey(dataObjectClass)) {
            for (Class<? extends DataObject> aClass : forcedDependencies.get(dataObjectClass)) {
                if (!dependencies.contains(aClass)) {
                    dependencies.add(aClass);
                }
            }
        }

        return dependencies;
    }

    public List<Class<? extends DataObject>> getSolidClasses() {
        return Collections.unmodifiableList(solidClasses);
    }

    public List<Class<? extends DataObject>> getDependencyOrder() {
        if (dependencyOrder == null) {
            generateDependencyList();
        }
        return Collections.unmodifiableList(dependencyOrder);
    }
}
