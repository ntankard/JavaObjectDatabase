package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

import java.util.Arrays;

/**
 * Utilities for the Source objects
 *
 * @author Nicholas Tankard
 */
public class Source_Util {

    /**
     * Get the container of the lowest field in a source chain. Those does NOT get the actually value of the lowest field
     * but instead gets the container object that can be used to get tht object or null if any step in the chain is null
     *
     * @param topContainer The container for the field at the top of the chain
     * @param fieldKeys    The chain of fields
     * @return The container that can be used to get the value from the lowest field or null if the chain is broken
     */
    public static DataObject getLowestContainer(DataObject topContainer, String... fieldKeys) {
        DataObject step = topContainer;
        String[] toStep = Arrays.copyOfRange(fieldKeys, 0, fieldKeys.length - 1);
        for (String stepKey : toStep) {

            if (step.get(stepKey) == null) {
                return null;
            }
            step = step.get(stepKey);
        }
        return step;
    }
}
