package com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema.IndividualCalculator;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.end.End_Source_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.step.Step_Source_Schema;

/**
 * A set of methods used to simplify the creation of Source_Schema's with specific behaviors
 *
 * @author Nicholas Tankard
 */
public class Source_Factory {

    /**
     * Add another element to the end of a list, used for multiple generated source arrays
     *
     * @param arr         The current array
     * @param lastElement The element to add
     * @param <T>         THe type of the array
     * @return the new array
     */
    public static <T> T[] append(T[] arr, T lastElement) {
        final int N = arr.length;
        arr = java.util.Arrays.copyOf((T[]) arr, N + 1);
        arr[N] = lastElement;
        return arr;
    }

    /**
     * Create a chain of Sources based on a list of field names. The names must be order top (the field in the container
     * object) to bottom (the last field in the chain where you get the actual data from.
     *
     * @param fieldKeys The keys for the fields to follow
     * @return A Source that contains all other child sources as needed
     */
    public static Source_Schema<?> makeSourceChain(String... fieldKeys) {
        return makeSourceChain(null, fieldKeys);
    }

    /**
     * @param individualCalculator The calculator to attach to the top of the chain
     * @see Source_Factory#makeSourceChain(String...)
     */
    public static Source_Schema<?> makeSourceChain(IndividualCalculator<?> individualCalculator, String... fieldKeys) {
        assert fieldKeys.length != 0;

        Source_Schema<?> nestedSource = new End_Source_Schema<>(fieldKeys[fieldKeys.length - 1], fieldKeys.length == 1 ? individualCalculator : null);
        for (int i = fieldKeys.length - 2; i >= 0; i--) {
            nestedSource = new Step_Source_Schema<>(fieldKeys[i], nestedSource, i == 0 ? individualCalculator : null);
        }
        return nestedSource;
    }

    /**
     * Create multiple source chains that all share a single starting point. This can only be exactly 2 layers deap
     * TODO This is only here to make a drop in replacement bor the old style of use. This should be modified to actually optimize the source chains when there is a shared link (https://www.wrike.com/open.htm?id=734695856)
     *
     * @param firstStep   The shared first step
     * @param secondSteps All the fields in the first step to watch
     * @return A array of sources equal to the number of second steps
     */
    public static Source_Schema<?>[] makeSharedStepSourceChain(String firstStep, String... secondSteps) {
        Source_Schema<?>[] sources = new Source_Schema[secondSteps.length];

        int i = 0;
        for (String field : secondSteps) {
            sources[i++] = makeSourceChain(firstStep, field);
        }
        return sources;
    }
}
