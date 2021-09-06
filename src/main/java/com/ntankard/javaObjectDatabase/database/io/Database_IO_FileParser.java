package com.ntankard.javaObjectDatabase.database.io;

import com.ntankard.javaObjectDatabase.database.Database;
import com.ntankard.javaObjectDatabase.database.subContainers.TreeNode;
import com.ntankard.javaObjectDatabase.exception.corrupting.CorruptingException;
import com.ntankard.javaObjectDatabase.util.FileUtil;

import java.util.List;

import static com.ntankard.javaObjectDatabase.database.io.Database_IO_Util.ROOT_FILES_PATH;

public class Database_IO_FileParser {

    /**
     * Map out all available files in the database path and add them to the core database
     *
     * @param database The core database
     * @param corePath The path to the files
     */
    public static void readAvailableFiles(Database database, String corePath) {
        // Set the core path
        database.setFilesPath(corePath + ROOT_FILES_PATH);

        // Map out all files
        TreeNode<String> root = new TreeNode<>(database.getFilesPath());
        if (FileUtil.findFilesInDirectory(database.getFilesPath()).size() != 0) {
            throw new CorruptingException(database, "Files in the root files directory, all must be in sub folders");
        }
        for (String path : FileUtil.findFoldersInDirectory(database.getFilesPath())) {
            recursiveProcess(database, path, root.addChild(path));
        }

        // Set the files
        database.getFileMap().setFolderTree(root);
    }

    /**
     * Process 1 folder
     *
     * @param database The core database
     * @param myPath   The path to explore
     * @param root     The parent node
     */
    private static void recursiveProcess(Database database, String myPath, TreeNode<String> root) {
        String qualifiedPath = database.getFilesPath() + "\\" + myPath;
        List<String> folders = FileUtil.findFoldersInDirectory(qualifiedPath);
        List<String> files = FileUtil.findFilesInDirectory(qualifiedPath);
        if (folders.isEmpty()) {
            for (String file : files) {
                root.addChild(file);
            }
        } else {
            if (files.size() != 0) {
                throw new CorruptingException(database, "Files in the root files directory, all must be in sub folders");
            }
            for (String path : folders) {
                recursiveProcess(database, myPath + "\\" + path, root.addChild(path));
            }
        }
    }
}
