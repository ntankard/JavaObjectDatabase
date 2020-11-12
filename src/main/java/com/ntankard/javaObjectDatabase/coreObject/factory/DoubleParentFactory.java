package com.ntankard.javaObjectDatabase.coreObject.factory;

import com.ntankard.javaObjectDatabase.coreObject.DataObject;
import com.ntankard.javaObjectDatabase.database.TrackingDatabase;
import com.ntankard.javaObjectDatabase.util.set.TwoParent_Children_Set;
import com.ntankard.javaObjectDatabase.database.TrackingDatabase_Reader_Read;

import java.util.ArrayList;
import java.util.List;

public class DoubleParentFactory<GeneratedType extends DataObject, PrimaryGeneratorType extends DataObject, SecondaryGeneratorType extends DataObject> extends ObjectFactory<GeneratedType> {

    /**
     * Interface to allow the generated object to be built
     */
    public interface GeneratedObjectConstructor<GeneratedType extends DataObject, PrimaryGeneratorType extends DataObject, SecondaryGeneratorType extends DataObject> {

        /**
         * Constructor one of the generated objects based on the
         *
         * @param primaryGenerator   The primary object generating the new object
         * @param secondaryGenerator The Secondary object generating the new object
         * @return A constructed, but not added object
         */
        GeneratedType generate(PrimaryGeneratorType primaryGenerator, SecondaryGeneratorType secondaryGenerator);
    }

    /**
     * The primary type of object that is used to generate the object
     */
    private final Class<PrimaryGeneratorType> primaryGeneratorType;

    /**
     * The field key to access the primary parent value in the constructed object
     */
    private final String primaryKey;

    /**
     * The secondary type of object that is used to generate the object
     */
    private final Class<SecondaryGeneratorType> secondaryGeneratorType;

    /**
     * The field key to access the secondary parent value in the constructed object
     */
    private final String secondaryKey;

    /**
     * The object constructor
     */
    private final GeneratedObjectConstructor<GeneratedType, PrimaryGeneratorType, SecondaryGeneratorType> constructor;

    /**
     * Constructor
     */
    public DoubleParentFactory(Class<GeneratedType> generatedType,
                               Class<PrimaryGeneratorType> primaryGeneratorType,
                               String primaryKey, Class<SecondaryGeneratorType> secondaryGeneratorType,
                               String secondaryKey, GeneratedObjectConstructor<GeneratedType, PrimaryGeneratorType, SecondaryGeneratorType> constructor) {
        super(generatedType);
        this.primaryGeneratorType = primaryGeneratorType;
        this.primaryKey = primaryKey;
        this.secondaryGeneratorType = secondaryGeneratorType;
        this.secondaryKey = secondaryKey;
        this.constructor = constructor;
    }

    /**
     * Constructor
     */
    public DoubleParentFactory(Class<GeneratedType> generatedType,
                               Class<PrimaryGeneratorType> primaryGeneratorType,
                               String primaryKey, Class<SecondaryGeneratorType> secondaryGeneratorType,
                               String secondaryKey, GeneratedObjectConstructor<GeneratedType, PrimaryGeneratorType, SecondaryGeneratorType> constructor,
                               GeneratorMode mode) {
        super(generatedType, mode);
        this.primaryGeneratorType = primaryGeneratorType;
        this.primaryKey = primaryKey;
        this.secondaryGeneratorType = secondaryGeneratorType;
        this.secondaryKey = secondaryKey;
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc
     */
    @SuppressWarnings("unchecked")
    @Override
    public void generate(DataObject generator) {
        if (primaryGeneratorType.isAssignableFrom(generator.getClass())) {
            PrimaryGeneratorType primaryGenerator = (PrimaryGeneratorType) generator;
            for (SecondaryGeneratorType secondaryGenerator : TrackingDatabase.get().get(secondaryGeneratorType)) {
                tryLoad(primaryGenerator, secondaryGenerator, generator);
                if (shouldBuild(new TwoParent_Children_Set<>(getGeneratedType(), primaryGenerator, secondaryGenerator).get().size())) {
                    constructor.generate(primaryGenerator, secondaryGenerator).add();
                }
            }
        } else if (secondaryGeneratorType.isAssignableFrom(generator.getClass())) {
            SecondaryGeneratorType secondaryGenerator = (SecondaryGeneratorType) generator;
            for (PrimaryGeneratorType primaryGenerator : TrackingDatabase.get().get(primaryGeneratorType)) {
                tryLoad(primaryGenerator, secondaryGenerator, generator);
                if (shouldBuild(new TwoParent_Children_Set<>(getGeneratedType(), secondaryGenerator, primaryGenerator).get().size())) {
                    constructor.generate(primaryGenerator, secondaryGenerator).add();
                }
            }
        } else {
            throw new IllegalArgumentException("A object other than one of the listed generators is trying to generate this object");
        }
    }

    /**
     * Try to load an object from the database
     *
     * @param primaryGenerator   The primary generator type
     * @param secondaryGenerator The secondary generator type
     * @param source             Primary or secodnary generator that invoked generate
     */
    private void tryLoad(PrimaryGeneratorType primaryGenerator, SecondaryGeneratorType secondaryGenerator, DataObject source) {
        TrackingDatabase_Reader_Read.tryLoad(getGeneratedType(), new TrackingDatabase_Reader_Read.LineMatcher() {
            @Override
            public boolean isTargetLine(String[] lines) {
                Integer id = TrackingDatabase_Reader_Read.getID(getGeneratedType(), primaryKey, lines);
                if (!primaryGenerator.getId().equals(id)) {
                    return false;
                }
                id = TrackingDatabase_Reader_Read.getID(getGeneratedType(), secondaryKey, lines);
                return secondaryGenerator.getId().equals(id);
            }
        }, source);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<Class<? extends DataObject>> getGenerators() {
        List<Class<? extends DataObject>> toReturn = new ArrayList<>();
        toReturn.add(secondaryGeneratorType);
        toReturn.add(primaryGeneratorType);
        return toReturn;
    }

    //------------------------------------------------------------------------------------------------------------------
    //#################################################### Getters #####################################################
    //------------------------------------------------------------------------------------------------------------------

    public Class<SecondaryGeneratorType> getSecondaryGeneratorType() {
        return secondaryGeneratorType;
    }
}