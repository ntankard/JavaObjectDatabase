package com.ntankard.javaObjectDatabase.Database;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Factory.ObjectFactory;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore.Calculate_DataCore;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataCore.Static_DataCore;
import com.ntankard.javaObjectDatabase.CoreObject.FieldContainer;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField;
import com.ntankard.javaObjectDatabase.util.FileUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.ntankard.javaObjectDatabase.util.SourceCodeInspector.classForName;

public class TrackingDatabase_Reader {

    // Root paths
    public static String ROOT_DATA_PATH = "\\Data";
    static String ROOT_FULL_IMAGE_PATH = "\\Image\\";
    public static String ROOT_FILE_PATH = "\\BudgetTracking.txt";

    // Save instance paths
    public static String INSTANCE_CLASSES_PATH = "\\Classes\\";

    /**
     * Read all files for the database from the latest save folder
     *
     * @param corePath The path that files are located in
     */
    @SuppressWarnings("unchecked")
    public static void read(String corePath, Map<String, String> nameMap) {
        if (!checkSavePath(corePath)) {
            throw new RuntimeException("Save path is invalid");
        }

        String savePath = FileUtil.getLatestSaveDirectory(corePath + ROOT_DATA_PATH);
        List<String> files = FileUtil.findFilesInDirectory(savePath + INSTANCE_CLASSES_PATH);

        List<Class<? extends DataObject>> savedClasses = new ArrayList<>();
        Map<Class<? extends DataObject>, List<SavedField<?>>> classSavedFields = new HashMap<>();
        Map<Class<? extends DataObject>, List<String[]>> classSavedLines = new HashMap<>();

        // Find all saved files, load the object types but not the data
        for (String file : files) {

            // Read the lines
            List<String[]> allLines = FileUtil.readLines(savePath + INSTANCE_CLASSES_PATH + file);

            // Check that the file has the class type and the parameter map in its header
            if (allLines.size() < 2 || allLines.get(0).length != 1 || allLines.get(1).length % 2 != 0) {
                throw new RuntimeException("File is in the wrong format");
            }

            // Parse the object
            Class<? extends DataObject> fileClass = (Class<? extends DataObject>) classForName(allLines.get(0)[0], "com.ntankard.Tracking.DataBase.Core", nameMap);

            // Save the meta data
            savedClasses.add(fileClass);
            classSavedFields.put(fileClass, getSavedFields(allLines.get(1), nameMap));
            classSavedLines.put(fileClass, allLines);
        }

        // Sort the objects so they are read correctly
        List<Class<? extends DataObject>> readOrder = sortByDependency(getConstructorDependencies(savedClasses));

        // Read each object type
        Map<Class<?>, List<DataObject>> readObjects = new HashMap<>();
        Map<Integer, DataObject> objectIDMap = new HashMap<>();
        int maxID = 0;
        for (Class<? extends DataObject> toRead : readOrder) {

            List<DataField<?>> fields = getSaveFields(toRead);
            List<SavedField<?>> savedFields = classSavedFields.get(toRead);
            List<String[]> savedLines = classSavedLines.get(toRead);

            // Load each object
            List<DataObject> loadedObjects = new ArrayList<>();
            for (int i = 2; i < savedLines.size(); i++) {
                if (fields.size() != savedLines.get(i).length)
                    throw new RuntimeException("Line parts dose not match the available params");

                // Generate
                DataObject toAdd = dataObjectFromString(toRead, savedLines.get(i), objectIDMap, savedFields);

                // Store
                loadedObjects.add(toAdd);
                if (objectIDMap.containsKey(toAdd.getId())) {
                    throw new RuntimeException("Duplicate key found");
                }
                objectIDMap.put(toAdd.getId(), toAdd);

                // Find the larges ID
                if (toAdd.getId() > maxID) {
                    maxID = toAdd.getId();
                }
            }
            readObjects.put(toRead, loadedObjects);
        }

        // Sort the objects so they can be added correctly
        List<Class<? extends DataObject>> loadOrder = sortByDependency(generateMangedDependencies(savedClasses));

        // Load into the database
        TrackingDatabase.get().setIDFloor(maxID);
        for (Class<?> toLoad : loadOrder) {
            if (readObjects.containsKey(toLoad)) {
                for (DataObject toAdd : readObjects.get(toLoad)) {
                    toAdd.add();
                }
            }
        }

        // Load the images and paths into the database
        TrackingDatabase.get().setImagePath(corePath + ROOT_FULL_IMAGE_PATH);
    }

    /**
     * Save the database to a new directory
     *
     * @param corePath The directory to put the folder
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save(String corePath) {
        if (!checkSavePath(corePath)) {
            throw new RuntimeException("Save path is invalid");
        }

        Map<Class<? extends DataObject>, List<List<String>>> classLinesToSave = new HashMap<>();
        Map<Class<? extends DataObject>, List<DataField<?>>> classFields = new HashMap<>();

        // Generate the headers
        for (Class<? extends DataObject> aClass : TrackingDatabase.get().getDataObjectTypes()) {

            // Only save solid objects
            if (Modifier.isAbstract(aClass.getModifiers())) {
                continue;
            }

            // Don't save if there are no instances to save (avoid empty file)
            if (TrackingDatabase.get().get(aClass).size() == 0) {
                continue;
            }

            // Should we save?
            if (shouldSave(aClass)) {

                // Create entry
                classLinesToSave.put(aClass, new ArrayList<>());

                // Write the object type
                classLinesToSave.get(aClass).add(new ArrayList<>(Collections.singletonList(aClass.getName())));

                // Write the object parameters to the header of the file
                classFields.put(aClass, getSaveFields(aClass));

                List<String> types = new ArrayList<>();
                for (DataField<?> constructorParameter : classFields.get(aClass)) {
                    types.add(constructorParameter.getIdentifierName());
                    types.add(constructorParameter.getType().getName());
                }
                classLinesToSave.get(aClass).add(types);
            }
        }

        // Add each individual object
        for (DataObject dataObject : TrackingDatabase.get().getAll()) {
            List<List<String>> lines = classLinesToSave.get(dataObject.getClass());
            List<DataField<?>> constructorParameters = classFields.get(dataObject.getClass());

            if (lines == null || constructorParameters == null) {
                if (shouldSave(dataObject.getClass())) {
                    throw new RuntimeException("Trying to save and object that is not setup for saving");
                } else {
                    continue;
                }
            }

            lines.add(dataObjectToString(dataObject, constructorParameters));
        }

        // Write to file
        String saveDir = FileUtil.newSaveDirectory(corePath + ROOT_DATA_PATH);
        new File(saveDir + INSTANCE_CLASSES_PATH).mkdir();

        // Save the classes
        for (Map.Entry<Class<? extends DataObject>, List<List<String>>> entry : classLinesToSave.entrySet()) {
            if (shouldSave(entry.getKey())) {
                FileUtil.writeLines(saveDir + INSTANCE_CLASSES_PATH + entry.getKey().getSimpleName() + ".csv", entry.getValue());
            }
        }
    }

    /**
     * Check that the save path is in the expected format
     *
     * @param path The path to check
     * @return True if the path is valid
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean checkSavePath(String path) {
        File coreDir = new File(path);
        if (!coreDir.exists()) {
            return false;
        }

        File dataDir = new File(path + ROOT_DATA_PATH);
        if (!dataDir.exists()) {
            return false;
        }

        File filePath = new File(path + ROOT_FILE_PATH);
        return filePath.exists();
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################## Dependency management #############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get a class order that ensures all dependencies come before a class
     *
     * @param dependencyMap The list of dependencies
     * @return The order that will ensure all dependencies come first
     */
    private static List<Class<? extends DataObject>> sortByDependency(Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> dependencyMap) {
        List<Class<? extends DataObject>> toReturn = new ArrayList<>(dependencyMap.keySet());

        // Sort the list
        boolean allSorted;
        int attempts = 0;
        do {

            // Infinite loop catch
            if (attempts++ > toReturn.size() * toReturn.size()) {
                throw new RuntimeException("Failed to sort dependencies, infinite loop detected");
            }

            allSorted = true;
            for (int i = 0; i < toReturn.size(); i++) {

                Class<? extends DataObject> toTest = toReturn.get(i);
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
                        if (dependency.isAssignableFrom(toReturn.get(j))) {
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
                    toReturn.remove(toTest);
                    toReturn.add(toTest);
                    allSorted = false;
                    break;
                }
            }
        } while (!allSorted);

        return toReturn;
    }

    /**
     * Find all dependencies to load all objects
     *
     * @param loadingObjects The objects to load
     * @return The dependency map
     */
    @SuppressWarnings("unchecked")
    private static Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> generateMangedDependencies(List<Class<? extends DataObject>> loadingObjects) {

        // Find all the objects that manage other objects, add the objects they make to there own dependency
        Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> managedObjectDependencies = new HashMap<>();
        for (Class<? extends DataObject> loadedObject : loadingObjects) {
            FieldContainer container = DataObject.getFieldContainer(loadedObject);
            if (container.getObjectFactories().size() != 0) {
                List<Class<? extends DataObject>> factories = new ArrayList<>();
                for (ObjectFactory<?, ?> factory : container.getObjectFactories()) {
                    factories.add((Class<? extends DataObject>) factory.getGeneratedType());
                }
                managedObjectDependencies.put(loadedObject, factories);
            }
        }

        // For each managed object, add dependencies for its constructors
        Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> managedObjectConstructorDependencies = new HashMap<>();
        for (Class<? extends DataObject> objectManager : managedObjectDependencies.keySet()) {                          // For each ObjectManager (Class with ObjectFactory attached)
            for (Class<? extends DataObject> managedObject : managedObjectDependencies.get(objectManager)) {            // For each ObjectManagers, ManagedObjects
                if (!managedObjectConstructorDependencies.containsKey(managedObject)) {                                 // If not seen before

                    // Get the non conflicting dependencies for the constructor
                    List<Class<? extends DataObject>> constructorParameters = new ArrayList<>();
                    for (DataField<?> field : getSaveFields(managedObject)) {       // For each ObjectManagers, ManagedObjects, Constructor parameters
                        Class<?> constructorParameterType = field.getType();
                        if (DataObject.class.isAssignableFrom(constructorParameterType)) {                              // If its a DataObject

                            // Check that the constructor parameter cant be used to build the managed object
                            FieldContainer container = DataObject.getFieldContainer(constructorParameterType);
                            boolean found = false;
                            if (container.getObjectFactories().size() != 0) {                                           // If the constructor parameter manages objects
                                for (ObjectFactory<?, ?> toTest : container.getObjectFactories()) {
                                    if (toTest.getGeneratedType().isAssignableFrom(managedObject)) {                    // Check that is dose not manage this object
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found && !constructorParameters.contains(constructorParameterType)) {                  // If it dose not manage objects, or if it dost no managed this Managed object
                                constructorParameters.add((Class<? extends DataObject>) constructorParameterType);      // Add to the list of dependencies
                            }
                        }
                    }

                    // Add them all
                    managedObjectConstructorDependencies.put(managedObject, constructorParameters);
                }
            }
        }

        // Create one master dependency map
        Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> dependencyMap = new HashMap<>();

        // Add all the ObjectManagers dependencies on the object they manage
        for (Class<? extends DataObject> objectManager : managedObjectDependencies.keySet())
            if (!dependencyMap.containsKey(objectManager))
                dependencyMap.put(objectManager, managedObjectDependencies.get(objectManager));

        // Add all the ManagedObjects dependencies on there contractors
        for (Class<? extends DataObject> managedObject : managedObjectConstructorDependencies.keySet())
            if (!dependencyMap.containsKey(managedObject))
                dependencyMap.put(managedObject, managedObjectConstructorDependencies.get(managedObject));

        // Add all missing objects
        for (Class<? extends DataObject> loadingObject : loadingObjects)
            if (!dependencyMap.containsKey(loadingObject))
                dependencyMap.put(loadingObject, new ArrayList<>());

        // To prevent an infinite loop remove any non solid objects and move there dependencies to the objects that depend on them
        List<Class<? extends DataObject>> nonSavedObjects = new ArrayList<>();
        for (Class<? extends DataObject> nonSavedObject : dependencyMap.keySet()) {
            if (!shouldSave(nonSavedObject)) {
                nonSavedObjects.add(nonSavedObject);
                List<Class<? extends DataObject>> nonSavedObject_dependants = dependencyMap.get(nonSavedObject);
                for (Class<? extends DataObject> toCheck : dependencyMap.keySet()) {
                    if (dependencyMap.get(toCheck).remove(nonSavedObject)) {                                            // Dose this object depend on the one to be removed? If so remove
                        for (Class<? extends DataObject> toAdd : nonSavedObject_dependants) {
                            if (!toAdd.isAssignableFrom(toCheck) && !toCheck.isAssignableFrom(toAdd)) {                   // If the dependence is not related to this object
                                dependencyMap.get(toCheck).add(toAdd);                                                  // Add the object nonSavedObject depends on to this object dependencies
                            }
                        }
                    }
                }
            }
        }

        // Remove all non save objects
        for (Class<? extends DataObject> aClass : nonSavedObjects) {
            dependencyMap.remove(aClass);
        }

        return dependencyMap;
    }

    /**
     * Find all dependencies needed to build all object
     *
     * @param loadingObjects The objects to load
     * @return A list of all objects that needed to be loaded to load for each class
     */
    private static Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> getConstructorDependencies(List<Class<? extends DataObject>> loadingObjects) {
        Map<Class<? extends DataObject>, List<Class<? extends DataObject>>> dependencyMap = new HashMap<>();
        for (Class<? extends DataObject> dataObjectClass : loadingObjects) {
            dependencyMap.put(dataObjectClass, getConstructorDependencies(dataObjectClass, loadingObjects));
        }
        return dependencyMap;
    }

    /**
     * Find all dependencies needed to build an object
     *
     * @param dataObjectClass The object to inspect
     * @param loadingObjects  All other know objects (used to find inherited dependencies)
     * @return A list of all objects that needed to be loaded to load this class
     */
    @SuppressWarnings("unchecked")
    private static List<Class<? extends DataObject>> getConstructorDependencies(Class<? extends DataObject> dataObjectClass, List<Class<? extends DataObject>> loadingObjects) {
        List<Class<? extends DataObject>> dependencies = new ArrayList<>();
        for (DataField<?> field : DataObject.getFieldContainer(dataObjectClass).getList()) {

            // Look for the direct dependency
            if (DataObject.class.isAssignableFrom(field.getType())) {
                if (Calculate_DataCore.class.isAssignableFrom(field.getDataCore().getClass())) {
                    continue; // TODO need to check this logic, the problem is without this a half transfer is detected, there might be a better solution
                }
                if (Static_DataCore.class.isAssignableFrom(field.getDataCore().getClass())) {
                    continue; // TODO THis is here because at least one Static_DataCore references itself resulting in a circular dependency. Some Static_DataCore may need to be added to dependency
                }
                Class<? extends DataObject> primeDependencies = (Class<? extends DataObject>) field.getType();
                dependencies.add(primeDependencies);

                // Is this dependency a solid object?
                if (!loadingObjects.contains(primeDependencies)) {
                    boolean found = false;

                    // Find an object down the tree that we can read
                    for (Class<? extends DataObject> toTest : loadingObjects) {
                        if (primeDependencies.isAssignableFrom(toTest)) {
                            dependencies.add(toTest);
                            found = true;
                        }
                    }
                    if (!found) {
                        throw new RuntimeException("Dependency detected on an object that dose not exist");
                    }
                }
            }
        }
        return dependencies;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Object to String ################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Create an object based on a string of its parameters
     *
     * @param aClass        The type of class to build
     * @param paramStrings  The values as a string
     * @param loadedObjects All past loaded objects
     * @param savedFields   The past parameters
     * @return The newly constructed object
     */
    public static DataObject dataObjectFromString(Class<? extends DataObject> aClass, String[] paramStrings, Map<Integer, DataObject> loadedObjects, List<SavedField<?>> savedFields) {

        FieldContainer fieldContainer = DataObject.getFieldContainer(aClass);
        List<DataField<?>> currentFields = DataObject.getFieldContainer(aClass).getList();
        currentFields.removeIf(field -> !field.getDataCore().canInitialSet());

        // Build up the argument list
        int i = 0;
        List<Object> args = new ArrayList<>();
        for (SavedField<?> savedField : savedFields) {
            DataField<?> currentField = fieldContainer.get(savedField.name);
            if (currentField == null)
                throw new RuntimeException("An unknown field was saved, you can probably skip past this");
            if (!currentField.getType().equals(savedField.type))
                throw new RuntimeException("Type mismatch");
            if (!currentFields.contains(currentField))
                throw new RuntimeException("Field should not have been saved or has been loaded twice");

            args.add(savedField.name);
            args.add(paramFromString(paramStrings[i++], savedField.type, loadedObjects));
            currentFields.remove(currentField);
        }
        if (currentFields.size() != 0)
            throw new RuntimeException("A expected field was not loaded");

        // Build the base object
        DataObject newDataObject;
        try {
            newDataObject = (DataObject) aClass.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return DataObject.assembleDataObject(DataObject.getFieldContainer(aClass), newDataObject, args.toArray());
    }

    /**
     * Build a parameter object from string
     *
     * @param paramString   The string value
     * @param paramType     The type
     * @param loadedObjects All past loaded objects
     * @return The object from the string
     */
    private static Object paramFromString(String paramString, Class<?> paramType, Map<Integer, DataObject> loadedObjects) {
        Object parsedData;

        if (DataObject.class.isAssignableFrom(paramType)) {
            if (paramString.equals(" ")) {
                parsedData = null;
            } else {
                DataObject dataObject = loadedObjects.get(Integer.parseInt(paramString));
                if (dataObject == null) {
                    throw new RuntimeException("Trying to load an object that is not yet in the database");
                }
                parsedData = dataObject;
            }
        } else if (String.class.isAssignableFrom(paramType)) {
            parsedData = paramString;
        } else if (Boolean.class.isAssignableFrom(paramType) || boolean.class.isAssignableFrom(paramType)) {
            parsedData = Boolean.parseBoolean(paramString);
        } else if (Double.class.isAssignableFrom(paramType) || double.class.isAssignableFrom(paramType)) {
            if (paramString.equals(" ")) {
                parsedData = null;
            } else {
                parsedData = Double.parseDouble(paramString);
            }
        } else if (Integer.class.isAssignableFrom(paramType) || int.class.isAssignableFrom(paramType)) {
            parsedData = Integer.parseInt(paramString);
        } else {
            throw new RuntimeException("Unknown data type");
        }

        return parsedData;
    }

    /**
     * Create a string of all teh values needed to rebuild the object
     *
     * @param dataObject the DataObject to convert
     * @param fields     the parameters to save for loading in the future
     * @return All objects needed to construct the object as a string
     */
    public static List<String> dataObjectToString(DataObject dataObject, List<DataField<?>> fields) {
        List<String> paramStrings = new ArrayList<>();
        for (DataField<?> field : fields) {

            // Find the method
            Method getter;
            try {
                getter = dataObject.getClass().getMethod(field.getIdentifierName());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("\n" + "Class: " + dataObject.getClass().getSimpleName() + " Method:" + field.getIdentifierName() + "\n" + e);
            }
            if (!getter.getReturnType().isAssignableFrom(field.getType()))
                throw new RuntimeException("Class:" + dataObject.getClass().getSimpleName() + " Method:" + field.getIdentifierName() + " Getter provided by ParameterMap dose not match the parameter in the constructor. Could save but would not be able to load. Aborting save");

            // Execute the getter
            Object getterValue;
            try {
                getterValue = getter.invoke(dataObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            // Convert to String
            if (getterValue == null) {
                paramStrings.add(" ");
            } else if (getterValue instanceof DataObject) {
                paramStrings.add(((DataObject) getterValue).getId().toString());
            } else {
                paramStrings.add(getterValue.toString());
            }
        }

        return paramStrings;
    }

    //------------------------------------------------------------------------------------------------------------------
    //############################################### Constructor access ###############################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Create a ConstructorMap object for its save line
     *
     * @param lines The lines to generate from
     * @return A ConstructorMap
     */
    private static List<SavedField<?>> getSavedFields(String[] lines, Map<String, String> nameMap) {
        List<SavedField<?>> savedFields = new ArrayList<>();
        for (int i = 0; i < lines.length / 2; i++) {
            savedFields.add(new SavedField<>(lines[i * 2], classForName(lines[i * 2 + 1], "com.ntankard.Tracking.DataBase.Core", nameMap)));
        }
        return savedFields;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################## Class access ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Get the fields used by a class that are not marked as do not save
     *
     * @param aClass The class to get the fields from
     * @return All fields not marked as do not save
     */
    public static List<DataField<?>> getSaveFields(Class<? extends DataObject> aClass) {
        List<DataField<?>> toReturn = DataObject.getFieldContainer(aClass).getList();
        toReturn.removeIf(field -> !field.getDataCore().canInitialSet());
        return toReturn;
    }

    /**
     * Should a class be saved?
     *
     * @param aClass The object to check
     * @return false if the object has requested to not be saved
     */
    public static boolean shouldSave(Class<? extends DataObject> aClass) {
        ParameterMap classParameterMap = aClass.getAnnotation(ParameterMap.class);
        if (classParameterMap != null) {
            return classParameterMap.shouldSave();
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################ Class containers ################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * A constructor parameter, its name and type
     */
    public static class SavedField<T> {
        Class<T> type;
        String name;

        public SavedField(String name, Class<T> type) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " " + type.toString();
        }
    }
}
