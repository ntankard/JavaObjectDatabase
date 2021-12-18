package com.ntankard.javaObjectDatabase.dataField.dataCore;

import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.Derived_DataCore_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema;
import com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Schema.IndividualCalculator;
import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.util.set.SetFilter;

import java.util.ArrayList;
import java.util.List;

import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Factory.makeSourceChain;
import static com.ntankard.javaObjectDatabase.dataField.dataCore.derived.source.Source_Util.getLowestContainer;

/**
 * A set of methods used to simplify the creation of Derived_DataCore_Schema with specific behaviors
 *
 * @author Nicholas Tankard
 */
public class DataCore_Factory {

    /**
     * Create a Derived_DataCore_Schema with a single source chain who's value is the exact values of the last field in
     * the chain. Sets a value calculated at run time if any value in the chain is null
     *
     * @param fieldKeys       The list of fields keys leading to the desired field.
     * @param getter          The method to generate the default values when needed if any field in the chain is null
     * @param <FieldType>     The type of field the DataCore is connected to
     * @param <ContainerType> The Type of object the FieldType instance is connected to
     * @return A newly constructed Derived_DataCore_Factory
     */
    public static <FieldType, ContainerType extends DataObject> Derived_DataCore_Schema<FieldType, ContainerType> createDefaultDirectDerivedDataCore(DefaultGetter<FieldType> getter, String... fieldKeys) {
        return new Derived_DataCore_Schema<>(container -> {
            DataObject end = getLowestContainer(container, fieldKeys);
            return end == null ? getter.getDefault(container) : end.get(fieldKeys[fieldKeys.length - 1]);
        }, makeSourceChain(fieldKeys));
    }

    /**
     * Create a Derived_DataCore_Schema with a single source chain who's value is the exact values of the last field in
     * the chain. Sets a static default value if any value in the chain is null
     *
     * @param fieldKeys       The list of fields keys leading to the desired field.
     * @param defaultValue    The value to use if any field in the chain is null
     * @param <FieldType>     The type of field the DataCore is connected to
     * @param <ContainerType> The Type of object the FieldType instance is connected to
     * @return A newly constructed Derived_DataCore_Factory
     */
    public static <FieldType, ContainerType extends DataObject> Derived_DataCore_Schema<FieldType, ContainerType> createDefaultDirectDerivedDataCore(FieldType defaultValue, String... fieldKeys) {
        return new Derived_DataCore_Schema<>(container -> {
            DataObject end = getLowestContainer(container, fieldKeys);
            return end == null ? defaultValue : end.get(fieldKeys[fieldKeys.length - 1]);
        }, makeSourceChain(fieldKeys));
    }

    /**
     * Create a Derived_DataCore_Schema with a single source chain who's value is the exact values of the last field in
     * the chain. Sets null if any value in the chain is null
     *
     * @param fieldKeys       The list of fields keys leading to the desired field.
     * @param <FieldType>     The type of field the DataCore is connected to
     * @param <ContainerType> The Type of object the FieldType instance is connected to
     * @return A newly constructed Derived_DataCore_Factory
     */
    public static <FieldType, ContainerType extends DataObject> Derived_DataCore_Schema<FieldType, ContainerType> createDirectDerivedDataCore(String... fieldKeys) {
        return createDefaultDirectDerivedDataCore(null, fieldKeys);
    }

    /**
     * Create a Derived_DataCore_Schema that will behave like a Multi Parent List. The only parent in this case will be
     * the container of the DataCore.
     *
     * @see DataCore_Factory#createMultiParentList(Class, SetFilter, String...)
     */
    public static <ListContentType extends DataObject>
    Derived_DataCore_Schema<List<ListContentType>, ?> createSelfParentList(Class<ListContentType> listContentType,
                                                                           SetFilter<ListContentType> setFilter) {
        // Create the shared calculator
        FullParentCalculator<ListContentType> fullParentCalculator = new FullParentCalculator<>(listContentType, setFilter);

        // Create the individual calculator
        List<IndividualParentCalculator<ListContentType>> individualParentCalculators = new ArrayList<>();
        individualParentCalculators.add(new IndividualParentCalculator<>(fullParentCalculator, DataObject.DataObject_ChildrenField));
        fullParentCalculator.setIndividualParentCalculators(individualParentCalculators);

        // Create the core
        return new Derived_DataCore_Schema<>(fullParentCalculator, individualParentCalculators.get(0).getSource());
    }

    /**
     * Create a Derived_DataCore_Schema that will behave like a Multi Parent List. The parent fields can only exist in
     * the container of the DataCore. Nested parents are not supported in this method
     * <p>
     * A Multi Parent List is a list of objects that meet the following criteria.
     * <p>
     * 1. The DataObject is a child of each of the parents (the child has atlases 1 field who's value is the parent)
     * 2. The DataObject is of, or inherits from a specific type.
     * 3. The DataObject meets certain filter criteria
     * <p>
     * If all 3 criteria are met the DataObject will be part of the list
     *
     * @param listContentType   The type of object that will be in the final list (or inherits from)
     * @param parentFieldKeys   The keys for all the field that act as parents
     * @param <ListContentType> The type of DataObject in the final list
     * @return A DataCore_Factory that behaves like a Multi Parent List
     */
    public static <ListContentType extends DataObject>
    Derived_DataCore_Schema<List<ListContentType>, ?> createMultiParentList(Class<ListContentType> listContentType,
                                                                            String... parentFieldKeys) {
        return createMultiParentList(listContentType, null, parentFieldKeys);
    }

    /**
     * @param setFilter Any filters to remove items from the list
     * @see DataCore_Factory#createMultiParentList(Class, String...)
     */
    public static <ListContentType extends DataObject>
    Derived_DataCore_Schema<List<ListContentType>, ?> createMultiParentList(Class<ListContentType> listContentType,
                                                                            SetFilter<ListContentType> setFilter,
                                                                            String... parentFieldKeys) {
        assert parentFieldKeys.length != 0;

        // Create the shared calculator
        FullParentCalculator<ListContentType> fullParentCalculator = new FullParentCalculator<>(listContentType, setFilter);

        // Create the individual calculators
        List<IndividualParentCalculator<ListContentType>> individualParentCalculators = new ArrayList<>();
        for (String field : parentFieldKeys) {
            individualParentCalculators.add(new IndividualParentCalculator<>(fullParentCalculator, field, DataObject.DataObject_ChildrenField));
        }
        fullParentCalculator.setIndividualParentCalculators(individualParentCalculators);

        // Extract all the sources
        List<Source_Schema<?>> source_schemas = new ArrayList<>();
        individualParentCalculators.forEach(listContentTypeIndividualParentCalculator -> source_schemas.add(listContentTypeIndividualParentCalculator.getSource()));

        // Create the core
        return new Derived_DataCore_Schema<>(fullParentCalculator, source_schemas.toArray(new Source_Schema[0]));
    }

    //------------------------------------------------------------------------------------------------------------------
    //################################################# Inner Classes ##################################################
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Interface to get a default value at run time once the database is established
     *
     * @param <FieldType> The type of field the DataCore is connected to
     */
    public interface DefaultGetter<FieldType> {

        /**
         * Generate the default value
         *
         * @param container The object containing the field
         * @return The default value
         */
        FieldType getDefault(DataObject container);
    }

    /**
     * The calculator used to modify a Multi Parent List when one of the parents has a change (either the parent changes
     * or its children change). Should be used in conjunction with FullParentCalculator.
     * <p>
     * A Multi Parent List is a list of objects that meet the following criteria.
     * <p>
     * 1. The DataObject is a child of each of the parents (the child has atlases 1 field who's value is the parent)
     * 2. The DataObject is of, or inherits from a specific type.
     * 3. The DataObject meets certain filter criteria
     * <p>
     * If all 3 criteria are met the DataObject will be part of the list
     *
     * @param <ListContentType> The type of DataObject in the final list
     */
    private static class IndividualParentCalculator<ListContentType extends DataObject> implements IndividualCalculator<List<ListContentType>> {

        /**
         * The Calculator used to create the field from all parents
         */
        private final FullParentCalculator<ListContentType> fullParentCalculator;

        /**
         * The keys leading to the parent. Must be sequential, top to bottom from the container
         */
        private final String[] fieldKeys;

        /**
         * The source generated for the fieldKeys and storing this individual calculator. Stored to prevent the risk of double creation
         */
        private final Source_Schema<?> sourceSchema;

        /**
         * Constructor
         *
         * @param fullParentCalculator The Calculator used to create the field from all parents
         * @param fieldKeys            The keys leading to the parent. Must be sequential, top to bottom from the container
         */
        public IndividualParentCalculator(FullParentCalculator<ListContentType> fullParentCalculator, String... fieldKeys) {
            assert fieldKeys.length != 0;
            assert fieldKeys[fieldKeys.length - 1].equals(DataObject.DataObject_ChildrenField);

            this.fullParentCalculator = fullParentCalculator;
            this.fieldKeys = fieldKeys;
            this.sourceSchema = makeSourceChain(this, fieldKeys);
        }

        /**
         * @inheritDoc
         */
        @Override
        public void doIndividualRecalculate(Derived_DataCore<List<ListContentType>, ?> parent, List<ListContentType> oldValue, List<ListContentType> newValue) {
            assert oldValue != null || newValue != null;
            if (oldValue != null) {
                for (ListContentType toRemove : oldValue) {
                    // TODO this might be unneeded, its only here to prevent an "you have not added" error in the container
                    if (fullParentCalculator.shouldAdd(toRemove, parent.getDataField().getContainer(), this)) {
                        parent.doRemove(toRemove);
                    }
                }
            }
            if (newValue != null) {
                for (ListContentType toAdd : newValue) {
                    if (fullParentCalculator.shouldAdd(toAdd, parent.getDataField().getContainer(), this)) {
                        parent.doAdd(toAdd);
                    }
                }
            }
        }

        /**
         * Get the source generated for the fieldKeys and storing this individual calculator
         *
         * @return The source generated for the fieldKeys and storing this individual calculator
         */
        public Source_Schema<?> getSource() {
            return sourceSchema;
        }
    }

    /**
     * The calculator used to calculate a Multi Parent List from scratch. Should be used in conjunction with IndividualParentCalculator.
     * <p>
     * A Multi Parent List is a list of objects that meet the following criteria.
     * <p>
     * 1. The DataObject is a child of each of the parents (the child has atlases 1 field who's value is the parent)
     * 2. The DataObject is of, or inherits from a specific type.
     * 3. The DataObject meets certain filter criteria
     * <p>
     * If all 3 criteria are met the DataObject will be part of the list
     *
     * @param <ListContentType> The type of DataObject in the final list
     */
    private static class FullParentCalculator<ListContentType extends DataObject> implements Derived_DataCore_Schema.Calculator<List<ListContentType>, DataObject> {

        /**
         * The type of object that will be in the final list (or inherits from)
         */
        private final Class<ListContentType> listContentType;

        /**
         * Any filters to remove items from the list
         */
        private final SetFilter<ListContentType> setFilter;

        /**
         * Calculators for each of the individual parents. Also contains the parents properties
         */
        private List<IndividualParentCalculator<ListContentType>> individualParentCalculators;

        /**
         * Constructor
         *
         * @param listContentType The type of object that will be in the final list (or inherits from)
         * @param setFilter       Any filters to remove items from the list
         */
        public FullParentCalculator(Class<ListContentType> listContentType, SetFilter<ListContentType> setFilter) {
            this.listContentType = listContentType;
            this.setFilter = setFilter;
        }

        /**
         * Set the calculators for each of the individual parents
         *
         * @param individualParentCalculators The Calculators for each of the individual parents
         */
        public void setIndividualParentCalculators(List<IndividualParentCalculator<ListContentType>> individualParentCalculators) {
            this.individualParentCalculators = individualParentCalculators;
        }

        /**
         * Check if a single object should be added to the final list
         *
         * @param toTest       The DataObject to test
         * @param container    The top level container
         * @param sourceParent The parent that generated this request (not checked against this parent, it is assumes
         *                     that the parent did its own check before calling this method
         * @return True if toTest matches all criteria to add
         */
        public boolean shouldAdd(ListContentType toTest, DataObject container, IndividualParentCalculator<ListContentType> sourceParent) {

            // Is the object the correct type?
            if (!listContentType.isAssignableFrom(toTest.getClass())) {
                return false;
            }

            // Is the object a child of all the parents?
            for (IndividualParentCalculator<ListContentType> individual : individualParentCalculators) {
                if (!individual.equals(sourceParent)) {
                    DataObject end = getLowestContainer(container, individual.fieldKeys);
                    if (end == null || !end.hasChild(toTest)) {
                        return false;
                    }
                }
            }

            // Dose the object pass all required filters?
            return setFilter == null || setFilter.shouldAdd(toTest);
        }

        /**
         * @inheritDoc
         */
        @Override
        public List<ListContentType> reCalculate(DataObject container) {
            IndividualParentCalculator<ListContentType> firstParent = individualParentCalculators.get(0);

            // Get all objects from the first parent
            DataObject end = getLowestContainer(container, firstParent.fieldKeys);
            if (end == null) {
                return new ArrayList<>();
            }
            List<ListContentType> base = end.getChildren(listContentType);

            // Validate each object against all other parents, there type and the filters
            base.removeIf(endFieldType -> !shouldAdd(endFieldType, container, firstParent));

            return base;
        }
    }
}
