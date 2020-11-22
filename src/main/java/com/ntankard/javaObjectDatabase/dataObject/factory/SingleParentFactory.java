package com.ntankard.javaObjectDatabase.dataObject.factory;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;

import java.util.Collections;
import java.util.List;

public class SingleParentFactory<GeneratedType extends DataObject, PrimaryGeneratorType extends DataObject> extends ObjectFactory<GeneratedType> {

    /**
     * Interface to allow the generated object to be built
     */
    public interface GeneratedObjectConstructor<GeneratedType extends DataObject, PrimaryGeneratorType extends DataObject> {

        /**
         * Constructor one of the generated objects based on the
         *
         * @param generator The object generating the new object
         * @return A constructed, but not added object
         */
        GeneratedType generate(PrimaryGeneratorType generator);
    }

    /**
     * The object constructor
     */
    private final GeneratedObjectConstructor<GeneratedType, PrimaryGeneratorType> constructor;

    /**
     * The primary type of object that is used to generate the object
     */
    private final Class<PrimaryGeneratorType> primaryGeneratorType;

    /**
     * Constructor
     */
    public SingleParentFactory(Class<GeneratedType> generatedType,
                               Class<PrimaryGeneratorType> primaryGeneratorType,
                               GeneratedObjectConstructor<GeneratedType, PrimaryGeneratorType> constructor) {
        super(generatedType);
        this.constructor = constructor;
        this.primaryGeneratorType = primaryGeneratorType;
    }

    /**
     * Constructor
     */
    public SingleParentFactory(Class<GeneratedType> generatedType,
                               Class<PrimaryGeneratorType> primaryGeneratorType,
                               GeneratedObjectConstructor<GeneratedType, PrimaryGeneratorType> constructor,
                               GeneratorMode mode) {
        super(generatedType, mode);
        this.constructor = constructor;
        this.primaryGeneratorType = primaryGeneratorType;
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    @Override
    public void generate(DataObject generator) {
        if (primaryGeneratorType.isAssignableFrom(generator.getClass())) {
            PrimaryGeneratorType primaryGenerator = (PrimaryGeneratorType) generator;
            if (shouldBuild(primaryGenerator.getChildren(getGeneratedType()).size())) {
                constructor.generate(primaryGenerator).add();
            }
        } else {
            throw new IllegalArgumentException("A object other than one of the listed generators is trying to generate this object");
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Class<? extends DataObject>> getGenerators() {
        return Collections.singletonList(primaryGeneratorType);
    }
}
