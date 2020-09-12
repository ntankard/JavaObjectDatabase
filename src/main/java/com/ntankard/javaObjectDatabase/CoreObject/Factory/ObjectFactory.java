package com.ntankard.javaObjectDatabase.CoreObject.Factory;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

import static com.ntankard.javaObjectDatabase.CoreObject.Factory.ObjectFactory.GeneratorMode.GENERATOR_ONLY;

public abstract class ObjectFactory<GeneratedType extends DataObject, GeneratorType extends DataObject> {

    /**
     * How the generator should behave in regards to multiple objects
     */
    public enum GeneratorMode {
        GENERATOR_ONLY, // The object can only be created by this generated. If it already exists throw an exception
        SINGLE,         // There can only be 1 of the objects in the system. If it already exists don't generate it
        MULTIPLE_NO_ADD,// There can be multiple objects in the system. If one or more exists don't make a new one
        MULTIPLE_ADD    // There can be multiple objects in the system. Make a new one no matter what
    }

    /**
     * The object type that will built
     */
    private final Class<GeneratedType> generatedType;

    /**
     * How the generator should behave in regards to multiple objects
     */
    private final GeneratorMode mode;

    /**
     * Constructor
     */
    public ObjectFactory(Class<GeneratedType> generatedType) {
        this(generatedType, GENERATOR_ONLY);
    }

    /**
     * Constructor
     */
    public ObjectFactory(Class<GeneratedType> generatedType, GeneratorMode mode) {
        this.generatedType = generatedType;
        this.mode = mode;
    }

    /**
     * Create the new object type
     *
     * @param generator The source invoking this method, the source of the new object
     */
    public abstract void generate(GeneratorType generator);

    /**
     * Get the object type this factory builds
     *
     * @return The object type this factory builds
     */
    public Class<GeneratedType> getGeneratedType() {
        return generatedType;
    }

    /**
     * Check if we should build another object based on the number that already exist and the mode. Will also throw exceptions if the database is in an invalid state
     *
     * @param existingObjectNum The number of GeneratedType currently in the database
     * @return True if a new one should be made
     */
    protected boolean shouldBuild(int existingObjectNum) {
        switch (mode) {
            case GENERATOR_ONLY:
                if (existingObjectNum != 0) {
                    throw new IllegalStateException("An object has already been created");
                }
                break;
            case SINGLE:
                if (existingObjectNum > 1) {
                    throw new IllegalStateException("More than 1 object exits");
                }
                if (existingObjectNum == 1) {
                    return false;
                }
                break;
            case MULTIPLE_NO_ADD:
                if (existingObjectNum != 0) {
                    return false;
                }
                break;
        }
        return true;
    }
}
