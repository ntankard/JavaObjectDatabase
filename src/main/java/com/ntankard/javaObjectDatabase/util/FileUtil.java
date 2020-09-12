package com.ntankard.javaObjectDatabase.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * Read lines from a csv file
     *
     * @param csvFile The path to the file to read
     * @return ALl lines read from the file
     */
    public static List<String[]> readLines(String csvFile) {
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        List<String[]> allLines = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] lines = line.split(cvsSplitBy);
                allLines.add(lines);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return allLines;
    }

    /**
     * Write lines to a csv file
     *
     * @param path  The path to write the files to
     * @param lines The lines to write
     */
    public static void writeLines(String path, List<List<String>> lines) {
        try {
            FileWriter fw = new FileWriter(path);
            for (List<String> line : lines) {
                for (String s : line) {
                    fw.write(s);
                    fw.write(",");
                }
                fw.write('\n');
            }
            fw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    /**
     * Find the files in a directory
     *
     * @param directoryPath The path to search
     * @return A list of files in the directory
     */
    public static List<String> findFilesInDirectory(String directoryPath) {
        return findInDirectory(directoryPath, File::isFile);
    }

    /**
     * Find the folders in a directory
     *
     * @param directoryPath The path to search
     * @return A list of folders in the directory
     */
    private static List<String> findFoldersInDirectory(String directoryPath) {
        return findInDirectory(directoryPath, File::isDirectory);
    }

    /**
     * Find in a directory
     *
     * @param directoryPath       The path to search
     * @param directoryFileFilter The type of tile to search for
     * @return A list of files in the directory
     */
    private static List<String> findInDirectory(String directoryPath, FileFilter directoryFileFilter) {
        File directory = new File(directoryPath);

        File[] directoryListAsFile = directory.listFiles(directoryFileFilter);

        assert directoryListAsFile != null;
        List<String> foldersInDirectory = new ArrayList<>(directoryListAsFile.length);
        for (File directoryAsFile : directoryListAsFile) {
            foldersInDirectory.add(directoryAsFile.getName());
        }

        return foldersInDirectory;
    }
}
