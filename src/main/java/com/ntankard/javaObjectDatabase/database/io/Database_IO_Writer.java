package com.ntankard.javaObjectDatabase.database.io;

import com.ntankard.javaObjectDatabase.dataField.DataField_Schema;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;
import com.ntankard.javaObjectDatabase.util.FileUtil;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.ntankard.javaObjectDatabase.database.io.Database_IO_Util.*;

public class Database_IO_Writer {

    /**
     * Save the database to a new directory
     *
     * @param corePath The directory to put the folder
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save(Database database, String corePath) {
        validateMasterDirectory(corePath);

        Map<Class<? extends DataObject>, List<List<String>>> classLinesToSave = new HashMap<>();
        Map<Class<? extends DataObject>, List<DataField_Schema<?>>> classFields = new HashMap<>();

        // Generate the headers
        for (Class<? extends DataObject> aClass : database.getDataObjectTypes()) {

            // Only save solid objects
            if (Modifier.isAbstract(aClass.getModifiers())) {
                continue;
            }

            // Don't save if there are no instances to save (avoid empty file)
            if (database.get(aClass).size() == 0) {
                continue;
            }

            // Should we save?
            if (shouldSave(aClass)) {

                // Create entry
                classLinesToSave.put(aClass, new ArrayList<>());

                // Write the object type
                classLinesToSave.get(aClass).add(new ArrayList<>(Collections.singletonList(aClass.getName())));

                // Write the object parameters to the header of the file
                classFields.put(aClass, database.getSchema().getClassSchema(aClass).getSavedFields());

                List<String> types = new ArrayList<>();
                for (DataField_Schema<?> constructorParameter : classFields.get(aClass)) {
                    types.add(constructorParameter.getIdentifierName());
                    types.add(constructorParameter.getType().getName());
                }
                classLinesToSave.get(aClass).add(types);
            }
        }

        // Add each individual object
        for (DataObject dataObject : database.getAll()) {
            List<List<String>> lines = classLinesToSave.get(dataObject.getClass());
            List<DataField_Schema<?>> constructorParameters = classFields.get(dataObject.getClass());

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
        String saveDir = Database_IO_Util.newSaveDirectory(corePath + ROOT_DATA_PATH);
        new File(saveDir + INSTANCE_CLASSES_PATH).mkdir();

        // Save the classes
        for (Map.Entry<Class<? extends DataObject>, List<List<String>>> entry : classLinesToSave.entrySet()) {
            if (shouldSave(entry.getKey())) {
                FileUtil.writeLines(saveDir + INSTANCE_CLASSES_PATH + entry.getKey().getSimpleName() + ".csv", entry.getValue());
            }
        }
    }

    /**
     * Create a string of all the values needed to rebuild the object
     *
     * @param dataObject the DataObject to convert
     * @param fields     the parameters to save for loading in the future
     * @return All objects needed to construct the object as a string
     */
    public static List<String> dataObjectToString(DataObject dataObject, List<DataField_Schema<?>> fields) {
        List<String> paramStrings = new ArrayList<>();
        for (DataField_Schema<?> field : fields) {

            // Execute the getter
            Object getterValue = dataObject.get(field.getIdentifierName());

            // Convert to String
            String paramString;
            if (getterValue == null) {
                paramString = " ";
            } else if (getterValue instanceof DataObject) {
                paramString = ((DataObject) getterValue).getId().toString();
            } else {
                paramString = getterValue.toString();
            }
            if (paramString.contains(",")) {
                throw new CorruptingException(dataObject.getTrackingDatabase(), "Trying to data data with a illegal character in it");
            }
            paramStrings.add(paramString);
        }

        return paramStrings;
    }
}
