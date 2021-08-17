package com.ntankard.javaObjectDatabase.database.io;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.util.FileUtil;
import com.ntankard.javaObjectDatabase.util.Timer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntankard.javaObjectDatabase.database.io.Database_IO_Util.*;
import static com.ntankard.javaObjectDatabase.util.SourceCodeInspector.classForName;

public class Database_IO_Reader {

    // New data
    private final List<Class<? extends DataObject>> newObjectList = new ArrayList<>();
    private final Map<Class<? extends DataObject>, List<String>> newParamList = new HashMap<>();
    private final Map<Class<? extends DataObject>, List<String[]>> newToReadData = new HashMap<>();

    // Core database
    private Database database;

    /**
     * An interface to check if a line of saved data is the one that the factory is trying to make
     */
    public interface LineMatcher {
        /**
         * Check if a line of data is the one the factory is trying to make
         *
         * @param lines The line of data to check
         * @return True if it is the expected object
         */
        boolean isTargetLine(String[] lines);
    }

    /**
     * Called by a factory to try and load an object before creating a new one
     *
     * @param type        The type of object to build
     * @param lineMatcher The tester to see if any specific line is the target object
     * @param source      The generator of the factory
     */
    public void tryLoad(Class<? extends DataObject> type, LineMatcher lineMatcher, DataObject source) {
        List<String[]> objects = newToReadData.get(type);
        if (objects == null) {
            return;
        }

        List<String[]> toRemove = new ArrayList<>();
        for (String[] line : objects) {
            if (lineMatcher.isTargetLine(line)) {
                loadObject(type, line, source);
                toRemove.add(line);
            }
        }
        newToReadData.get(type).removeAll(toRemove);
    }

    /**
     * Read all files for the database from the latest save folder
     *
     * @param rootPackageName The root package all classes are in
     * @param corePath        The path that files are located in
     */
    public void read(Database database, String rootPackageName, String corePath, Map<String, String> nameMap) {
        this.database = database;

        Timer timer = new Timer();
        timer.stopPrint("Start");

        // Check that we have a valid path to a save directory
        validateMasterDirectory(corePath);

        // Find the latest save and validate the structure
        String savePath = Database_IO_Util.getLatestSaveDirectory(corePath + ROOT_DATA_PATH);
        validateSaveInstance(savePath);

        // Extract all lines from all files
        for (String file : FileUtil.findFilesInDirectory(savePath + INSTANCE_CLASSES_PATH)) {
            validateAndExtractFile(savePath + INSTANCE_CLASSES_PATH + file, rootPackageName, nameMap);
        }

        // Find the largest saved ID to prevent overlap
        int maxID = 0;
        for (Class<? extends DataObject> toRead : newObjectList) {
            List<String[]> savedLines = newToReadData.get(toRead);
            for (String[] line : savedLines) {
                int id = Integer.parseInt(getValue(toRead, DataObject.DataObject_Id, line));

                // Find the larges ID
                if (id > maxID) {
                    maxID = id;
                }
            }
        }
        database.setIDFloor(maxID);

        // Sort the objects so they are read correctly
        List<Class<? extends DataObject>> readOrder = new ArrayList<>(database.getSchema().getDependencyOrder());

        // TODO this needs to be fixed. This is needed because currency is not detected as a dependency for some factory children
        Class<? extends DataObject> currency = null;
        for (Class<? extends DataObject> toRead : readOrder) {
            if (toRead.getSimpleName().equals("Currency")) {
                currency = toRead;
                break;
            }
        }
        readOrder.remove(currency);
        readOrder.add(0, currency);

        // Load the core data
        for (Class<? extends DataObject> toRead : readOrder) {

            // If the type was not saved, skip
            if (!newObjectList.contains(toRead)) {
                continue;
            }

            // Ensure all factories object were consumed
            if (database.getSchema().getClassSchema(toRead).getMyFactory() != null) {
                if (newToReadData.get(toRead).size() != 0) {
                    throw new RuntimeException("There are still factorised object left unloaded");
                }
            }

            // Load each object
            for (String[] lines : newToReadData.get(toRead)) {
                loadObject(toRead, lines, null);
            }
        }

        // Load the images and paths into the database
        database.setImagePath(corePath + ROOT_IMAGE_PATH);

        timer.stopPrint("End");
    }

    /**
     * Validate a class save file and extract the information to be processed
     *
     * @param path            The file to parse
     * @param rootPackageName The root package all classes are in
     * @param nameMap         Any changed names
     */
    private void validateAndExtractFile(String path, String rootPackageName, Map<String, String> nameMap) {
        // Read the lines
        List<String[]> allLines = FileUtil.readLines(path);

        // Check that the file has the class type and the parameter map in its header
        if (allLines.size() < 2 || allLines.get(0).length != 1 || allLines.get(1).length % 2 != 0)
            throw new IllegalArgumentException("Files class name or parameter's are not formatted correctly");

        // Extract the class name
        Class<? extends DataObject> fileClass = extractClass(allLines.get(0)[0], rootPackageName, nameMap); // Here

        // Extract the params
        List<String> params = extractParams(fileClass, allLines.get(1), nameMap, rootPackageName);

        // Extract the data
        List<String[]> lines = new ArrayList<>();
        for (int i = 2; i < allLines.size(); i++) {
            if (allLines.get(i).length != params.size())
                throw new IllegalStateException("Data line dose not have entries for all the fields");
            lines.add(allLines.get(i));
        }

        // Save the loaded data
        newObjectList.add(fileClass);
        newParamList.put(fileClass, params);
        newToReadData.put(fileClass, lines);
    }

    /**
     * Extract the target class from a saved file
     *
     * @param classLine       The line of data containing the class name
     * @param rootPackageName The root package all classes are in
     * @param nameMap         Any renames classes
     * @return The DataObject saved in the file
     */
    @SuppressWarnings("unchecked")
    public Class<? extends DataObject> extractClass(String classLine, String rootPackageName, Map<String, String> nameMap) {
        Class<?> baseClass = classForName(classLine, rootPackageName, nameMap);
        if (!DataObject.class.isAssignableFrom(baseClass))
            throw new IllegalStateException("Object is not of type DataObject");
        return (Class<? extends DataObject>) baseClass;
    }

    /**
     * Extract the ordered list of saved parameter's and check that they match the current versions
     *
     * @param fileClass       The type of object being loaded
     * @param paramStrings    The line containing the params
     * @param nameMap         Any renamed classes
     * @param rootPackageName The root package all classes are in
     * @return An order list of the parameters
     */
    public List<String> extractParams(Class<? extends DataObject> fileClass, String[] paramStrings, Map<String, String> nameMap, String rootPackageName) {
        // Get the expected field's
        DataObject_Schema dataObjectSchema = database.getSchema().getClassSchema(fileClass);
        List<DataField_Schema<?>> currentFields = dataObjectSchema.getSavedFields();

        // Check the size
        if (currentFields.size() * 2 != paramStrings.length)
            throw new IllegalStateException("The object was saved with the wrong number of parameters");

        // Check each individual field
        List<String> params = new ArrayList<>();
        for (int i = 0; i < currentFields.size(); i++) {
            String savedName = paramStrings[i * 2];
            Class<?> savedType = classForName(paramStrings[i * 2 + 1], rootPackageName, nameMap);

            DataField_Schema<?> field = dataObjectSchema.get(paramStrings[i * 2]);
            if (field == null)
                throw new IllegalStateException("A unknown field has been saved: " + paramStrings[i * 2]);

            if (!field.getType().equals(savedType))
                throw new IllegalStateException("The field is of the wrong type. Expected: " + field.getType().getSimpleName() + " Actual: " + savedType.getSimpleName());

            params.add(savedName);
        }

        return params;
    }

    /**
     * Get a specific field from the save line
     *
     * @param type       The type of object to looks at
     * @param primaryKey The name of the field to get
     * @param lines      The lines to extract from
     * @return The string of that field
     */
    public String getValue(Class<? extends DataObject> type, String primaryKey, String[] lines) {
        int index = newParamList.get(type).indexOf(primaryKey);
        if (index < 0) {
            throw new IllegalArgumentException("Trying to get a field that dose not exist");
        }
        return lines[index];
    }

    /**
     * Get the ID field value
     *
     * @param type       The type of object to looks at
     * @param primaryKey The name of the field to get
     * @param lines      The lines to extract from
     * @return The ID field as a integer
     */
    public Integer getID(Class<? extends DataObject> type, String primaryKey, String[] lines) {
        return Integer.parseInt(getValue(type, primaryKey, lines));
    }

    /**
     * Load an individual object
     *
     * @param toRead            The type of object to load
     * @param lines             The lines for this instance
     * @param underConstruction The under construction object that may have created this one
     */
    public void loadObject(Class<? extends DataObject> toRead, String[] lines, DataObject underConstruction) {
        DataObject toAdd = dataObjectFromString(toRead, lines, underConstruction);
        toAdd.add();
    }

    /**
     * Create an object based on a string of its parameters
     *
     * @param aClass       The type of class to build
     * @param paramStrings The values as a string
     * @return The newly constructed object
     */
    public DataObject dataObjectFromString(Class<? extends DataObject> aClass, String[] paramStrings, DataObject underConstruction) {

        DataObject_Schema dataObjectSchema = database.getSchema().getClassSchema(aClass);
        List<DataField_Schema<?>> currentFields = new ArrayList<>(dataObjectSchema.getList());
        currentFields.removeIf(field -> !field.getSourceMode().equals(DataField_Schema.SourceMode.DIRECT));

        // Build up the argument list
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < newParamList.get(aClass).size(); i++) {
            args.add(newParamList.get(aClass).get(i));
            args.add(paramFromString(paramStrings[i], dataObjectSchema.get(newParamList.get(aClass).get(i)).getType(), underConstruction));
        }

        // Build the base object
        DataObject newDataObject = null;
        try {
            Constructor<?>[] constructors = aClass.getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].equals(Database.class)) {
                    newDataObject = (DataObject) constructor.newInstance(database);
                    break;
                }
                // TODO add a check for the constructor with the schema
            }
            if (newDataObject == null) {
                throw new RuntimeException();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return newDataObject.setAllValues(args.toArray());
    }

    /**
     * Build a parameter object from string
     *
     * @param paramString The string value
     * @param paramType   The type
     * @return The object from the string
     */
    private Object paramFromString(String paramString, Class<?> paramType, DataObject underConstruction) {
        Object parsedData;

        if (DataObject.class.isAssignableFrom(paramType)) {
            if (paramString.equals(" ")) {
                parsedData = null;
            } else {
                DataObject dataObject = database.get(DataObject.class, Integer.parseInt(paramString));//loadedObjects.get(Integer.parseInt(paramString));
                if (dataObject == null && underConstruction != null) {
                    if (underConstruction.getId().equals(Integer.parseInt(paramString))) {
                        dataObject = underConstruction;
                    } else {
                        throw new RuntimeException("Trying to load an object that is not yet in the database");
                    }
                } else if (dataObject == null) {
                    throw new RuntimeException();
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
            if (paramString.equals(" ")) {
                parsedData = null;
            } else {
                parsedData = Integer.parseInt(paramString);
            }
        } else {
            throw new RuntimeException("Unknown data type");
        }

        return parsedData;
    }
}
