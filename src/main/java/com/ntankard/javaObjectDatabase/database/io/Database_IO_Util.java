package com.ntankard.javaObjectDatabase.database.io;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.database.ParameterMap;
import com.ntankard.javaObjectDatabase.util.FileUtil;

import java.io.File;
import java.util.List;

import static com.ntankard.javaObjectDatabase.util.FileUtil.findFoldersInDirectory;

public class Database_IO_Util {

    // Root paths
    public static String ROOT_DATA_PATH = "\\Data";
    public static String ROOT_FILES_PATH = "\\Files";
    public static String ROOT_FILE_PATH = "\\BudgetTracking.txt";

    // Save instance paths
    public static String INSTANCE_CLASSES_PATH = "\\Classes\\";

    /**
     * Check that the save path is in the expected format
     *
     * @param path The path to check
     */
    protected static void validateMasterDirectory(String path) {
        File coreDir = new File(path);
        if (!coreDir.exists()) {
            throw new IllegalStateException("The core database path dose not exist");
        }

        File dataDir = new File(path + ROOT_DATA_PATH);
        if (!dataDir.exists()) {
            throw new IllegalStateException("The core database path dose not contain a data directory");
        }

        File filesDir = new File(path + ROOT_FILES_PATH);
        if (!filesDir.exists()) {
            throw new IllegalStateException("The core database path dose not contain a files directory");
        }

        File filePath = new File(path + ROOT_FILE_PATH);
        if (!filePath.exists()) {
            throw new IllegalStateException("The core database path dose not contain the database file");
        }

        List<String> folders = findFoldersInDirectory(path + ROOT_DATA_PATH);
        for (String saveInstance : folders) {
            try {
                Double.parseDouble(saveInstance);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("The core database, data path has folders that are not numbers (save instances)");
            }
        }
    }

    /**
     * Check that the saved instance is valid
     *
     * @param path The path to check
     */
    public static void validateSaveInstance(String path) {
        if (!FileUtil.findFilesInDirectory(path).isEmpty()) {
            throw new IllegalStateException("Unexpected file in the root save directory");
        }

        List<String> folders = FileUtil.findFoldersInDirectory(path);
        if (folders.size() != 1) {
            throw new IllegalStateException("The wrong number of folders is in the root save directory");
        }

        if (!folders.get(0).equals(INSTANCE_CLASSES_PATH.replace("\\", ""))) {
            throw new IllegalStateException("No " + INSTANCE_CLASSES_PATH + " folder found");
        }

        String classesPath = path + INSTANCE_CLASSES_PATH;
        if (!FileUtil.findFoldersInDirectory(classesPath).isEmpty()) {
            throw new IllegalStateException("Unexpected folders in the " + INSTANCE_CLASSES_PATH + " folder");
        }

        List<String> classFiles = FileUtil.findFilesInDirectory(classesPath);
        if (classFiles.size() == 0) {
            throw new IllegalStateException("No classes to load");
        }

        for (String classFile : classFiles) {
            if (!classFile.endsWith(".csv")) {
                throw new IllegalStateException("A file of the wrong type was found: " + classFile);
            }
        }
    }

    /**
     * Find the latest save directory
     *
     * @param corePath The core directory
     * @return The path of the latest save directory
     */
    public static String getLatestSaveDirectory(String corePath) {
        int max = 0;
        List<String> folders = findFoldersInDirectory(corePath);
        for (String s : folders) {
            int value = Integer.parseInt(s);
            if (value > max) {
                max = value;
            }
        }
        return corePath + "\\" + max + "\\";
    }

    /**
     * Create an empty save directory in the core directory
     *
     * @param corePath The core directory
     * @return The new save path
     */
    public static String newSaveDirectory(String corePath) {
        // Find the next save dir
        int max = 0;
        List<String> folders = findFoldersInDirectory(corePath);
        for (String s : folders) {
            int value = Integer.parseInt(s);
            if (value > max) {
                max = value;
            }
        }
        String csvFile = corePath + "\\" + (max + 1) + "\\";

        // Make the folder
        //noinspection ResultOfMethodCallIgnored
        new File(csvFile).mkdir();

        return csvFile;
    }

    // TO REMOVE -------------------------------------------------------------------------------------------------------

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
}
