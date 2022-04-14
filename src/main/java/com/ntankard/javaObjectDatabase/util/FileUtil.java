package com.ntankard.javaObjectDatabase.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * Read lines from a csv file non separated
     *
     * @param csvFile The path to the file to read
     * @return ALl lines read from the file
     */
    public static List<String> readRawLines(String csvFile) {
        BufferedReader br = null;
        String line;
        List<String> allLines = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                allLines.add(line);
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
     * Write raw lines to a csv file
     *
     * @param path  The path to write files too
     * @param lines The raw lines to write
     */
    public static void writeRawLines(String path, List<String> lines) {
        try {
            FileWriter fw = new FileWriter(path, false);
            for (String line : lines) {
                fw.write(line);
                fw.write('\n');
            }
            fw.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public static List<String> findFoldersInDirectory(String directoryPath) {
        return findInDirectory(directoryPath, File::isDirectory);
    }

    /**
     * Find in a directory
     *
     * @param directoryPath       The path to search
     * @param directoryFileFilter The type of tile to search for
     * @return A list of files in the directory
     */
    public static List<String> findInDirectory(String directoryPath, FileFilter directoryFileFilter) {
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
