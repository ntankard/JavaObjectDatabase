package com.ntankard.javaObjectDatabase.Database;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;
import com.ntankard.javaObjectDatabase.CoreObject.Field.DataField_Schema;
import com.ntankard.javaObjectDatabase.CoreObject.DataObject_Schema;
import com.ntankard.javaObjectDatabase.util.FileUtil;
import com.ntankard.javaObjectDatabase.util.Timer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntankard.javaObjectDatabase.Database.TrackingDatabase_Reader_Util.*;
import static com.ntankard.javaObjectDatabase.util.SourceCodeInspector.classForName;

public class TrackingDatabase_Reader_Read {

    // New data
    static List<Class<? extends DataObject>> newObjectList;
    static Map<Class<? extends DataObject>, List<String>> newParamList;
    static Map<Class<? extends DataObject>, List<String[]>> newToReadData;

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
    public static void tryLoad(Class<? extends DataObject> type, LineMatcher lineMatcher, DataObject source) {
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
     * @param corePath The path that files are located in
     */
    public static void read(String corePath, Map<String, String> nameMap) {
        Timer timer = new Timer();
        timer.stopPrint("Start");

        // Clear all stored memory
        resetState();

        // Check that we have a valid path to a save directory
        validateMasterDirectory(corePath);

        // Find the latest save and validate the structure
        String savePath = TrackingDatabase_Reader_Util.getLatestSaveDirectory(corePath + ROOT_DATA_PATH);
        validateSaveInstance(savePath);

        // Extract all lines from all files
        for (String file : FileUtil.findFilesInDirectory(savePath + INSTANCE_CLASSES_PATH)) {
            validateAndExtractFile(savePath + INSTANCE_CLASSES_PATH + file, nameMap);
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
        TrackingDatabase.get().setIDFloor(maxID);

        // Sort the objects so they are read correctly
        List<Class<? extends DataObject>> readOrder = TrackingDatabase_Schema.get().getDecencyOrder();//sortByDependency(getConstructorDependencies(newObjectList));

        // Load the core data
        for (Class<? extends DataObject> toRead : readOrder) {

            // If the type was not saved, skip
            if (!newObjectList.contains(toRead)) {
                continue;
            }

            // Ensure all factories object were consumed
            if (TrackingDatabase_Schema.getFieldContainer(toRead).getMyFactory() != null) {
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
        TrackingDatabase.get().setImagePath(corePath + ROOT_IMAGE_PATH);

        timer.stopPrint("End");
    }

    /**
     * Reset the reader instance
     */
    public static void resetState() {
        newObjectList = new ArrayList<>();
        newParamList = new HashMap<>();
        newToReadData = new HashMap<>();
    }

    /**
     * Validate a class save file and extract the information to be processed
     *
     * @param path    The file to parse
     * @param nameMap Any changed names
     */
    private static void validateAndExtractFile(String path, Map<String, String> nameMap) {
        // Read the lines
        List<String[]> allLines = FileUtil.readLines(path);

        // Check that the file has the class type and the parameter map in its header
        if (allLines.size() < 2 || allLines.get(0).length != 1 || allLines.get(1).length % 2 != 0)
            throw new IllegalArgumentException("Files class name or parameter's are not formatted correctly");

        // Extract the class name
        Class<? extends DataObject> fileClass = extractClass(allLines.get(0)[0], nameMap);

        // Extract the params
        List<String> params = extractParams(fileClass, allLines.get(1), nameMap);

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
     * @param classLine The line of data containing the class name
     * @param nameMap   Any renames classes
     * @return The DataObject saved in the file
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends DataObject> extractClass(String classLine, Map<String, String> nameMap) {
        Class<?> baseClass = classForName(classLine, "com.ntankard.Tracking.DataBase.Core", nameMap);
        if (!DataObject.class.isAssignableFrom(baseClass))
            throw new IllegalStateException("Object is not of type DataObject");
        return (Class<? extends DataObject>) baseClass;
    }

    /**
     * Extract the ordered list of saved parameter's and check that they match the current versions
     *
     * @param fileClass    The type of object being loaded
     * @param paramStrings The line containing the params
     * @param nameMap      Any renamed classes
     * @return An order list of the parameters
     */
    public static List<String> extractParams(Class<? extends DataObject> fileClass, String[] paramStrings, Map<String, String> nameMap) {
        // Get the expected field's
        DataObject_Schema dataObjectSchema = TrackingDatabase_Schema.getFieldContainer(fileClass);
        List<DataField_Schema<?>> currentFields = dataObjectSchema.getSavedFields();

        // Check the size
        if (currentFields.size() * 2 != paramStrings.length)
            throw new IllegalStateException("The object was saved with the wrong number of parameters");

        // Check each individual field
        List<String> params = new ArrayList<>();
        for (int i = 0; i < currentFields.size(); i++) {
            String savedName = paramStrings[i * 2];
            Class<?> savedType = classForName(paramStrings[i * 2 + 1], "com.ntankard.Tracking.DataBase.Core", nameMap);

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
    public static String getValue(Class<? extends DataObject> type, String primaryKey, String[] lines) {
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
    public static Integer getID(Class<? extends DataObject> type, String primaryKey, String[] lines) {
        return Integer.parseInt(getValue(type, primaryKey, lines));
    }

    /**
     * Load an individual object
     *
     * @param toRead            The type of object to load
     * @param lines             The lines for this instance
     * @param underConstruction The under construction object that may have created this one
     */
    public static void loadObject(Class<? extends DataObject> toRead, String[] lines, DataObject underConstruction) {
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
    public static DataObject dataObjectFromString(Class<? extends DataObject> aClass, String[] paramStrings, DataObject underConstruction) {

        DataObject_Schema dataObjectSchema = TrackingDatabase_Schema.getFieldContainer(aClass);
        List<DataField_Schema<?>> currentFields = dataObjectSchema.getList();
        currentFields.removeIf(field -> !field.getSourceMode().equals(DataField_Schema.SourceMode.DIRECT));

        // Build up the argument list
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < newParamList.get(aClass).size(); i++) {
            args.add(newParamList.get(aClass).get(i));
            args.add(paramFromString(paramStrings[i], dataObjectSchema.get(newParamList.get(aClass).get(i)).getType(), underConstruction));
        }

        // Build the base object
        DataObject newDataObject;
        try {
            newDataObject = (DataObject) aClass.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return DataObject.assembleDataObject(TrackingDatabase_Schema.getFieldContainer(aClass), newDataObject, args.toArray());
    }

    /**
     * Build a parameter object from string
     *
     * @param paramString The string value
     * @param paramType   The type
     * @return The object from the string
     */
    private static Object paramFromString(String paramString, Class<?> paramType, DataObject underConstruction) {
        Object parsedData;

        if (DataObject.class.isAssignableFrom(paramType)) {
            if (paramString.equals(" ")) {
                parsedData = null;
            } else {
                DataObject dataObject = TrackingDatabase.get().get(DataObject.class, Integer.parseInt(paramString));//loadedObjects.get(Integer.parseInt(paramString));
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
            parsedData = Integer.parseInt(paramString);
        } else {
            throw new RuntimeException("Unknown data type");
        }

        return parsedData;
    }
}
