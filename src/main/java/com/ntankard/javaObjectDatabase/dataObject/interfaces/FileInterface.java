package com.ntankard.javaObjectDatabase.dataObject.interfaces;

public interface FileInterface {

    /**
     * Get the relative path from the save dir to the folder containing the file attached to this object
     *
     * @return The relative path to the folder containing the file
     */
    String getContainerPath();

    /**
     * Get the name of the file attached to this object
     *
     * @return The file name
     */
    String getFileName();
}
